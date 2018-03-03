package cn.hejinyo.jelly.modules.sys.model;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 14:11
 */
@Data
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色编号
     */
    private Integer roleId;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String roleCode;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDescription;

    /**
     * 排序号
     */
    private Integer seq;

    /**
     * 状态  0：正常；1：锁定；-1：禁用(删除)
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
}
