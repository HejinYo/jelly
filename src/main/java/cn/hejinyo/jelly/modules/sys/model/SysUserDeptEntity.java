package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_user_dept 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 18:34
 */
@Data
public class SysUserDeptEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 编号 id
     **/
    private Integer id;

    /**
     * 用户编号 user_id
     **/
    private Integer userId;

    /**
     * 部门编号 dept_id
     **/
    private Integer deptId;

    /**
     * 创建人编号 create_id
     **/
    private Integer createId;

    /**
     * 创建时间 create_time
     **/
    private Date createTime;
}