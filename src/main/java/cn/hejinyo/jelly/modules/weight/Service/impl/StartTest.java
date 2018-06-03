package cn.hejinyo.jelly.modules.weight.Service.impl;

import cn.hejinyo.jelly.common.utils.JsonUtil;
import cn.hejinyo.jelly.modules.weight.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/3 0:50
 */
@Service
public class StartTest {
    @Autowired
    private WeightRoundService weightRoundService;


    public void test() {

        List<User> list = new ArrayList<>();
        list.add(new User("A", 1005L, 5, 0));
        list.add(new User("B", 2003L, 3, 0));
        list.add(new User("C", 3003L, 3, 0));
        list.add(new User("D", 4002L, 2, 0));
        list.add(new User("E", 5001L, 1, 0));

        List<User> log = new ArrayList<>();

        for (int i = 1; i < 2; i++) {
            int finalI = i;
            Thread test = new Thread(() -> {
                for (int j = finalI * 1000; j < (finalI * 1000) + 300; j++) {
                    log.add(weightRoundService.weater(j));
                }
            });
            test.start();
        }


        try {
            Thread.sleep(180000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.forEach(value -> {
            switch (value.getUserName()) {
                case "A":
                    list.get(0).setSeq(list.get(0).getSeq() + 1);
                    break;
                case "B":
                    list.get(1).setSeq(list.get(1).getSeq() + 1);
                    break;
                case "C":
                    list.get(2).setSeq(list.get(2).getSeq() + 1);
                    break;
                case "D":
                    list.get(3).setSeq(list.get(3).getSeq() + 1);
                    break;
                case "E":
                    list.get(4).setSeq(list.get(4).getSeq() + 1);
                    break;
                default:
            }

        });
        list.forEach(value -> {
            System.out.println(JsonUtil.toJson(value));
        });
    }

}
