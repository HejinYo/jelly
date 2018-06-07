package cn.hejinyo.jelly.modules.sys.shiro.realm;

import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 账户登录多次失败锁定
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
public class CredentialsMatcher extends HashedCredentialsMatcher {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        String cacheName = RedisKeys.getLoginRecordCacheKey(username);
        AtomicInteger retryCount = redisUtils.get(cacheName, AtomicInteger.class);
        //如果缓存中没有，就为用户创建一个
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            redisUtils.set(cacheName, retryCount, 1800);
        }
        //每次执行登录增加一次，大于5次，抛出异常
        if (retryCount.incrementAndGet() > 5) {
            if (6 == retryCount.get()) {
                redisUtils.set(cacheName, retryCount, 1800);
                redisUtils.delete(RedisKeys.getTokenCacheKey(username));
            } else {
                redisUtils.set(cacheName, retryCount);
            }
            throw new ExcessiveAttemptsException();
        }
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //认证成功，清除登陆执行次数
            redisUtils.delete(cacheName);
        } else {
            redisUtils.set(cacheName, retryCount);
        }
        return matches;
    }

}
