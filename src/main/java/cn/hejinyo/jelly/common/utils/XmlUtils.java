package cn.hejinyo.jelly.common.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/12/30 0:59
 */
public class XmlUtils {

    /**
     * XML转对象
     *
     * @param clazz 对象类
     * @param xml   xml字符串
     * @param <T>   T
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseFromXml(Class<T> clazz, String xml) {
        //创建解析XML对象
        XStream xStream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypes(new Class[]{clazz});
        //处理注解
        xStream.processAnnotations(clazz);
        //将XML字符串转为bean对象
        return (T) xStream.fromXML(xml);
    }

    /**
     * 对象转xml
     */
    public static String toXml(Object obj) {
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(obj.getClass());
        return xStream.toXML(obj);
    }

}