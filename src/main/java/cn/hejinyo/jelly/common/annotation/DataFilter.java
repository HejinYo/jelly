package cn.hejinyo.jelly.common.annotation;

import cn.hejinyo.jelly.common.consts.Constant;

import java.lang.annotation.*;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/13 21:10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {

    /**
     * 查询参数下标
     */
    int index() default 0;

    /**
     * 表的别名
     */
    String tableAlias() default "";

    /**
     * curDept：所在部门
     * subDept：子部门
     * allDept：所有部门
     */
    Constant.Dept dept() default Constant.Dept.CUR_DEPT;

    /**
     * true：没有本部门数据权限，也能查询本人数据
     */
    boolean user() default true;

    /**
     * 部门ID
     */
    String deptId() default "dept_id";

    /**
     * 用户ID
     */
    String userId() default "user_id";
}
