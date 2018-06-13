package cn.hejinyo.jelly.common.consts;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/5 18:25
 * @Description :
 */
public enum StatusCode {
    FAILURE(-1, "失败"),
    SUCCESS(0, "成功"),
    PARAMETER_ERROR(1001, "提交参数不符合规范"),
    USERTOKEN_PARAMETER_ERROR(1002, "请登录后继续操作"),
    LOGIN_FAILURE(1110, "登录失败"),
    LOGIN_EXCESSIVE_ATTEMPTS(1111, "登录失败次数太多，请30分钟后再试"),
    LOGIN_VERIFYCODE_ERROR(1112, "验证码错误"),
    LOGIN_PASSWORD_ERROR(1113, "用户密码错误"),
    LOGIN_USER_NOEXIST(1114, "用户不存在"),
    LOGIN_USER_LOCK(1115, "用户已被禁用"),
    USER_UNAUTHORIZED(1116, "无此操作权限"),
    TOKEN_OVERDUE(1130, "登录过期，请重新登录"),
    TOKEN_OUT(1131, "登录失效，帐号在其他地方登录"),
    TOKEN_FAULT(1132, "未登录，无权限访问"),
    DATABASE_DUPLICATEKEY(2001, "数据库中已存在该记录"),
    DATABASE_SAVE_FAILURE(2002, "添加失败"),
    DATABASE_UPDATE_FAILURE(2003, "数据未修改"),
    DATABASE_DELETE_FAILURE(2004, "删除失败"),
    DATABASE_SELECT_FAILURE(2005, "数据不存在"),
    DATABASE_NOT_CHANGE(2006, "未作任何修改"),
    DATABASE_UPDATE_LOOP(2100, "节点回环，不允许修改"),
    DATABASE_UPDATE_ROOT(2101, "根节点，不允许修改"),
    DATABASE_DELETE_ROOT(2102, "根节点，不允许删除"),
    DATABASE_UPDATE_CHILD(2103, "存在子节点，不允许修改"),
    DATABASE_DELETE_CHILD(2104, "存在子节点，不允许删除"),
    DATABASE_NO_FATHER(2105, "父节点不存在"),
    PERMISSION_UNAUTHORIZED(2201, "不允许越权操作"),
    PERMISSION_ONESELF(2202, "不允许对自己进行操作");

    private final int code;
    private final String msg;

    private StatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
