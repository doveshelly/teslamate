package cn.doveshelly.teslamate.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {

    /**
     * 一次性获取指定月份的所有聚合指标的原始数据
     */
    Map<String, Object> getSimplifiedMetrics(
            @Param("carId") Integer carId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 获取指定月份每天的行驶里程
     *
     * @return 返回一个 Map，Key 是日期 (LocalDate)，Value 是当天的总里程 (Double)
     */
    List<Map<String, Object>> getDailyDriveStats(@Param("carId") Integer carId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * 获取指定月份有充电记录的日期
     *
     * @return 返回一个日期列表 (LocalDate)
     */
    List<LocalDate> getDailyChargeDates(@Param("carId") Integer carId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
}