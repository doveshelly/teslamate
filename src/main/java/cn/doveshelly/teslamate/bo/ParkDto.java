package cn.doveshelly.teslamate.bo;

import lombok.Data;

@Data
public class ParkDto {
    private Long id;
    private String location;
    private String dateTime;
    private String consumption; // 消耗电量
    private String cost;        // 预估费用
    private String batteryChange;
    private String rangeChange;
}