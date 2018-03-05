package cn.hejinyo.jelly.modules.sys.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 资源实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 14:32
 */
@Data
public class SysResource implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 资源编号
     */
    private Integer resId;

    /**
     * 资源类型号
     */
    private String resType;

    /**
     * 资源编码
     */
    private String resCode;

    /**
     * 资源名称
     */
    private String resName;

    /**
     * 父资源ID
     */
    private Integer resPid;

    /**
     * 父资源名称
     */
    private String resPname;

    /**
     * 资源级别
     */
    private Integer resLevel;

    /**
     * 显示图标
     */
    private String resIcon;

    /**
     * 排序号
     */
    private Integer seq;

    /**
     * 状态 0：正常；1：锁定；-1：禁用(删除)
     */
    private Integer state;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人员ID
     */
    private Integer createId;

    /**
     * 资源编号
     */
    private List<SysResource> children;
}
