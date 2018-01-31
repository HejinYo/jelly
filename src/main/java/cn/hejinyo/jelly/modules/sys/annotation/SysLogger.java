package cn.hejinyo.jelly.modules.sys.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogger {
    String value() default "";
}
