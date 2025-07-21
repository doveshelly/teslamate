package cn.doveshelly.teslamate.bo;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Data;

@Data
public class LoginBo {

    private String phone;
    private SaTokenInfo tokenInfo;
}
