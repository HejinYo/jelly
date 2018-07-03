package cn.hejinyo.jelly.modules.sys.model;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * sys_permission 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 16:47
 */
@Data
public class SysPermissionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限编号 perm_id
     **/
    private Integer permId;

    /**
     * 资源编号 res_id
     **/
    @NotNull(message = "所属资源不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private Integer resId;

    /**
     * 资源名称 res_name
     **/
    private String resName;

    /**
     * 权限名称 perm_name
     **/
    @NotNull(message = "权限名称不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String permName;

    /**
     * 权限编码 perm_code
     **/
    @NotNull(message = "权限编码不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String permCode;

    /**
     * 状态 0：正常；1：禁用 state
     **/
    private Integer state;

    /**
     * 创建人员 create_id
     **/
    private Integer createId;

    /**
     * 创建时间 create_time
     **/
    private Date createTime;

    /**
     * 修改人员 update_id
     **/
    private Integer updateId;

    /**
     * 修改时间 update_time
     **/
    private Date updateTime;
}