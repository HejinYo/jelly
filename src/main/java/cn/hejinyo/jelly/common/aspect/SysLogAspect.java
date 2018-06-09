package cn.hejinyo.jelly.common.aspect;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.utils.JsonUtil;
import cn.hejinyo.jelly.common.utils.WebUtils;
import cn.hejinyo.jelly.modules.sys.model.SysLog;
import cn.hejinyo.jelly.modules.sys.service.SysLogService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.session.StandardSessionFacade;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;


/**
 * 系统日志，切面处理类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:19
 */
@Aspect
@Component
@Slf4j
public class SysLogAspect {

    @Resource
    private SysLogService sysLogService;

    /*@Pointcut("execution(public * cn.hejinyo.skyeboot.controller.SysLogController.add(..))")*/
    @Pointcut("@annotation(cn.hejinyo.jelly.common.annotation.SysLogger)")
    public void logPointCut() {

    }

    @Before("logPointCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        SysLog sysLog = new SysLog();

        //获取request
        HttpServletRequest request = WebUtils.getHttpServletRequest();

        //注解上的描述
        SysLogger syslog = signature.getMethod().getAnnotation(SysLogger.class);
        sysLog.setOperation(syslog != null ? syslog.value() : "");

        //请求的方法名
        sysLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName() + "()");

        //请求的参数
        Object[] parameter = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        HashMap<String, String> paramMap = new HashMap<>(16);
        for (int i = 0; i < parameter.length; i++) {
            if (!(parameter[i] instanceof ServletRequest) && !(parameter[i] instanceof StandardSessionFacade)) {
                paramMap.put(paramNames[i], JsonUtil.toJson(parameter[i]));
            }
        }
        if (paramMap.size() > 0) {
            sysLog.setParams(JsonUtil.toJson(paramMap));
        }

        //设置IP地址
        sysLog.setIp(WebUtils.getIpAddr(request));

        //日志时间
        sysLog.setCreateTime(new Date());

        if (SecurityUtils.getSubject().isAuthenticated()) {
            //用户名
            String username = ShiroUtils.getLoginUser().getUserName();
            sysLog.setUserName(username);
        } else {
            sysLog.setUserName(sysLog.getIp());
        }

        //保存系统日志
        sysLogService.save(sysLog);
    }

}
