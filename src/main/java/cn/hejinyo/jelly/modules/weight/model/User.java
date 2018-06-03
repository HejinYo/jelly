package cn.hejinyo.jelly.modules.weight.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/2 20:07
 */
@Data
public class User implements Serializable {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户权重
     */
    private Integer weigth;

    /**
     * 用户排序号
     */
    private Integer seq;

    public User(String userName, Long userId, Integer weigth, Integer seq) {
        this.userName = userName;
        this.userId = userId;
        this.weigth = weigth;
        this.seq = seq;
    }
}
