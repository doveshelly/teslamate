package cn.doveshelly.teslamate.exception;


import cn.doveshelly.teslamate.api.IMessageCode;

/**
 * 自定义API异常
 *
 */
public class ApiException extends RuntimeException {
    private IMessageCode errorCode;

    public ApiException(IMessageCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public IMessageCode getErrorCode() {
        return errorCode;
    }
}
