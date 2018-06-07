package cn.hejinyo.jelly.modules.sys.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:07
 */
public class StatelessLoginToken implements AuthenticationToken {

    private String username;
    private String password;

    public StatelessLoginToken(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return getUsername();
    }

    @Override
    public Object getCredentials() {
        return getPassword();
    }
}
