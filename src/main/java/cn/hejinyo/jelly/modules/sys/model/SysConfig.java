package cn.hejinyo.jelly.modules.sys.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 22:14
 */
@Data
public class SysConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "参数名不能为空")
    private String key;

    @NotBlank(message = "参数值不能为空")
    private String value;

    private String remark;

    /**
     * 状态   0：隐藏   1：显示 status
     */
    private Byte status;
}
