package cn.hejinyo.jelly.modules.wechat.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * wechat_joke 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/03/06 23:03
 */
@Data
public class WechatJoke implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private String content;

    private Integer typeid;

    private Integer hits;

    private Date dateandtime;
}