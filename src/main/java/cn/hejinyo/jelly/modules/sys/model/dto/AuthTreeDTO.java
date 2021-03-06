package cn.hejinyo.jelly.modules.sys.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 授权树数据对象
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date 2017/9/27 23:32
 */
@Data
public class AuthTreeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 树编号
     */
    private Integer id;

    /**
     * 名称
     */
    private String label;

    /**
     * 权限编号
     */
    private Integer permId;

    /**
     * 0资源/1权限
     */
    private Integer type;

    /**
     * 禁止选择
     */
    private Boolean disabled;

    /**
     * 子节点
     */
    private List<AuthTreeDTO> children;

    public AuthTreeDTO(Integer id, String label, Boolean disabled, Integer type, List<AuthTreeDTO> children) {
        this.id = id;
        this.label = label;
        this.disabled = disabled;
        this.type = type;
        this.children = children;
    }


    public AuthTreeDTO(Integer id, String label, Integer permId, Integer type) {
        this.id = id;
        this.label = label;
        this.permId = permId;
        this.type = type;
    }
}
