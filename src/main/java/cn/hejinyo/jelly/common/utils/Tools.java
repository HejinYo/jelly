package cn.hejinyo.jelly.common.utils;

import cn.hejinyo.jelly.common.consts.Constant;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jodd.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 字符串，字符，日期 工具类
 *
 * @author HejinYo hejinyo@gmail.com
 */
public class Tools {

    /**
     * BASE64编码
     *
     * @param str String
     */
    public static String base64Encode(String str) {
        return Base64.encodeToString(str);
    }

    /**
     * BASE64编码
     *
     * @param arr byte[]
     */
    public static String base64Encode(byte[] arr) {
        return Base64.encodeToString(arr);
    }

    /**
     * BASE64解码
     */
    public static String base64Decoder(String str) {
        return Base64.decodeToString(str);
    }

    /**
     * 数据库密码加密
     */
    public static String[] encryptDBPassword(String password) {
        String path = "C:/java/tools/JDK/";
        String druid = "druid-1.0.16.jar com.alibaba.druid.filter.config.ConfigTools ";
        String fileInfo = "java -cp " + path + druid + password + " ;exit;";
        String pw[] = new String[3];
        try {
            Process proc = Runtime.getRuntime().exec(fileInfo);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader in = new BufferedReader(is);
            //privateKey
            pw[1] = in.readLine();
            //publicKey
            pw[2] = in.readLine();
            //encryptPassword
            pw[0] = in.readLine();
            in.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pw;
    }

    /**
     * HmacSHA256 加密，返回 base64编码
     *
     * @param key     密钥
     * @param content 内容
     */
    public static String hmacSHA256Digest(String key, String content) {
        try {
            //创建加密对象
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            //处理密钥Key
            SecretKey secret_key = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA256");
            //初始化加密
            sha256_HMAC.init(secret_key);
            //加密内容
            byte[] doFinal = sha256_HMAC.doFinal(content.getBytes("utf-8"));
            //16进制内容
            // byte[] hexB = new Hex().encode(doFinal);
            return base64Encode(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * byte 转 字符串
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString();
    }

    /**
     * 创建jwt token
     * 默认失效时间
     */
    public static String createToken(int userId, String username, String password) {
        return createToken(Constant.JWT_EXPIRES_DEFAULT, userId, username, password);
    }

    /**
     * 创建jwt token
     * expires n小时后失效
     */
    public static String createToken(int expires, int userId, String username, String password) {
        String token = "";
        try {
            token = JWT.create().
                    withIssuedAt(new Date()).
                    withExpiresAt(new Date(System.currentTimeMillis() + (expires * 60 * 60 * 1000))).
                    withClaim(Constant.JWT_TOKEN_USERID, userId).
                    withClaim(Constant.JWT_TOKEN_USERNAME, username).
                    sign(Algorithm.HMAC256(password));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 获得token 信息
     */
    public static String tokenInfoStr(String token, String key) {
        return JWT.decode(token).getClaim(key).asString();
    }

    public static Integer tokenInfoInt(String token, String key) {
        return JWT.decode(token).getClaim(key).asInt();
    }

    /**
     * 验证token 有效性
     */
    public static void verifyToken(String token, String password) throws UnsupportedEncodingException {
        JWT.require(Algorithm.HMAC256(password)).build().verify(token);
    }


    /**************************** 测试 *********************************/
    public static void main(String agrs[]) {
        String[] str = encryptDBPassword("hj269957961");
        for (String s : str) {
            System.out.println(s);
        }
    }
}
