package edu.dhu.auction.web.util;

public enum ResultStatus {
    // @formatter:off
    //---------------------------------------------------------------------
    //                          SUCCESS CODE
    //---------------------------------------------------------------------

    SUCCESS                                     (0, "操作成功"),
    LOGIN_SUCCESS                               (0, "登陆成功"),
    REGISTER_SUCCESS                            (0, "注册成功"),

    //---------------------------------------------------------------------
    //                          FAILURE CODE
    //---------------------------------------------------------------------

    FAILURE                                     (1, "操作失败"),
    /*                      Authentication Error                         */
    NOT_LOGIN                                   (2, "未登录"),
    ACCESS_DENIED                               (2, "未登录或权限错误"),
    LOGIN_FAILURE                               (2, "登录失败"),
    PASSWORD_ERROR                              (2, "用户名或密码错误"),
    ACCOUNT_NOT_EXIST                           (2, "账号不存在"),
    /*                           Token Error                              */
    JWT_ERROR                                   (3, "身份令牌错误"),
    /*                           Corda Error                              */
    CORDA_ERROR                                 (4, "Corda错误"),
    /*                        Verification Error                          */
    VERIFY_FAILURE                              (5, "验证失败"),
    ERROR_PARAMETER                             (5, "参数类型错误"),
    UNREADABLE_PARAMETER                        (5, "参数读取失败"),
    /*                          Request Error                             */
    REQUEST_METHOD_ERROR                        (6, "请求方法错误"),
    /*                          Service Error                             */
    OSS_ERROR                                   (7, "OSS错误"),
    ROCKET_MQ_ERROR                             (7, "RocketMQ错误"),
    ALIYUN_ERROR                                (7, "阿里云错误"),
    /*                           Other Error                              */
    SYSTEM_ERROR                                (10, "系统错误");
    // @formatter:on

    private final Integer code;
    private final String msg;

    ResultStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
