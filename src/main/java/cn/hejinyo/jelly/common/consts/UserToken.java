package cn.hejinyo.jelly.common.consts;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/19 18:54
 * @Description :
 */
public enum UserToken {
    /**
     * 用户名,username
     */
    USERNAME("use"),
    /**
     * 用户id,userid
     */
    USERID("uid");
    private final String value;

    private UserToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
