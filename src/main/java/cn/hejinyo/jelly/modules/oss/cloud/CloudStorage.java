package cn.hejinyo.jelly.modules.oss.cloud;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * 云存储配置信息
 *
 * @author hejinyo
 * @date 2017/9/26 11:00
 */
@Data
public class CloudStorage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 七牛绑定的域名
     */
    @NotBlank(message = "七牛绑定的域名不能为空")
    @URL(message = "七牛绑定的域名格式不正确")
    private String qiniuDomain;

    /**
     * 七牛路径前缀
     */
    private String qiniuPrefix;

    /**
     * 七牛ACCESS_KEY
     */
    @NotBlank(message = "七牛AccessKey不能为空")
    private String qiniuAccessKey;

    /**
     * 七牛SECRET_KEY
     */
    @NotBlank(message = "七牛SecretKey不能为空")
    private String qiniuSecretKey;

    /**
     * 七牛存储空间名
     */
    @NotBlank(message = "七牛空间名不能为空")
    private String qiniuBucketName;

    /**
     * 覆盖上传的key
     */
    private String key;

}
