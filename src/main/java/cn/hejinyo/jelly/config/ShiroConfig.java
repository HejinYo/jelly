package cn.hejinyo.jelly.config;

import cn.hejinyo.jelly.modules.sys.shiro.filter.SysAuthcFilter;
import cn.hejinyo.jelly.modules.sys.shiro.filter.URLFilter;
import cn.hejinyo.jelly.modules.sys.shiro.realm.ModularRealm;
import cn.hejinyo.jelly.modules.sys.shiro.realm.SysAuthcRealm;
import cn.hejinyo.jelly.modules.sys.shiro.subject.SubjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.*;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/17 23:22
 */
@Configuration
@Slf4j
public class ShiroConfig {

    /**
     * SecurityManager 安全管理器 有多个Realm,可使用'realms'属性代替
     */
    @Bean("securityManager")
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 禁用session 的subjectFactory
        securityManager.setSubjectFactory(new SubjectFactory());
        // 禁用使用Sessions 作为存储策略的实现，但它没有完全地禁用Sessions,所以需要配合context.setSessionCreationEnabled(false);
        ((DefaultSessionStorageEvaluator) ((DefaultSubjectDAO) securityManager.getSubjectDAO()).getSessionStorageEvaluator()).setSessionStorageEnabled(false);
        // 自定义realms
        List<Realm> realms = new ArrayList<>();
        realms.add(statelessAuthcRealm());
        securityManager.setRealms(realms);
        // 自定义 ModularRealm
        securityManager.setAuthenticator(defaultModularRealm(realms));
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        // 注入自定义拦截器,注意拦截器自注入问题
        Map<String, Filter> filters = new HashMap<>(16);
        filters.put("url", new URLFilter());
        //访问验证拦截器
        filters.put("authc", authcFilter());
        factoryBean.setFilters(filters);

        // 拦截器链
        Map<String, String> filterMap = new LinkedHashMap<>();

        filterMap.put("/", "anon");
        filterMap.put("/login/**", "anon");
        filterMap.put("/logout", "anon");
        filterMap.put("/favicon.ico", "anon");
        filterMap.put("/druid/**", "anon");

        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/v2/api-docs", "anon");

        filterMap.put("/test/**", "anon");
        filterMap.put("/wechat/**", "anon");
        // spring cloud 健康检查
        filterMap.put("/info/**", "anon");
        // 开放授权
        filterMap.put("/oauth/**", "anon");

        filterMap.put("/**", "url,authc");
        factoryBean.setFilterChainDefinitionMap(filterMap);

        log.debug("注入ShiroFilterFactoryBean成功");
        return factoryBean;
    }


    /**
     * 配置使用自定义认证器，可以实现多Realm认证，并且可以指定特定Realm处理特定类型的验证
     */
    @Bean
    public ModularRealm defaultModularRealm(List<Realm> realms) {
        ModularRealm authenticator = new ModularRealm();
        authenticator.setRealms(realms);
        return authenticator;
    }

    /**
     * token验证Realm
     */
    @Bean
    public SysAuthcRealm statelessAuthcRealm() {
        return new SysAuthcRealm();
    }

    /**
     * 在方法中 注入  securityManager ，进行代理控制,相当于调用SecurityUtils.setSecurityManager(securityManager)
     */
    @Bean
    public MethodInvokingFactoryBean getMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(securityManager());
        return factoryBean;
    }

    @Bean
    public SysAuthcFilter authcFilter() {
        return new SysAuthcFilter();
    }

    /**
     * 解决自定义拦截器混乱问题
     */
    @Bean
    public FilterRegistrationBean registrationAuthcFilterBean(SysAuthcFilter authcFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(authcFilter);
        //取消自动注册功能 Filter自动注册,不会添加到FilterChain中.
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 注解RequiresPermissions 需要此配置，否侧注解不生效，和上面aop搭配才有效,这里会出问题，导致controller失效，还没有解决方案
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


}
