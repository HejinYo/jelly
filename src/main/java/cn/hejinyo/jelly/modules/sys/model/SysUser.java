package cn.hejinyo.jelly.modules.sys.model;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 14:48
 */
@Data
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    private Integer userId;

    /**
     * 用户名称
     */
    @NotBlank(message = "用户名不能为空", groups = {RestfulValid.POST.class})
    private String userName;

    /**
     * 密码相关不允许序列化
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
     * 头像地址
     */
    private String avatar;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^$|^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$", message = "手机格式不正确", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
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
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人员ID
     */
    private Integer createId;

    /**
     * 用户角色
     */
    private Integer roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /*
    角色名称
     */
    private String roleName;
}
