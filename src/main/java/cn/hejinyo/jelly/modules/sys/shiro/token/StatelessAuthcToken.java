package cn.hejinyo.jelly.modules.sys.shiro.token;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:07
 * @Description :
 */
public class StatelessAuthcToken extends BaseToken {

    private String username;
    private String userToken;
    private Object currentUser;

    public StatelessAuthcToken(String tokenType, String username, String userToken, Object currentUser) {
        super.tokenType = tokenType;
        this.username = username;
        this.userToken = userToken;
        this.currentUser = currentUser;
    }

    public StatelessAuthcToken(String username, String userToken, Object currentUser) {
        this(StatelessAuthcToken.class.getSimpleName(), username, userToken, currentUser);
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
