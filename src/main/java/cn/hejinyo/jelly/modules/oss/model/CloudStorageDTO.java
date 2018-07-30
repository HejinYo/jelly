package cn.hejinyo.jelly.modules.oss.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 云存储配置信息
 *
 * @author hejinyo
 * @date 2017/9/26 11:00
 */
@Data
public class CloudStorageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类型 0：阿里云；1：腾讯云；2：七牛云
     */
    private Integer type;

    /**
     * 绑定的域名
     */
    private String domain;

    /**
     * 不指定文件夹，默认路径前缀
     */
    private String prefix;

    /**
     * ACCESS_KEY
     */
    private String accessKey;

    /**
     * SECRET_KEY
     */
    private String secretKey;

    /**
     * 存储空间名
     */
    private String bucketName;

    /**
     * 地区
     */
    private String region;

}
