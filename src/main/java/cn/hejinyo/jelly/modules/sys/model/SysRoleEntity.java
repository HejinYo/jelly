package cn.hejinyo.jelly.modules.sys.model;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * sys_role 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/14 20:33
 */
@Data
public class SysRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 角色编号 role_id
     **/
    private Integer roleId;

    /**
     * 角色编码 role_code
     **/
    @NotBlank(message = "角色编码不能为空", groups = {RestfulValid.POST.class})
    private String roleCode;

    /**
     * 角色名称 role_name
     **/
    @NotBlank(message = "角色名称不能为空", groups = {RestfulValid.POST.class})
    private String roleName;

    /**
     * 角色描述 description
     **/
    private String description;

    /**
     * 排序号 seq
     **/
    private Integer seq;

    /**
     * 状态 0：正常；1：锁定；-1：禁用(删除) state
     **/
    @NotNull(message = "状态不能为空", groups = {RestfulValid.POST.class})
    private Integer state;

    /**
     * 创建人员 create_id
     **/
    private Integer createId;

    /**
     * 创建时间 create_time
     **/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改人员 update_id
     **/
    private Integer updateId;

    /**
     * 修改时间 update_time
     **/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 拥有权限列表
     */
    private List<Integer> permIdList;
}