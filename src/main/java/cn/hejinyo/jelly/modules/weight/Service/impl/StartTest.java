package cn.hejinyo.jelly.modules.weight.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/3 0:50
 */
@Service
public class StartTest {
    @Autowired
    private WeightRoundService weightRoundService;

    public void test() {

        for (int i = 10; i < 30; i++) {
            int finalI = i;
            Thread test = new Thread(() -> {
                for (int j = finalI * 1000; j < (finalI * 1000) + 150; j++) {
                    weightRoundService.weater(j);
                }
            });
            test.start();
        }
    }

}
