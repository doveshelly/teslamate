package cn.doveshelly.teslamate.bo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDto {
    // 对应中间的 6 个核心指标
    private List<Metric> metrics;
    // 对应底部的日历数据
    private List<CalendarDayDto> calendarDays;

    // 嵌套类，为了结构清晰
    @Data
    public static class Summary {
        private List<Metric> costs;
        private List<Metric> consumption;
    }

    @Data
    public static class Metric {
        private String label;
        private String value;

        public Metric(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }
}