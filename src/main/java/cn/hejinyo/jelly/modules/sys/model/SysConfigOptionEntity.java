package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_config_option 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/15 22:58
 */
@Data
public class SysConfigOptionEntity implements Serializable {
    /**
     * 配置信息主键 option_id
     **/
    private Integer optionId;

    /**
     * 配置编码 code
     **/
    private String code;

    /**
     * 显示标签 label
     **/
    private String label;

    /**
     * 配置值 json格式 value
     **/
    private String value;

    /**
     * 说明 description
     **/
    private String description;

    /**
     * 创建人编号 create_id
     **/
    private Integer createId;

    /**
     * 创建日期 create_time
     **/
    private Date createTime;

    /**
     * 更新人编号 update_id
     **/
    private Integer updateId;

    /**
     * 更新日期 update_time
     **/
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}