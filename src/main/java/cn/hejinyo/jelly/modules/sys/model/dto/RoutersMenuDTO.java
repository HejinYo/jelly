package cn.hejinyo.jelly.modules.sys.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
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
public class RoutersMenuDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 资源编号 res_id
     **/
    @JSONField(serialize = false)
    private Integer resId;

    /**
     * 父节点 parent_id
     **/
    @JSONField(serialize = false)
    private Integer parentId;

    /**
     * 路由name 对应 resCode
     **/
    private String name;

    /**
     * 路由title 对应 res_name
     **/
    private String title;

    /**
     * 显示图标 icon
     **/
    private String icon;

    /**
     * 子菜单
     */
    private List<RoutersMenuDTO> children;


}
