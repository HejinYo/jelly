package cn.hejinyo.jelly.modules.sys.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date 2017/9/27 23:32
 */
@Data
public class RolePermissionTreeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 资源编号
     */
    private Integer resId;

    /**
     * 资源编码
     */
    private String resCode;

    /**
     * 资源名称
     */
    private String resName;

    /**
     * 权限编号
     */
    private Integer permId;

    /**
     * 权限编码
     */
    private String permCode;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 权限名称
     */
    private String type;

    /**
     * 子资源
     */
    @JSONField(deserialize = false)
    private List<RolePermissionTreeDTO> childrenRes;
}
