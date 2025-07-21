package cn.doveshelly.teslamate.bo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DriveDTO {
    private Integer id;
    private String startLocation;
    private String endLocation;
    private String dateTime;
    private String powerConsumption;
    private String estimatedCost;
    private String distance;
    private String rangeChange;
    private String avgConsumption;
    private String batteryChange;

    @Override
    public String toString() {
        return String.format(
                "### 🚗 **特斯拉行程报告**\n\n" +
                        "**行程详情**\n" +
                        "- **路线:** 从 **%s** 到 **%s**\n" +
                        "- **时间:** %s\n" +
                        "- **里程:** **%s km**\n\n" +
                        "**能耗分析**\n" +
                        "- **总消耗:** **%s kWh** (预计 %s 元)\n" +
                        "- **平均能耗:** %s Wh/km\n" +
                        "- **电量变化:** %s\n" +
                        "- **续航变化:** %s",
                startLocation,
                endLocation,
                dateTime,
                distance,
                powerConsumption,
                estimatedCost,
                avgConsumption,
                batteryChange,
                rangeChange);
    }
}