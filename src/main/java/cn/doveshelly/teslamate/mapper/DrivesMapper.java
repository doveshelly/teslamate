package cn.doveshelly.teslamate.mapper;

import cn.doveshelly.teslamate.data.Drives;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

public interface DrivesMapper extends BaseMapper<Drives> {
    /**
     * Custom query to fetch drive details by joining with addresses and positions tables.
     * This query is designed to get all necessary data to build the DriveDTO.
     */
    @Select("SELECT " +
            "    d.id, " +
            "    d.start_date, " +
            "    d.end_date, " +
            "    d.duration_min, " +
            "    d.distance, " +
            "    d.start_ideal_range_km, " +
            "    d.end_ideal_range_km, " +
            "    start_pos.battery_level AS start_battery_level, " +
            "    end_pos.battery_level AS end_battery_level, " +
            "    start_pos.latitude AS start_latitude, " +
            "    start_pos.longitude AS start_longitude, " +
            "    end_pos.latitude AS end_latitude, " +
            "    end_pos.longitude AS end_longitude " +
            "FROM drives d " +
            "LEFT JOIN positions start_pos ON d.start_position_id = start_pos.id " +
            "LEFT JOIN positions end_pos ON d.end_position_id = end_pos.id " +
            "WHERE d.car_id = #{carId} AND d.start_date >= #{startDate} AND d.start_date < #{endDate} " +
            "ORDER BY d.start_date DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "durationMin", column = "duration_min"),
            @Result(property = "distance", column = "distance"),
            @Result(property = "startIdealRangeKm", column = "start_ideal_range_km"),
            @Result(property = "endIdealRangeKm", column = "end_ideal_range_km"),
            @Result(property = "startBatteryLevel", column = "start_battery_level"),
            @Result(property = "endBatteryLevel", column = "end_battery_level"),
            @Result(property = "startLatitude", column = "start_latitude"),
            @Result(property = "startLongitude", column = "start_longitude"),
            @Result(property = "endLatitude", column = "end_latitude"),
            @Result(property = "endLongitude", column = "end_longitude")
    })
    List<DriveDetailView> findDriveDetailsByMonth(@Param("carId") Integer carId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Select("SELECT " +
            "    d.id, " +
            "    d.start_date, " +
            "    d.end_date, " +
            "    d.duration_min, " +
            "    d.distance, " +
            "    d.start_ideal_range_km, " +
            "    d.end_ideal_range_km, " +
            "    start_pos.battery_level AS start_battery_level, " +
            "    end_pos.battery_level AS end_battery_level, " +
            "    start_pos.latitude AS start_latitude, " +
            "    start_pos.longitude AS start_longitude, " +
            "    end_pos.latitude AS end_latitude, " +
            "    end_pos.longitude AS end_longitude " +
            "FROM drives d " +
            "LEFT JOIN positions start_pos ON d.start_position_id = start_pos.id " +
            "LEFT JOIN positions end_pos ON d.end_position_id = end_pos.id " +
            "WHERE d.push_result =  '00'" +
            "ORDER BY d.start_date DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "durationMin", column = "duration_min"),
            @Result(property = "distance", column = "distance"),
            @Result(property = "startIdealRangeKm", column = "start_ideal_range_km"),
            @Result(property = "endIdealRangeKm", column = "end_ideal_range_km"),
            @Result(property = "startBatteryLevel", column = "start_battery_level"),
            @Result(property = "endBatteryLevel", column = "end_battery_level"),
            @Result(property = "startLatitude", column = "start_latitude"),
            @Result(property = "startLongitude", column = "start_longitude"),
            @Result(property = "endLatitude", column = "end_latitude"),
            @Result(property = "endLongitude", column = "end_longitude")
    })
    List<DriveDetailView> findNeedPushDriveDetails();

    @Update("update drives set push_result = #{pushResult} where id = #{id}")
    void updatePushResult(@Param("id") Integer id, @Param("pushResult") String pushResult);

    /**
     * A helper class to hold the flat result from the complex SQL query.
     */
    @Data
    class DriveDetailView {
        private Integer id;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer durationMin;
        private Double distance;
        private Double startIdealRangeKm;
        private Double endIdealRangeKm;
        private Integer startBatteryLevel;
        private Integer endBatteryLevel;
        private String startLatitude;
        private String startLongitude;
        private String endLatitude;
        private String endLongitude;
    }
}