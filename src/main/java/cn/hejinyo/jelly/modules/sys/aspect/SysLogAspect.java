package cn.hejinyo.jelly.modules.sys.aspect;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.utils.JsonUtils;
import cn.hejinyo.jelly.common.utils.WebUtils;
import cn.hejinyo.jelly.modules.sys.model.SysLog;
import cn.hejinyo.jelly.modules.sys.service.SysLogService;
import cn.hejinyo.jelly.modules.sys.utils.ShiroUtils;
import org.apache.catalina.session.StandardSessionFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/**
 * 系统日志，切面处理类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:19
 * @Description :
 */
@Aspect
@Component
public class SysLogAspect {
    private final static Logger logger = LoggerFactory.getLogger(SysLogAspect.class);

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
        StringBuilder params = new StringBuilder();
        Object[] parameter = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < parameter.length; i++) {
            if (!(parameter[i] instanceof ServletRequest) && !(parameter[i] instanceof StandardSessionFacade)) {
                String parm = JsonUtils.toJSONString(parameter[i]);
                params.append(paramNames[i]).append(":").append(parm).append(";");
            }
        }
        sysLog.setParams(params.toString());

        //设置IP地址
        sysLog.setIp(WebUtils.getIpAddr(request));

        //日志时间
        sysLog.setCreateTime(new Date());

       /*
        //method POST GET PUT
        String method = request.getMethod();*/

        //用户名
        //Optional<String> username = Optional.ofNullable(ShiroUtils.getCurrentUser().getUserName());
        //sysLog.setUserName(username.orElse("outline"));
        if (ShiroUtils.getSubject().isAuthenticated()) {
            sysLog.setUserName(ShiroUtils.getCurrentUser().getUserName());
        } else {
            sysLog.setUserName("visitor");
        }

        logger.debug("SysLogger={}", "[" + request.getRequestURL().toString() + "]" + JsonUtils.toJSONString(syslog));
        //保存系统日志
        sysLogService.save(sysLog);
    }

}
