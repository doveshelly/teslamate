package cn.doveshelly.teslamate.data;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParkingSession {
    // 假设 drive_id 是 bigint 或 integer
    private Long driveId;
    private Integer carId;
    private String latitude;
    private String longitude;
    private LocalDateTime parkingStartDate;
    private LocalDateTime parkingEndDate;
    private Long parkingDurationSeconds; // 将时长直接在SQL中计算为秒
    private Integer batteryLevelStart;
    private Integer batteryLevelEnd;

    // 新增字段，用于接收续航里程
    private BigDecimal rangeKmStart;
    private BigDecimal rangeKmEnd;
}