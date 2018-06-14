package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_user_role 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/14 23:44
 */
@Data
public class SysUserRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;
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

    /**
     * 创建人员 create_id
     **/
    private Integer createId;

    /**
     * 创建时间 create_time
     **/
    private Date createTime;
}