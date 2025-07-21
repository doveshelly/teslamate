package cn.doveshelly.teslamate.mapper;

import cn.doveshelly.teslamate.data.ParkingSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingMapper {
    /**
     * 根据月份和车辆ID查询停车会话
     *
     * @param month YYYY-MM 格式的字符串, e.g., "2025-07"
     * @param carId 车辆ID
     * @return 原始停车会话列表
     */
    List<ParkingSession> findParkingSessionsByMonth(
            @Param("month") String month,
            @Param("carId") Integer carId
    );
}