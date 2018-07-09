package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * sys_config 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/15 22:57
 */
@Data
public class SysConfigEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 编号 config_id
     **/
    private Integer configId;

    /**
     * 配置名称 name
     **/
    private String name;

    /**
     * 配置编码 唯一主键 code
     **/
    private String code;

    /**
     * 配置值，关联配置值表数据 option_id
     **/
    private Integer optionId;

    /**
     * 数据类型 0：字符串 1：整型  2：浮点型  3：布尔  4：json对象 type
     **/
    private Integer type;

    /**
     * 状态 0：正常 1：禁用 status
     **/
    private Integer status;

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
     * 更新时间 update_time
     **/
    private Date updateTime;

    /**
     * 配置值
     **/
    private String value;

    /**
     * 配置说明
     **/
    private String label;

    /**
     * 配置下拉列表
     */
    private List<SysConfigOptionEntity> optionList;
}