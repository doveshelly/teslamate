package cn.doveshelly.teslamate.api;

import cn.hutool.core.util.ObjectUtil;

/**
 * 封装Controller返回对象
 *
 */
public class CommonResult<T> {
    private String rtCode;
    private String rtMessage;
    private String sign;
    private T rtData;

    public CommonResult() {
    }

    public CommonResult(String rtCode, String rtMessage) {
        this.rtCode = rtCode;
        this.rtMessage = rtMessage;
    }

    public CommonResult(String rtCode, String rtMessage, T rtData) {
        this.rtCode = rtCode;
        this.rtMessage = rtMessage;
        this.rtData = rtData;
    }

    public boolean isSuccess() {
        return ObjectUtil.equal(rtCode, ReturnCode.SUCCESS.getCode());
    }

    /**
     * 成功返回结果
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<T>(ReturnCode.SUCCESS.getCode(),
                ReturnCode.SUCCESS.getMessage());
    }

    /**
     * 成功返回结果
     *
     * @param content 业务数据
     */
    public static <T> CommonResult<T> success(T content) {
        return new CommonResult<T>(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMessage(), content);
    }

    /**
     * 成功返回结果
     *
     * @param content 获取的数据
     * @param message 提示信息
     */
    public static <T> CommonResult<T> success(T content, String message) {
        return new CommonResult<T>(ReturnCode.SUCCESS.getCode(), message, content);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(IMessageCode errorCode) {
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static <T> CommonResult<T> failed(IMessageCode errorCode, String message) {
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ReturnCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ReturnCode.FAILED);
    }

    public String getRtCode() {
        return rtCode;
    }

    public String getRtMessage() {
        return rtMessage;
    }

    public String getSign() {
        return sign;
    }

    public T getRtData() {
        return rtData;
    }

    public void setRtCode(String rtCode) {
        this.rtCode = rtCode;
    }

    public void setRtMessage(String rtMessage) {
        this.rtMessage = rtMessage;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setRtData(T rtData) {
        this.rtData = rtData;
    }
}
