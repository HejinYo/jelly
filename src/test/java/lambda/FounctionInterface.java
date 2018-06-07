package lambda;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 函数式接口
 *
 * @author : heshuangshuang
 * @date : 2018/6/7 10:06
 */
public class FounctionInterface {
    @Autowired
    private static Testclass testclass;

    /**
     * 点名
     */
    public static void main(String[] args) {
        List<String> demoList = Arrays.asList("小明", "Zing", "阿三", "小红", "赵日天");
        rollCall(demoList,
                name -> name.startsWith("小"),
                name -> {
                    String rate = name + "是单身狗!";
                    System.out.println(rate);
                });
        List<String> test = test(1L, aLong -> testclass.s());

    }

    private static List<String> test(Long id, Test<Long, List<String>> test) {
        return test.check(id);
    }

    /**
     * 点名逻辑
     */
    private static void rollCall(List<? extends String> list, Checker checker, Out out) {
        for (String name : list) {
            if (checker.check(name)) {
                out.achievement(name);
            }
        }
    }
}

class Testclass {
    List<String> s() {
        return new ArrayList<>();
    }
}

/**
 * 函数式接口，抽象方法只能有一个，除了Object中的方法以外
 */
@FunctionalInterface
interface Test<T, R> {
    R check(T t);
}

/**
 * 函数式接口，抽象方法只能有一个，除了Object中的方法以外
 */
@FunctionalInterface
interface Checker<T extends String> {
    boolean check(T t);
}

@FunctionalInterface
interface Out<T> {
    void achievement(T t);
}
