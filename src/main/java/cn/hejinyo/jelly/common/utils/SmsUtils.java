package cn.hejinyo.jelly.common.utils;

import cn.hejinyo.jelly.common.exception.InfoException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;

import java.io.IOException;

/**
 * 发送短信验证码工具
 *
 * @author : heshuangshuang
 * @date : 2018/7/28 15:41
 */
public class SmsUtils {
    /**
     * 短信应用SDK AppID 1400开头
     */
    private static int appid = 1400116390;

    /**
     * 短信应用SDK AppKey
     */
    private static String appkey = "af0a76c4aa7ab5fd3236810b04de0bf2";

    /**
     * 发送模板验证码
     * {"result":1014,"errmsg":"package format error, sdkappid not have this tpl_id","ext":""}
     * {"result":0,"errmsg":"OK","ext":"","sid":"8:Cec7OojxNILq1xQ8AYn20180728","fee":1}
     */
    private static boolean send(String phone, int templateId, String[] params) {
        try {
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            // 签名参数未提供或者为空时，会使用默认签名发送短信
            SmsSingleSenderResult result = ssender.sendWithParam("86", phone, templateId, params, "", "", "");
            if (result.result == 0) {
                return true;
            }
            throw new InfoException("短信发送失败：" + result.errMsg);
        } catch (HTTPException | JSONException | IOException e) {
            // HTTP响应码错误
            e.printStackTrace();
            throw new InfoException("短信发送异常：" + e.getMessage());
        }
    }

    /**
     * 发送登录验证码
     */
    public static boolean sendLogin(String phone, String code) {
        String[] params = {code, "5"};
        return send(phone, 164420, params);
    }

    /**
     * 发送操作验证码
     */
    public static boolean sendOperate(String phone, String code) {
        String[] params = {code, "5"};
        return send(phone, 164410, params);
    }
}
