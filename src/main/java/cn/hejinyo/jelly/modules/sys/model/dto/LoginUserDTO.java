package cn.hejinyo.jelly.modules.sys.model.dto;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 14:48
 */
@Data
public class LoginUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号 user_id
     **/
    private Integer userId;

    /**
     * 用户昵称 nick_name
     **/
    private String nickName;

    /**
     * 用户名 user_name
     **/
    @NotBlank(message = "用户名不能为空", groups = {RestfulValid.POST.class})
    private String userName;

    /**
     * 密码
     */
    @JSONField(serialize = false)
    @NotBlank(message = "密码不能为空", groups = {RestfulValid.POST.class})
    private String userPwd;

    /**
     * 盐
     */
    @JSONField(serialize = false)
    private String userSalt;

    /**
     * 头像 avatar
     **/
    private String avatar;

    /**
     * 邮箱 email
     **/
    private String email;

    /**
     * 手机号 phone
     **/
    private String phone;

    /**
     * 最后登录IP login_ip
     **/
    private String loginIp;

    /**
     * 最后登录时间 login_time
     **/
    private Date loginTime;

    /**
     * 用户状态 0：正常；1：禁用 state
     **/
    @JSONField(serialize = false)
    private Integer state;

    /**
     * 用户登录token
     */
    private String userToken;

}
