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
public class UserMenuDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 菜单编号
     */
    private Integer mid;

    /**
     * 资源编码
     */
    private String redCode;

    /**
     * 菜单名字
     */
    private String mname;

    /**
     * 菜单地址
     */
    private String murl;

    /**
     * 上级菜单编号
     */
    private Integer pid;

    /**
     * 菜单级别
     */
    private Integer mlevel;

    /**
     * 菜单显示顺序
     */
    private Integer seq;

    /**
     * 菜单图标
     */
    private String micon;

    private List<UserMenuDTO> children;
}
