package cn.hejinyo.jelly.modules.sys.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_log 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/13 23:29
 */
@Data
public class SysLogEntity implements Serializable {
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
     * 创建人员 create_id
     **/
    private Integer createId;

    /**
     * 创建时间 create_time
     **/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}