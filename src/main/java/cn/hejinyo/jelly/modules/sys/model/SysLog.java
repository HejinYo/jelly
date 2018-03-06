package cn.hejinyo.jelly.modules.sys.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统日志实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:19
 */
@Data
public class SysLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 日志编号 id
     **/
    private Integer id;

    /**
     * 用户名 user_name
     **/
    private String userName;

    /**
     * 用户操作 operation
     **/
    private String operation;

    /**
     * 请求方法 method
     **/
    private String method;

    /**
     * 请求参数 params
     **/
    private String params;

    /**
     * IP地址 ip
     **/
    private String ip;

    /**
     * 创建时间 create_time
     **/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
