package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("charging_processes")
public class ChargingProcesses {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("start_date")
    private LocalDateTime startDate;

    @TableField("end_date")
    private LocalDateTime endDate;

    @TableField("charge_energy_added")
    private Double chargeEnergyAdded;

    @TableField("start_ideal_range_km")
    private Double startIdealRangeKm;

    @TableField("end_ideal_range_km")
    private Double endIdealRangeKm;

    @TableField("start_battery_level")
    private Integer startBatteryLevel;

    @TableField("end_battery_level")
    private Integer endBatteryLevel;

    private Integer durationMin;

    @TableField("outside_temp_avg")
    private Double outsideTempAvg;

    @TableField("car_id")
    private Integer carId;

    @TableField("position_id")
    private Integer positionId;

    @TableField("address_id")
    private Integer addressId;

    @TableField("start_rated_range_km")
    private Double startRatedRangeKm;

    @TableField("end_rated_range_km")
    private Double endRatedRangeKm;

    @TableField("geofence_id")
    private Integer geofenceId;

    @TableField("charge_energy_used")
    private Double chargeEnergyUsed;

    private Double cost;

    // Getters and Setters
}