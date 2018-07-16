package cn.hejinyo.jelly.modules.sys.model;

import cn.hejinyo.jelly.common.validator.RestfulValid;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 22:14
 */
@Data
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * key key
     **/
    @NotBlank(message = "参数名不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String key;

    /**
     * value value
     **/
    @NotBlank(message = "参数值不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String value;

    /**
     * 状态   0：隐藏   1：显示 status
     **/
    private Integer status;

    /**
     * 备注 remark
     **/
    private String remark;

}
