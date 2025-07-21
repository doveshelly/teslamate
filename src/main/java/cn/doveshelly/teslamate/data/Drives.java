package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("drives")
public class Drives {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("start_date")
    private LocalDateTime startDate;

    @TableField("end_date")
    private LocalDateTime endDate;

    @TableField("outside_temp_avg")
    private Double outsideTempAvg;

    private Integer speedMax;
    private Integer powerMax;
    private Integer powerMin;

    @TableField("start_ideal_range_km")
    private Double startIdealRangeKm;

    @TableField("end_ideal_range_km")
    private Double endIdealRangeKm;

    @TableField("start_km")
    private Double startKm;

    @TableField("end_km")
    private Double endKm;

    private Double distance;
    private Integer durationMin;

    @TableField("car_id")
    private Integer carId;

    @TableField("inside_temp_avg")
    private Double insideTempAvg;

    @TableField("start_address_id")
    private Integer startAddressId;

    @TableField("end_address_id")
    private Integer endAddressId;

    @TableField("start_rated_range_km")
    private Double startRatedRangeKm;

    @TableField("end_rated_range_km")
    private Double endRatedRangeKm;

    @TableField("start_position_id")
    private Integer startPositionId;

    @TableField("end_position_id")
    private Integer endPositionId;

    @TableField("start_geofence_id")
    private Integer startGeofenceId;

    @TableField("end_geofence_id")
    private Integer endGeofenceId;

    @TableField("push_result")
    private String pushResult;

    // Getters and Setters
}