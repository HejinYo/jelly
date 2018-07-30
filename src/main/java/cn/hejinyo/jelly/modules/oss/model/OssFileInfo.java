package cn.hejinyo.jelly.modules.oss.model;

import lombok.Data;

import java.util.Date;

/**
 * @author : heshuangshuang
 * @date : 2018/7/30 20:33
 */
@Data
public final class OssFileInfo {
    /**
     * 文件名
     */
    private String key;
    /**
     * 文件hash值
     */
    private String hash;
    /**
     * 文件大小，单位：字节
     */
    private long size;
    /**
     * 文件上传时间，单位为：100纳秒
     */
    private Date putTime;
    /**
     * 文件的mimeType
     */
    private String mimeType;
}

