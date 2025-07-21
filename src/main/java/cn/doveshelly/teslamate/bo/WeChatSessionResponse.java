package cn.doveshelly.teslamate.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 映射微信 jscode2session 接口返回的 JSON
 */
public class WeChatSessionResponse {
    private String openid;
    @JsonProperty("session_key") // Jackson 注解，用于匹配 JSON 中的下划线命名
    private String sessionKey;
    private String unionid;
    private Integer errcode;
    private String errmsg;

    // Getter 和 Setter
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}