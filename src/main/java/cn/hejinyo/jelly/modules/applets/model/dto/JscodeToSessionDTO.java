package cn.hejinyo.jelly.modules.applets.model.dto;

import lombok.Data;

/**
 * code 换取 session_key
 *
 * @author : heshuangshuang
 * @date : 2018/3/10 17:20
 */
@Data
public class JscodeToSessionDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 微信OpenId
     */
    private String openid;
    /**
     * 会话密钥
     */
    private String sessionKey;
    /**
     * 微信产品唯一id
     */
    private String unionid;
    /**
     * 错误码
     */
    private Integer errcode;
    /**
     * 错误信息
     */
    private String errmsg;

}
