package cn.doveshelly.teslamate.web;

import cn.doveshelly.teslamate.api.CommonResult;
import cn.doveshelly.teslamate.bo.CalendarDayDto;
import cn.doveshelly.teslamate.bo.DashboardDto;
import cn.doveshelly.teslamate.mapper.DashboardMapper;
import cn.doveshelly.teslamate.utils.CommonConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class DashboardController {

    @Autowired
    private final DashboardMapper dashboardMapper;

    @Autowired
    private CommonConfig commonConfig;

    @RequestMapping
    public CommonResult<DashboardDto> getDashboardData(
            @RequestParam("month") String month, // 格式: "2025-07"
            @RequestParam(value = "carId", defaultValue = "1") Integer carId) {
        DashboardDto dashboard = new DashboardDto();

        YearMonth yearMonth = YearMonth.parse(month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        // 1. 从数据库获取所有需要的原始聚合数据
        Map<String, Object> rawMetrics = dashboardMapper.getSimplifiedMetrics(carId, startDate, endDate);

        long driveCount = (long) rawMetrics.get("drive_count");
        double totalDistance = (double) rawMetrics.get("total_distance");
        long totalDurationMin = (long) rawMetrics.get("total_duration_min");
        long chargeCount = (long) rawMetrics.get("charge_count");
        double totalChargeEnergy = ((BigDecimal) rawMetrics.get("total_charge_energy")).doubleValue();
        long total_start_battery = (long) rawMetrics.get("total_start_battery");
        long total_end_battery = (long) rawMetrics.get("total_end_battery");

        // 2. 根据您指定的公式进行计算

        // 平均速度 = 总里程 / (总分钟数 / 60)
        double avgSpeed = 0;
        if (totalDurationMin > 0) {
            avgSpeed = totalDistance / (totalDurationMin / 60.0);
        }

        // 能耗 (Wh/km) = (充电总量 kWh * 1000) / 总里程
        double avgConsumption = 0;
        if (totalDistance > 0) {
            avgConsumption = ((total_start_battery - total_end_battery) / 100d * commonConfig.getBatteryCapacity() * 1000) / totalDistance;
        }

        // 3. 组装成最终的 List<Metric> 格式
        List<DashboardDto.Metric> metrics = new ArrayList<>();
        metrics.add(new DashboardDto.Metric("行驶(次)", String.valueOf(driveCount)));
        metrics.add(new DashboardDto.Metric("里程(km)", String.format("%.2f", totalDistance)));
        metrics.add(new DashboardDto.Metric("速度(km/h)", String.format("%.2f", avgSpeed)));
        metrics.add(new DashboardDto.Metric("能耗(wh/km)", String.format("%.1f", avgConsumption)));
        metrics.add(new DashboardDto.Metric("充电(次)", String.valueOf(chargeCount)));
        metrics.add(new DashboardDto.Metric("充入(度)", String.format("%.2f", totalChargeEnergy)));
        dashboard.setMetrics(metrics);


        // 2. 获取日历所需数据
        //  a. 获取每天的行驶数据
        List<Map<String, Object>> dailyDriveStatsList = dashboardMapper.getDailyDriveStats(carId, startDate, endDate);
        Map<LocalDate, Double> driveDataMap = dailyDriveStatsList.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row.get("day")).toLocalDate(),
                        row -> (double) row.get("daily_distance")
                ));

        //  b. 获取有充电的日期
        Set<LocalDate> chargeDates = new HashSet<>(dashboardMapper.getDailyChargeDates(carId, startDate, endDate));

        // 3. 组装日历数据
        dashboard.setCalendarDays(buildCalendar(yearMonth, driveDataMap, chargeDates));

        return CommonResult.success(dashboard);
    }

    private List<CalendarDayDto> buildCalendar(YearMonth yearMonth, Map<LocalDate, Double> driveDataMap, Set<LocalDate> chargeDates) {
        List<CalendarDayDto> calendarDays = new ArrayList<>();
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int firstDayOfWeekValue = firstOfMonth.getDayOfWeek().getValue(); // 1=周一, 7=周日

        // 1. 添加日历第一天之前的空白占位符
        int leadingEmptyDays = (firstDayOfWeekValue == 7) ? 0 : firstDayOfWeekValue;
        leadingEmptyDays = firstDayOfWeekValue - 1;
        for (int i = 0; i < leadingEmptyDays; i++) {
            CalendarDayDto emptyDay = new CalendarDayDto();
            emptyDay.setDay("");
            emptyDay.setActive(false);
            emptyDay.setDistance("");
            calendarDays.add(emptyDay);
        }

        // 2. 填充当月的每一天
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate currentDate = yearMonth.atDay(day);
            CalendarDayDto dayDto = new CalendarDayDto();
            dayDto.setDay(String.format("%02d", day));
            dayDto.setActive(true);

            // 设置行驶里程
            double distance = driveDataMap.getOrDefault(currentDate, 0.0);
            if (distance > 0) {
                dayDto.setDistance(String.format("%.0fkm", distance));
            } else {
                dayDto.setDistance("");
            }

            // 设置是否有充电
            dayDto.setHasCharge(chargeDates.contains(currentDate));

            calendarDays.add(dayDto);
        }

        return calendarDays;
    }
}