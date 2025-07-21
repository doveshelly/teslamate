package cn.doveshelly.teslamate.bo;

import lombok.Data;

@Data
public class CalendarDayDto {
    private String day;          // 日期，如 "01", "02", 或 ""
    private String distance;     // 当日里程，如 "10km" 或 ""
    private boolean hasCharge;   // 当日是否有充电记录
    private boolean isActive;    // 是否是本月的有效日期
}