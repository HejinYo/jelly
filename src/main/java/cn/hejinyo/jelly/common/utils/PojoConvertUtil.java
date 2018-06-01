package cn.hejinyo.jelly.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.beanutils.BeanUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/22 21:24
 */
@Slf4j
public class PojoConvertUtil {
    private static Lock initLock = new ReentrantLock();

    private static Map<String, BeanCopier> beanCopierMap = new HashMap<>();


    /**
     * 将一个map映射成一个JavaBean
     */
    public static <T> T map2Bean(Map<String, Object> params, Class<T> beanClass) {
        try {
            T bean = beanClass.newInstance();
            BeanUtils.populate(bean, params);
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将一个bean映射成一个map
     */
    public static Map<String, Object> convertBean(Object bean) {
        try {
            Class type = bean.getClass();
            Map<String, Object> returnMap = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(type);

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (!"class".equals(propertyName)) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean);
                    // 不转换值为空的字段
                    if (result != null) {
                        returnMap.put(propertyName, result);
                    } /*else {
                        returnMap.put(propertyName, "");
                    }*/
                }
            }
            return returnMap;
        } catch (ReflectiveOperationException | IntrospectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化 BeanCopier
     */
    private static BeanCopier initCopier(Class source, Class target) {
        initLock.lock();
        BeanCopier find = beanCopierMap.get(source.getName() + "_" + target.getName());
        if (find != null) {
            initLock.unlock();
            return find;
        }
        BeanCopier beanCopier = BeanCopier.create(source, target, false);
        beanCopierMap.put(source.getName() + "_" + target.getName(), beanCopier);
        initLock.unlock();
        return beanCopier;
    }


    /**
     * 获取BeanCopier
     */
    private static BeanCopier getBeanCopier(Class source, Class target) {
        BeanCopier beanCopier = beanCopierMap.get(source.getClass().getName() + "_" + target.getName());
        if (beanCopier != null) {
            return beanCopier;
        }
        return initCopier(source, target);
    }


    /**
     * Pojo 类型转换（浅复制，字段名&类型相同则被复制）
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        BeanCopier beanCopier = getBeanCopier(source.getClass(), targetClass);
        try {
            T target = targetClass.newInstance();
            beanCopier.copy(source, target, null);
            return target;

        } catch (Exception e) {
            log.error("对象拷贝失败,{}", e);
            throw new RuntimeException("对象拷贝失败" + source + "_" + targetClass);
        }
    }

    /**
     * Pojo 类型转换（浅复制，字段名&类型相同则被复制）
     */
    public static <E> List<E> convert(List source, Class<E> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            if (source.isEmpty()) {
                return new ArrayList<E>();
            }
            List<E> result = new ArrayList<E>();

            for (Object each : source) {
                result.add(convert(each, targetClass));
            }
            return result;
        } catch (Exception e) {
            log.error("对象拷贝失败,{}", e);
            throw new RuntimeException("对象拷贝失败" + source + "_" + targetClass);
        }
    }


}
