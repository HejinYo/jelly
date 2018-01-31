package cn.hejinyo.jelly.modules.oss.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 文件上传
 *
 * @author :heshuangshuang
 * @date :2018/1/20 10:10
 */
@Data
public class SysOss implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    //URL地址
    private String url;
    //创建时间
    private Date createDate;
}
