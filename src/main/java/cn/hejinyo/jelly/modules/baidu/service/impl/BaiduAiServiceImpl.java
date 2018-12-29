package cn.hejinyo.jelly.modules.baidu.service.impl;

import cn.hejinyo.jelly.modules.baidu.common.ConnUtil;
import cn.hejinyo.jelly.modules.baidu.common.TokenHolder;
import cn.hejinyo.jelly.modules.baidu.model.vo.SpeechSynthesisVo;
import cn.hejinyo.jelly.modules.baidu.service.BaiduAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/12/29 18:49
 */
@Service
public class BaiduAiServiceImpl implements BaiduAiService {

    @Value("${baidu.ai.appKey}")
    private String appKey;

    @Value("${baidu.ai.secretKey}")
    private String secretKey;

    /**
     * text 的内容为"欢迎使用百度语音合成"的urlencode,utf-8 编码
     */
    private final String text = "欢迎使用百度语音";

    /**
     * 发音人选择, 0为普通女声，1为普通男生，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女声
     */
    private final int PER = 0;
    /**
     * 语速，取值0-15，默认为5中语速
     */
    private final int SPD = 5;
    /**
     * 音调，取值0-15，默认为5中语调
     */
    private final int PIT = 5;
    /**
     * 音量，取值0-9，默认为5中音量
     */
    private final int VOL = 5;

    /**
     * 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
     */
    private final int AUE = 3;

    /**
     * 可以使用https
     */
    public final String url = "http://tsn.baidu.com/text2audio";

    private String cuid = "1234567JAVA";

    /**
     * 百度ai语音合成
     */
    @Override
    public String speechSynthesis(SpeechSynthesisVo speechSynthesisVo) throws IOException {
        TokenHolder holder = new TokenHolder(appKey, secretKey, TokenHolder.ASR_SCOPE);
        holder.refresh();
        String token = holder.getToken();

        // 此处2次urlencode， 确保特殊字符被正确编码
        String params = "tex=" + ConnUtil.urlEncode(ConnUtil.urlEncode(text));
        params += "&PER=" + speechSynthesisVo;
        params += "&SPD=" + SPD;
        params += "&PIT=" + PIT;
        params += "&VOL=" + VOL;
        params += "&cuid=" + cuid;
        params += "&tok=" + token;
        params += "&AUE=" + AUE;
        params += "&lan=zh&ctp=1";
        // 反馈请带上此url，浏览器上可以测试
        System.out.println(url + "?" + params);
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
        printWriter.write(params);
        printWriter.close();
        String contentType = conn.getContentType();
        if (contentType.contains("audio/")) {
            byte[] bytes = ConnUtil.getResponseBytes(conn);
            String format = getFormat(AUE);
            // 打开mp3文件即可播放
            File file = new File("result." + format);
            FileOutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
            System.out.println("audio file write to " + file.getAbsolutePath());
        } else {
            System.err.println("ERROR: content-type= " + contentType);
            String res = ConnUtil.getResponseString(conn);
            System.err.println(res);
        }
        return null;
    }


    private void run() throws IOException {
    }

    /**
     * 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
     */
    private String getFormat(int aue) {
        String[] formats = {"mp3", "pcm", "pcm", "wav"};
        return formats[aue - 3];
    }

    public static void main(String[] args) throws IOException {
        (new BaiduAiServiceImpl()).run();
    }

}
