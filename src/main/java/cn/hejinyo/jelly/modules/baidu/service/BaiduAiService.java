package cn.hejinyo.jelly.modules.baidu.service;

import cn.hejinyo.jelly.modules.baidu.model.vo.SpeechSynthesisVo;

import java.io.IOException;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/12/29 18:49
 */
public interface BaiduAiService {

    /**
     * 百度ai语音合成
     */
    String speechSynthesis(SpeechSynthesisVo speechSynthesisVo) throws IOException;
}
