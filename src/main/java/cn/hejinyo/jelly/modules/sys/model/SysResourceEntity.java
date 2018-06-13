package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * sys_resource 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 16:46
 */
@Data
public class SysResourceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源编号 res_id
     **/
    private Integer resId;

    /**
     * 父节点 parent_id
     **/
    private Integer parentId;

    /**
     * 资源类型（数据字典）：0：文件夹 ； 1：菜单； type
     **/
    private Integer type;

    /**
     * 资源名称 res_name
     **/
    private String resName;

    /**
     * 资源编码 res_code
     **/
    private String resCode;

    /**
     * 显示图标 icon
     **/
    private String icon;

    /**
     * 排序号 seq
     **/
    private Integer seq;

    /**
     * 状态(数据字典） 0：正常；1：禁用 state
     **/
    private Integer state;

    /**
     * 创建人员 create_id
     **/
    private Integer createId;

    /**
     * 创建时间 create_time
     **/
    private Date createTime;

    /**
     * 修改人员 update_id
     **/
    private Integer updateId;

    /**
     * 修改时间 update_time
     **/
    private Date updateTime;

    /**
     * 子资源
     */
    private List<SysResourceEntity> children;
}