package cn.hejinyo.jelly.modules.sys.model;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * sys_user 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/08 23:20
 */
@Data
public class SysUserEntity implements Serializable {

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
     * 用户密码 user_pwd
     **/
    @JSONField(serialize = false)
    @NotBlank(message = "密码不能为空", groups = {RestfulValid.POST.class})
    private String userPwd;

    /**
     * 用户盐 user_salt
     **/
    @JSONField(serialize = false)
    private String userSalt;

    /**
     * 头像 avatar
     **/
    private String avatar;

    /**
     * 邮箱 email
     **/
    @Email(message = "邮箱格式不正确", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String email;

    /**
     * 手机号 phone
     **/
    @Pattern(regexp = "^$|^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$", message = "手机格式不正确", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String phone;

    /**
     * 最后登录IP login_ip
     **/
    private String loginIp;

    /**
     * 最后登录时间 login_time
     **/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    /**
     * 用户状态 0：正常；1：禁用； state
     **/
    private Integer state;

    /**
     * 注册时间 create_time
     **/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人员 create_id
     **/
    private Integer createId;

    /**
     * 修改时间 update_time
     **/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 更新人编号 update_id
     **/
    private Integer updateId;

    /**
     * 拥有的角色ID列表
     */
    private List<Integer> roleIdList;

    /**
     * 拥有的角色列表
     */
    private List<SysRoleEntity> roleList;

    /**
     * 所在的部门ID列表
     */
    private List<Integer> deptIdList;

    /**
     * 所在的部门列表
     */
    private List<SysDeptEntity> deptList;

}