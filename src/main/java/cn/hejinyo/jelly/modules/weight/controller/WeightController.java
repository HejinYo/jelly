package cn.hejinyo.jelly.modules.weight.controller;

import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.modules.weight.Service.impl.StartTest;
import cn.hejinyo.jelly.modules.weight.Service.impl.WeightRoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/2 20:50
 */
@RestController
@RequestMapping("/app")
public class WeightController {
    @Autowired
    private WeightRoundService weightRoundService;
    @Autowired
    private StartTest startTest;

    @GetMapping("/weigth")
    public Result test() {
        weightRoundService.weater(1);
        return Result.ok();
    }

    @GetMapping("/startTest")
    public Result StartTest() {
        startTest.test();
        return Result.ok();
    }
}
