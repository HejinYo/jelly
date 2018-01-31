package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 14:19
 * @Description : 角色资源关联类，授权的是资源权限（两张表）
 */
@Data
public class SysRoleResource implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色授权编号
     */
    private Integer id;
    /**
     * 角色编号
     */
    private Integer roleId;
    /**
     * 资源编号
     */
    private Integer resId;
    /**
     * 权限编号
     */
    private Integer permId;
}
