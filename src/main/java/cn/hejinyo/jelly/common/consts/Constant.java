package cn.hejinyo.jelly.common.consts;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 23:12
 */
public class Constant {
    /**
     * 超级管理员ID
     */
    public static final Integer SUPER_ADMIN = 1;
    /**
     * JWT token 用户名
     */
    public static final String JWT_TOKEN_USERNAME = "use";
    /**
     * JWT token 用户编号
     */
    public static final String JWT_TOKEN_USERID = "uid";
    /**
     * jwtToken 超时时间 小时
     */
    public static final int JWT_EXPIRES_DEFAULT = 12;
    /**
     * 用户登录token的续命时长 秒
     */
    public static final int USER_TOKEN_EXPIRE = 1800;
    /**
     * 用户权限的有效时长 秒
     */
    public static final int USER_PERM_EXPIRE = 600;
    /**
     * 请求头 中token的key
     */
    public static final String AUTHOR_PARAM = "Authorization";


    /**
     * 菜单类型
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private Integer value;

        MenuType(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public boolean equals(Integer value) {
            return this.getValue().equals(value);
        }
    }

    /**
     * 定时任务状态
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private Integer value;

        ScheduleStatus(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public boolean equals(Integer value) {
            return this.getValue().equals(value);
        }
    }
}

