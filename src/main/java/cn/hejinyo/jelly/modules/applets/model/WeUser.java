package cn.hejinyo.jelly.modules.applets.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * w_user 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/03/10 17:14
 */
@Data
public class WeUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号 user_id
     **/
    private Integer userId;

    /**
     * OpenId open_id
     **/
    private String openId;

    /**
     * 昵称 nick_name
     **/
    private String nickName;

    /**
     * 性别 gender
     **/
    private Byte gender;

    /**
     * 头像 avatar_url
     **/
    private String avatarUrl;

    /**
     * 国家 country
     **/
    private String country;

    /**
     * 省份 province
     **/
    private String province;

    /**
     * 城市 city
     **/
    private String city;

    /**
     * 创建日期 create_time
     **/
    private Date createTime;

    /**
     * 状态 0正常 1禁用 -1已删除 state
     **/
    private Integer state;
}