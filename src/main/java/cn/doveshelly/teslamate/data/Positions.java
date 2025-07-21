package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("positions")
public class Positions {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private LocalDateTime date;
    private Double latitude;
    private Double longitude;
    private Integer speed;
    private Integer power;
    private Double odometer;

    @TableField("ideal_battery_range_km")
    private Double idealBatteryRangeKm;

    @TableField("battery_level")
    private Integer batteryLevel;

    private Double outsideTemp;
    private Integer elevation;
    private Integer fanStatus;

    @TableField("driver_temp_setting")
    private Double driverTempSetting;

    @TableField("passenger_temp_setting")
    private Double passengerTempSetting;

    @TableField("is_climate_on")
    private Boolean isClimateOn;

    @TableField("is_rear_defroster_on")
    private Boolean isRearDefrosterOn;

    @TableField("is_front_defroster_on")
    private Boolean isFrontDefrosterOn;

    @TableField("car_id")
    private Integer carId;

    @TableField("drive_id")
    private Integer driveId;

    private Double insideTemp;

    @TableField("battery_heater")
    private Boolean batteryHeater;

    @TableField("battery_heater_on")
    private Boolean batteryHeaterOn;

    @TableField("battery_heater_no_power")
    private Boolean batteryHeaterNoPower;

    @TableField("est_battery_range_km")
    private Double estBatteryRangeKm;

    @TableField("rated_battery_range_km")
    private Double ratedBatteryRangeKm;

    @TableField("usable_battery_level")
    private Integer usableBatteryLevel;

    @TableField("tpms_pressure_fl")
    private Double tpmsPressureFl;

    @TableField("tpms_pressure_fr")
    private Double tpmsPressureFr;

    @TableField("tpms_pressure_rl")
    private Double tpmsPressureRl;

    @TableField("tpms_pressure_rr")
    private Double tpmsPressureRr;

    // Getters and Setters
}