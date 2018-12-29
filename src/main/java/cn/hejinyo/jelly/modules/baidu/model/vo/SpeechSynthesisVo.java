package cn.hejinyo.jelly.modules.baidu.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/12/29 18:59
 */
@Data
public class SpeechSynthesisVo implements Serializable {

    private static long serialVersionUID = 1L;

    /**
     * 语音内容
     */
    private String content;
    /**
     * 发音人选择, 0为普通女声，1为普通男生，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女声
     */
    private int pronunciation;
    /**
     * 语速，取值0-15，默认为5中语速
     */
    private int speechSpeed;
    /**
     * 音调，取值0-15，默认为5中语调
     */
    private int tone;
    /**
     * 音量，取值0-9，默认为5中音量
     */
    private int volume;
    /**
     * 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
     */
    private int fileType;
}
