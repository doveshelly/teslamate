package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("charges")
public class Charges {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private LocalDateTime date;
    
    @TableField("battery_heater_on")
    private Boolean batteryHeaterOn;

    @TableField("battery_level")
    private Integer batteryLevel;

    @TableField("charge_energy_added")
    private Double chargeEnergyAdded;

    @TableField("charger_actual_current")
    private Integer chargerActualCurrent;

    @TableField("charger_phases")
    private Integer chargerPhases;

    @TableField("charger_pilot_current")
    private Integer chargerPilotCurrent;

    @TableField("charger_power")
    private Integer chargerPower;

    @TableField("charger_voltage")
    private Integer chargerVoltage;

    @TableField("fast_charger_present")
    private Boolean fastChargerPresent;

    @TableField("conn_charge_cable")
    private String connChargeCable;

    @TableField("fast_charger_brand")
    private String fastChargerBrand;

    @TableField("fast_charger_type")
    private String fastChargerType;

    @TableField("ideal_battery_range_km")
    private Double idealBatteryRangeKm;

    @TableField("not_enough_power_to_heat")
    private Boolean notEnoughPowerToHeat;

    private Double outsideTemp;

    @TableField("charging_process_id")
    private Integer chargingProcessId;

    @TableField("battery_heater")
    private Boolean batteryHeater;

    @TableField("battery_heater_no_power")
    private Boolean batteryHeaterNoPower;

    @TableField("rated_battery_range_km")
    private Double ratedBatteryRangeKm;

    @TableField("usable_battery_level")
    private Integer usableBatteryLevel;

    // Getters and Setters
}