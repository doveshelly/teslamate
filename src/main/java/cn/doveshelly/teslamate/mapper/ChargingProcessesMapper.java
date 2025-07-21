package cn.doveshelly.teslamate.mapper;

import cn.doveshelly.teslamate.data.ChargingProcesses;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargingProcessesMapper extends BaseMapper<ChargingProcesses> {
    /**
     * 自定义查询，用于按月份连接 charging_processes 和 addresses 表，获取充电会话详情
     */
    @Select("SELECT " +
            "    cp.id, " +
            "    cp.start_date, " +
            "    cp.end_date, " +
            "    cp.duration_min, " +
            "    cp.charge_energy_added, " +
            "    cp.cost, " + // <-- 新增：直接从数据库读取 cost 字段
            "    cp.start_battery_level, " +
            "    cp.end_battery_level, " +
            "    cp.start_rated_range_km, " +
            "    cp.end_rated_range_km, " +
            "    pos.latitude AS latitude, " +
            "    pos.longitude AS longitude " +
            "FROM charging_processes cp " +
            "LEFT JOIN positions pos ON cp.position_id = pos.id " +
            "WHERE cp.car_id = #{carId} AND cp.start_date >= #{startDate} AND cp.start_date < #{endDate} " +
            "ORDER BY cp.start_date DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "durationMin", column = "duration_min"),
            @Result(property = "chargeEnergyAdded", column = "charge_energy_added"),
            @Result(property = "cost", column = "cost"),
            @Result(property = "startBatteryLevel", column = "start_battery_level"),
            @Result(property = "endBatteryLevel", column = "end_battery_level"),
            @Result(property = "startRatedRangeKm", column = "start_rated_range_km"),
            @Result(property = "endRatedRangeKm", column = "end_rated_range_km"),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "longitude", column = "longitude")
    })
    List<ChargeProcessDetailView> findChargeSessionDetailsByMonth(@Param("carId") Integer carId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 用于接收自定义 SQL 查询结果的辅助类 (已更新)
     */
    @Data
    class ChargeProcessDetailView {
        private Integer id;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer durationMin;
        private Double chargeEnergyAdded;
        private Double cost;
        private Integer startBatteryLevel;
        private Integer endBatteryLevel;
        private Double startRatedRangeKm;
        private Double endRatedRangeKm;
        private String latitude;
        private String longitude;
    }
}