package cn.hejinyo.jelly.modules.sys.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 当前用户实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 14:48
 */
@Data
public class CurrentUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 密码
     */
    @JSONField(serialize = false)
    private String userPwd;

    /**
     * 盐
     */
    @JSONField(serialize = false)
    private String userSalt;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    /**
     * 用户状态 0：正常；1：锁定；-1：禁用(删除)
     */
    private Integer state;

    /**
     * 用户可用菜单
     */
    private List<UserMenuDTO> userMenu;

    /**
     * 用户登录token
     */
    private String userToken;

}
