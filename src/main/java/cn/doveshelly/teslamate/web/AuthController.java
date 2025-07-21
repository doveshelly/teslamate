package cn.doveshelly.teslamate.web;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.doveshelly.teslamate.api.CommonResult;
import cn.doveshelly.teslamate.bo.LoginBo;
import cn.doveshelly.teslamate.bo.WeChatSessionResponse;
import cn.doveshelly.teslamate.config.RateLimit;
import cn.doveshelly.teslamate.config.SignatureRequired;
import cn.doveshelly.teslamate.utils.CommonConfig;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class AuthController {

    public static final String PHONE = "10000";

    @Autowired
    private CommonConfig commonConfig;

    /**
     * 登录接口
     *
     * @return
     */
    @RateLimit(limit = 3, duration = 10)
    @SignatureRequired
    @RequestMapping("/user/login")
    public CommonResult login(String code) {
        log.info("根据code查询用户请求参数phone:{}.", code);
        String openId = getOpenId(code);
        if (StrUtil.isEmpty(openId)) {
            return CommonResult.failed("获取用户信息失败，请稍后重试！");
        }

        if (!StrUtil.equals(commonConfig.getOpenId(), openId)) {
            return CommonResult.failed("用户不存在");
        }
        LoginBo loginBo = new LoginBo();
        loginBo.setPhone(PHONE);

        StpUtil.login(PHONE);

        //生成token
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        loginBo.setTokenInfo(tokenInfo);
        return CommonResult.success(loginBo);
    }

    private String getOpenId(String code) {
        // 1. 构建请求微信 API 的 URL
        try {
            String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    commonConfig.getAppId(), commonConfig.getAppSecret(), code
            );

            String responseStr = HttpUtil.get(url);
            WeChatSessionResponse wechatResponse = JSONUtil.toBean(responseStr, WeChatSessionResponse.class);

            // 检查微信服务器是否返回错误
            if (wechatResponse == null || wechatResponse.getErrcode() != null && wechatResponse.getErrcode() != 0) {
                String errorMsg = wechatResponse != null ? wechatResponse.getErrmsg() : "微信服务调用失败";
                System.err.println("微信登录凭证校验失败: " + errorMsg);
                return null;
            }

            String openid = wechatResponse.getOpenid();
            log.info("获取到openId:{},code:{}", openid, code);
            return openid;
        } catch (Exception e) {
            log.error("获取opneId失败.", e);
            return null;
        }
    }


}