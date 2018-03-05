package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 14:17
 */
@Data
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    public SysUserRole() {
    }

    public SysUserRole(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    /**
     * 编号 id
     **/
    private Integer id;

    /**
     * 用户编号 user_id
     **/
    private Integer userId;

    /**
     * 角色编号 role_id
     **/
    private Integer roleId;
}
