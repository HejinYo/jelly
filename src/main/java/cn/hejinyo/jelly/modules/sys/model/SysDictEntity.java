package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_dict 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/15 23:59
 */
@Data
public class SysDictEntity implements Serializable {
    /**
     * 编号 id
     **/
    private Integer id;

    /**
     * 字典名称 name
     **/
    private String name;

    /**
     * 字典编码 code
     **/
    private String code;

    /**
     * 数据类型 0：字符串 1：整型  2：浮点型  3：布尔  4：json对象 type
     **/
    private Integer type;

    /**
     * 创建人编号 create_id
     **/
    private Integer createId;

    /**
     * 创建日期 create_time
     **/
    private Date createTime;

    /**
     * 更新人 update_id
     **/
    private Integer updateId;

    /**
     * 更新日期 update_time
     **/
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}