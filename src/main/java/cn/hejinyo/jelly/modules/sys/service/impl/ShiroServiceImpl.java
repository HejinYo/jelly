package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.modules.sys.dao.ShiroDao;
import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/3/20 22:48
 */
@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    private ShiroDao shiroDao;

    @Override
    public CurrentUserDTO getCurrentUser(String userName) {
        return shiroDao.getCurrentUser(userName);
    }

    /**
     * 查找用户编号对应的角色编码字符串
     */
    @Override
    public Set<String> getUserRoleSet(int userId) {
        return shiroDao.getUserRoleSet(userId);
    }

    @Override
    public Set<String> getUserPermisSet(int userId) {
        //管理员获得所有权限
        if (userId == Constant.SUPER_ADMIN) {
            return shiroDao.getAllPermisSet();
        }
        return shiroDao.getUserPermisSet(userId);
    }

}
