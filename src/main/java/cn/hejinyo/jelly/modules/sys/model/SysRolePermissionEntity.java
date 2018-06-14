package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_role_permission 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/14 21:57
 */
@Data
public class SysRolePermissionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色资源编号 id
     **/
    private Integer id;

    /**
     * 角色编号 role_id
     **/
    private Integer roleId;

    /**
     * 权限编号 perm_id
     **/
    private Integer permId;

    /**
     * 创建人编号 create_id
     **/
    private Integer createId;

    /**
     * 创建时间 create_time
     **/
    private Date createTime;
}