package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_dict_option 实体类
 * 
 * @author : HejinYo   hejinyo@gmail.com 
 * @date : 2018/06/15 23:59
 */
@Data
public class SysDictOptionEntity implements Serializable {
    private Integer id;

    /**
	 * 字典编码 code
	 **/
    private String code;

    /**
	 * 字典键 value
	 **/
    private String value;

    /**
	 * 字典标签 label
	 **/
    private String label;

    /**
	 * 排序号 seq
	 **/
    private Integer seq;

    /**
	 * 注释 description
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
	 * 更新人 update_id
	 **/
    private Integer updateId;

    /**
	 * 更新时间 update_time
	 **/
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}