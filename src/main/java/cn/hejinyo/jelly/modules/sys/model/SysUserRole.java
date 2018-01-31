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

    /**
     * 用户角色ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 角色ID
     */
    private Integer roleId;

}
