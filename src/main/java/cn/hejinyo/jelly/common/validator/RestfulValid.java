package cn.hejinyo.jelly.common.validator;

import javax.validation.GroupSequence;

/**
 * 实体校验接口
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/25 21:47
 */
public interface RestfulValid {
    interface GET {
    }

    interface DELETE {
    }

    interface PUT {
    }

    interface POST {
    }

    interface PATCH {
    }

    /**
     * 定义校验顺序，如果POST组失败，则PUT组不会再校验
     */
    @GroupSequence({POST.class, PUT.class})
    interface POSTANDPUT {

    }
}
