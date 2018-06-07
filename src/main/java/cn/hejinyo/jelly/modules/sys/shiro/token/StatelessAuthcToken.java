package cn.hejinyo.jelly.modules.sys.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:07
 */
public class StatelessAuthcToken implements AuthenticationToken {

    private String username;
    private String userToken;
    private Object currentUser;

    public StatelessAuthcToken(String username, String userToken, Object currentUser) {
        this.username = username;
        this.userToken = userToken;
        this.currentUser = currentUser;
    }

    public Object getCurrentUser() {
        return currentUser;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return userToken;
    }
}
