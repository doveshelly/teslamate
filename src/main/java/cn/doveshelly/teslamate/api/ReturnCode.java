package cn.doveshelly.teslamate.api;

/**
 * 枚举了一些常用API操作码
 *
 */
public enum ReturnCode implements IMessageCode {

    /**
     * 通用：成功
     */
    SUCCESS("0000", "操作成功"),
    USER_UN_BIND("9991", "用户还未绑定"),
    TOKEN_INVALID("9992", "token失效"),

    /**
     * 通用：失败
     */
    FAILED("9999", "操作失败");

    /**
     * 代码
     */
    private final String code;

    /**
     * 消息
     */
    private final String message;

    ReturnCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
