package cn.hejinyo.jelly.modules.sys.model;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * sys_dept 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 18:31
 */
@Data
public class SysDeptEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门编号 dept_id
     **/
    private Integer deptId;

    /**
     * 父节点 parent_id
     **/
    @NotNull(message = "父节点不能为空", groups = {RestfulValid.POST.class,RestfulValid.PUT.class})
    private Integer parentId;

    /**
     * 部门名称 dept_name
     **/
    @NotBlank(message = "部门名称不能为空", groups = {RestfulValid.POST.class,RestfulValid.PUT.class})
    private String deptName;

    /**
     * 排序号 seq
     **/
    @NotNull(message = "排序号不能为空", groups = {RestfulValid.POST.class,RestfulValid.PUT.class})
    private Integer seq;

    /**
     * 描述 description
     **/
    private String description;

    /**
     * 状态 0：正常 1：禁用 state
     **/
    @NotNull(message = "状态不能为空", groups = {RestfulValid.POST.class,RestfulValid.PUT.class})
    private Integer state;

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
     * 子部门
     */
    private List<SysDeptEntity> children;
}