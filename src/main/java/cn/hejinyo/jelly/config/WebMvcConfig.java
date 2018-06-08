package cn.hejinyo.jelly.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * SpringMVC配置
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/16 23:38
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 注入fastJson
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //fast参数配置
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //是否需要格式化
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        // 自定义时间格式
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //禁用循环引用检测
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        //返回字符串格式
        List<MediaType> fastMediaTypes = new ArrayList<MediaType>() {{
            add(MediaType.APPLICATION_JSON_UTF8);
        }};
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastConverter);
        super.configureMessageConverters(converters);
    }

    /**
     * 配置静态访问资源
     *
     */
/*    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //自定义项目内目录
        //registry.addResourceHandler("/my/**").addResourceLocations("classpath:/my/");
        //指向外部目录
        registry.addResourceHandler("/my/**").addResourceLocations("file:E:/my/");
        super.addResourceHandlers(registry);
    }*/

    /**
     * 以前要访问一个页面需要先创建个Controller控制类，在写方法跳转到页面
     * 在这里配置后就不需要那么麻烦了，直接访问http://localhost:8080/toLogin就跳转到login.html页面了
     *
     */
  /*  @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/toLogin").setViewName("login");
        super.addViewControllers(registry);
    }*/

    /**
     * 拦截器
     */
  /*  @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        //registry.addInterceptor(authcInterceptor()).addPathPatterns("/**").excludePathPatterns("/login", "/logout", "/error", "/wechat/**");
        super.addInterceptors(registry);
    }*/
    /*@Bean
    public AuthcInterceptor authcInterceptor() {
        return new AuthcInterceptor();
    }*/


}