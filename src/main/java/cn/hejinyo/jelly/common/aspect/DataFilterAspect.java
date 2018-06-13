package cn.hejinyo.jelly.common.aspect;

import cn.hejinyo.jelly.common.annotation.DataFilter;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.StringUtils;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.service.SysUserDeptService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/13 21:09
 */
@Aspect
@Component
public class DataFilterAspect {
    @Autowired
    private SysUserDeptService sysUserDeptService;

    @Pointcut("@annotation(cn.hejinyo.jelly.common.annotation.DataFilter)")
    public void dataFilterCut() {

    }

    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataFilter dataFilter = signature.getMethod().getAnnotation(DataFilter.class);

        Object params = point.getArgs()[dataFilter.index()];
        if (params instanceof Map) {
            LoginUserDTO user = ShiroUtils.getLoginUser();
            //如果不是超级管理员，则进行数据过滤
            if (!Constant.SUPER_ADMIN.equals(user.getUserId())) {
                Map map = (Map) params;
                map.put(Constant.SQL_FILTER, getSQLFilter(user, dataFilter));
            }
            return;
        }

        throw new InfoException("数据权限接口，只能是Map类型参数");
    }

    /**
     * 获取数据过滤的SQL
     */
    private String getSQLFilter(LoginUserDTO user, DataFilter dataFilter) {
        //获取表的别名
        String tableAlias = dataFilter.tableAlias();
        if (StringUtils.isNotBlank(tableAlias)) {
            tableAlias += ".";
        }

        List<Integer> deptList;
        switch (dataFilter.dept()) {
            case ALL_DEPT:
                deptList = sysUserDeptService.getAllDeptIdListByUserId(user.getUserId());
                break;
            case SUB_DEPT:
                deptList = sysUserDeptService.getSubDeptIdListByUserId(user.getUserId());
                break;
            default:
                deptList = sysUserDeptService.getCurDeptIdListByUserId(user.getUserId());
        }
        Set<Integer> deptIdList = new HashSet<>(deptList);

        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if (deptIdList.size() > 0) {
            sqlFilter.append(tableAlias).append(dataFilter.deptId()).append(" in(").append(StringUtils.join(deptIdList, ",")).append(")");
        }

        //没有本部门数据权限，也能查询本人数据
        if (dataFilter.user()) {
            if (deptIdList.size() > 0) {
                sqlFilter.append(" or ");
            }
            sqlFilter.append(tableAlias).append(dataFilter.userId()).append("=").append(user.getUserId());
        }

        sqlFilter.append(")");

        return sqlFilter.toString();
    }
}
