package cn.hejinyo.jelly.modules.sys.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户菜单实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 18:09
 */
@Data
public class UserMenuDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 菜单编号 res_id
     **/
    private Integer resId;

    /**
     * 父节点 parent_id
     **/
    private Integer parentId;

    /**
     * 菜单名称 res_name
     **/
    private String resName;

    /**
     * 菜单编码 res_code
     **/
    private String resCode;

    /**
     * 显示图标 icon
     **/
    private String icon;

    /**
     * 排序号 seq
     **/
    private Integer seq;

    /**
     * 子菜单
     */
    private List<UserMenuDTO> children;
}
