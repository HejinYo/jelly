package cn.hejinyo.jelly.modules.baidu.service.impl;

import cn.hejinyo.jelly.modules.baidu.common.ConnUtil;
import cn.hejinyo.jelly.modules.baidu.common.TokenHolder;
import cn.hejinyo.jelly.modules.baidu.model.vo.SpeechSynthesisVo;
import cn.hejinyo.jelly.modules.baidu.service.BaiduAiService;
import cn.hejinyo.jelly.modules.oss.factory.OSSFactory;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/12/29 18:49
 */
@Service
@Slf4j
public class BaiduAiServiceImpl implements BaiduAiService {

    @Value("${baidu.ai.appKey}")
    private String appKey;

    @Value("${baidu.ai.secretKey}")
    private String secretKey;

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
        // 设置默认值
        speechSynthesisVo.setPronunciation(Optional.ofNullable(speechSynthesisVo.getPronunciation()).orElse(0));
        speechSynthesisVo.setSpeechSpeed(Optional.ofNullable(speechSynthesisVo.getSpeechSpeed()).orElse(5));
        speechSynthesisVo.setTone(Optional.ofNullable(speechSynthesisVo.getTone()).orElse(5));
        speechSynthesisVo.setVolume(Optional.ofNullable(speechSynthesisVo.getVolume()).orElse(5));
        speechSynthesisVo.setFileType(Optional.ofNullable(speechSynthesisVo.getFileType()).orElse(3));

        log.debug("speechSynthesisVo==>{}", speechSynthesisVo);

        TokenHolder holder = new TokenHolder(appKey, secretKey, TokenHolder.ASR_SCOPE);
        holder.refresh();
        String token = holder.getToken();

        // 此处2次urlencode， 确保特殊字符被正确编码
        String params = "tex=" + ConnUtil.urlEncode(ConnUtil.urlEncode(speechSynthesisVo.getContent()));
        params += "&PER=" + speechSynthesisVo.getPronunciation();
        params += "&SPD=" + speechSynthesisVo.getSpeechSpeed();
        params += "&PIT=" + speechSynthesisVo.getTone();
        params += "&VOL=" + speechSynthesisVo.getVolume();
        params += "&cuid=" + cuid;
        params += "&tok=" + token;
        params += "&AUE=" + speechSynthesisVo.getFileType();
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
            // 上传到云存储
            String format = getFormat(speechSynthesisVo.getFileType());
            String key = "speechSynthesis/" + ShiroUtils.getLoginUser().getUserName() + "/" + LocalDateTime.now().toString() + "." + format;
            String avatarUrl = OSSFactory.build().upload(bytes, key);
            System.out.println("上传到云存储 =>" + avatarUrl);
        } else {
            System.err.println("ERROR: content-type= " + contentType);
            String res = ConnUtil.getResponseString(conn);
            System.err.println(res);
        }
        return null;
    }

    /**
     * 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
     */
    private String getFormat(int aue) {
        String[] formats = {"mp3", "pcm", "pcm", "wav"};
        return formats[aue - 3];
    }


}
