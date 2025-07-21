package cn.doveshelly.teslamate.exception;


import cn.dev33.satoken.exception.NotLoginException;
import cn.doveshelly.teslamate.api.CommonResult;
import cn.doveshelly.teslamate.api.ReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public CommonResult handle(Exception e) {
        LOGGER.warn("系统异常,message:{}.", e.getMessage());
        return CommonResult.failed("系统异常:" + e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = NotLoginException.class)
    public CommonResult notLogin(Exception e) {
        LOGGER.warn("当前token校验失败,message:{}.", e.getMessage());
        return CommonResult.failed(ReturnCode.TOKEN_INVALID);
    }

}
