package cn.hejinyo.jelly.modules.baidu.controller;

import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.modules.baidu.model.vo.SpeechSynthesisVo;
import cn.hejinyo.jelly.modules.baidu.service.BaiduAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/12/29 18:48
 */
@RestController
@RequestMapping("/baidu/ai/")
public class BaiduAiController {

    @Autowired
    private BaiduAiService baiduAiService;

    /**
     * 百度ai语音合成
     */
    @PostMapping("/speechSynthesis")
    public Result speechSynthesis(@RequestBody SpeechSynthesisVo speechSynthesisVo) throws IOException {
        return Result.ok(baiduAiService.speechSynthesis(speechSynthesisVo));
    }
}
