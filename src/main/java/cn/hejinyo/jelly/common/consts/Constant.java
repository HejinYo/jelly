package cn.hejinyo.jelly.common.consts;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 23:12
 */
public class Constant {
    /**
     * 数据权限过滤
     */
    public static final String SQL_FILTER = "sqlFilter";
    /**
     * 超级管理员ID
     */
    public static final Integer SUPER_ADMIN = 1;
    /**
     * 树根节点ID
     */
    public static final Integer TREE_ROOT = 1;
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

    public enum Dept {
        /**
         * 所在部门
         */
        CUR_DEPT("curDept"),
        /**
         * 子部门
         */
        SUB_DEPT("subDept"),
        /**
         * 所有部门
         */
        ALL_DEPT("allDept");

        private String value;

        Dept(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

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
     * 通用状态
     */
    public enum Status {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 禁用
         */
        DISABLE(1);

        private Integer value;

        Status(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public boolean equals(Integer value) {
            return value.equals(this.value);
        }
    }

    /**
     * 数据类型
     * 0：字符串 1：整型  2：浮点型  3：布尔  4：json对象
     */
    public enum DataType {
        /**
         * 字符串
         */
        STRING(0),
        /**
         * 整型
         */
        INTEGER(1),
        /**
         * 浮点型
         */
        DOUBLE(2),
        /**
         * 布尔
         */
        BOOLEAN(3),
        /**
         * json对象
         */
        JSON(4);

        private Integer value;

        DataType(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public boolean equals(Integer value) {
            return value.equals(this.value);
        }
    }
}

