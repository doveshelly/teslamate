package cn.doveshelly.teslamate.bo;

import lombok.Data;

@Data
public class ChargeSessionDTO {
    private Integer id;
    private String location;
    private String type;
    private String dateTime;
    private String chargeAmount; // 充电量 (kWh)
    private String cost;         // 费用 (元)
    private String avgPower;     // 平均功率 (kW)
    private String batteryChange; // 电池电量变化
    private String rangeAdded;    // 增加的续航 (km)
}