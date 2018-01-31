package cn.hejinyo.jelly.modules.sys.model.dto;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author : HejinYo   hejinyo@gmail.com     2017/9/27 20:16
 * @apiNote :
 */
@Data
public class RoleResourceDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色授权编号
     */
    private Integer id;

    /**
     * 资源编号
     */
    private Integer resId;

    /**
     * 角色编号
     */
    private Integer roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色编码
     */
    private String resName;

    /**
     * 权限编号 perm_id
     */
    private Integer permId;

    /**
     * 资源编码 res_code
     */
    @NotBlank(message = "资源编码不能为空", groups = {RestfulValid.POST.class})
    private String resCode;

    /**
     * 权限编码 perm_code
     */
    @NotBlank(message = "权限编码不能为空", groups = {RestfulValid.POST.class})
    private String permCode;

    /**
     * 权限名称 perm_name
     */
    @NotBlank(message = "权限名称不能为空", groups = {RestfulValid.POST.class})
    private String permName;
}
