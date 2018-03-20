package cn.hejinyo.jelly.modules.sys.shiro.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.servlet.AdviceFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
@Slf4j
public class URLFilter extends AdviceFilter {

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        log.debug("请求的URL:" + contextPath + uri);
        return Boolean.TRUE;
    }

}
