package cn.doveshelly.teslamate.utils;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@ToString
public class CommonConfig {
    @Value("${sys.config.batteryCapacity}")
    public double batteryCapacity;

    @Value("${sys.config.costPerKwh}")
    public double costPerKwh;

    @Value("${sys.config.fastChargePowerThresholdKw}")
    public double fastChargePowerThresholdKw;

    @Value("${sys.config.ratedConsumptionKwh}")
    public double ratedConsumptionKwh;

    @Value("${sys.config.appId}")
    public String appId;

    @Value("${sys.config.appSecret}")
    public String appSecret;

    @Value("${sys.config.openId}")
    public String openId;


    @Value("${sys.config.signSecret}")
    public String signSecret;

    @Value("${sys.config.baiduKey}")
    public String baiduKey;

    @Value("${sys.config.webhookToken}")
    public String webhookToken;


}
