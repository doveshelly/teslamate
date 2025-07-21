package cn.doveshelly.teslamate.web;

import cn.doveshelly.teslamate.api.CommonResult;
import cn.doveshelly.teslamate.bo.ParkDateGroupDTO;
import cn.doveshelly.teslamate.bo.ParkDto;
import cn.doveshelly.teslamate.data.ParkingSession;
import cn.doveshelly.teslamate.mapper.ParkingMapper;
import cn.doveshelly.teslamate.utils.CommonConfig;
import cn.doveshelly.teslamate.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parks")
@RequiredArgsConstructor
public class ParkingController {

    @Autowired
    private final ParkingMapper parkingMapper;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private CommonConfig commonConfig;

    // 时间格式化器保持不变
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @RequestMapping("/monthly")
    // 1. 修改方法签名，返回类型变为 List<ParkingRecordDto>
    public CommonResult<List<ParkDateGroupDTO>> getParkingReport(@RequestParam("month") String month, @RequestParam(value = "carId", defaultValue = "1") Integer carId) {

        // 2. 从数据库获取原始停车数据 (此步骤不变)
        List<ParkingSession> sessions = parkingMapper.findParkingSessionsByMonth(month, carId);

        if (sessions == null || sessions.isEmpty()) {
            return CommonResult.success(Collections.emptyList());
        }

        // 定义日期分组的 Key 的格式，例如 "07-16"
        DateTimeFormatter groupByFormatter = DateTimeFormatter.ofPattern("MM-dd");

        // 使用 Collectors.groupingBy 进行分组
        // 1. 第一个参数 `detail -> detail.getStartDate().format(groupByFormatter)` 指定按什么进行分组（日期字符串）
        // 2. 第二个参数 `LinkedHashMap::new` 保证了 Map 中的日期顺序与数据库查询出的顺序一致（通常是倒序）
        // 3. 第三个参数 `Collectors.mapping(...)` 指定对每个分组内的元素做什么操作（转换为 DTO 并收集为 List）
        Map<String, List<ParkDto>> groupedByDate = sessions.stream()
                // 步骤1: (优化) 先对每个元素进行预处理，比如时区转换。
                // 这样做可以避免在 groupingBy 中产生副作用，让代码更清晰。
                .map(detail -> {
                    detail.setParkingStartDate(CommonUtils.toBeijingTime(detail.getParkingStartDate()));
                    detail.setParkingEndDate(CommonUtils.toBeijingTime(detail.getParkingEndDate()));
                    return detail;
                })
                // 步骤2: (核心) 添加 filter 操作，过滤掉不符合条件的记录。
                // 注意：这里我们假设停车是耗电的，所以开始电量 > 结束电量。
                .filter(detail -> detail.getBatteryLevelStart() > detail.getBatteryLevelEnd())
                // 步骤3: 对已经过滤和处理过的数据流进行分组。
                .collect(Collectors.groupingBy(
                        // 分组的 key 提取逻辑现在非常纯粹，只负责返回 key。
                        detail -> detail.getParkingStartDate().format(groupByFormatter),
                        LinkedHashMap::new,
                        Collectors.mapping(this::convertToDto, Collectors.toList())
                ));

        List<ParkDateGroupDTO> finalResult = groupedByDate.entrySet().stream()
                .map(entry -> {
                    ParkDateGroupDTO groupDTO = new ParkDateGroupDTO();
                    groupDTO.setDate(entry.getKey()); // 设置日期 "07-15"
                    groupDTO.setRecords(entry.getValue()); // 设置该日期的记录列表
                    return groupDTO;
                }).collect(Collectors.toList());
        return CommonResult.success(finalResult);
    }

    /**
     * 辅助方法：将单个 ParkingSession 实体转换为 ParkingRecordDto
     *
     * @param session 数据库查询出的原始实体
     * @return 格式化后的 DTO 对象
     */
    private ParkDto convertToDto(ParkingSession session) {
        ParkDto recordDto = new ParkDto();

        // 设置 ID
        recordDto.setId(session.getDriveId());

        // 设置地点
        recordDto.setLocation(commonUtils.getAddress(session.getLatitude(), session.getLongitude()));

        // 格式化日期时间和停车时长
        String startTime = session.getParkingStartDate().format(TIME_FORMATTER);
        String endTime = session.getParkingEndDate().format(TIME_FORMATTER);
        String durationStr = formatDuration(session.getParkingDurationSeconds());
        recordDto.setDateTime(String.format("%s - %s (%s)", startTime, endTime, durationStr));

        // 格式化电量变化
        Integer startBattery = session.getBatteryLevelStart();
        Integer endBattery = session.getBatteryLevelEnd();
        recordDto.setBatteryChange(String.format("%d%% → %d%%", startBattery, endBattery));

        // 优先用续航里程变化估算能耗和费用
        BigDecimal cost = BigDecimal.ZERO;
        double consumptionKwh = 0.0;
        if (session.getRangeKmStart() != null && session.getRangeKmEnd() != null && session.getRangeKmStart().doubleValue() > session.getRangeKmEnd().doubleValue()) {
            double rangeDelta = session.getRangeKmStart().doubleValue() - session.getRangeKmEnd().doubleValue();
            consumptionKwh = rangeDelta * commonConfig.getRatedConsumptionKwh() / 100.0;
            cost = BigDecimal.valueOf(consumptionKwh * commonConfig.getCostPerKwh()).setScale(2, RoundingMode.HALF_UP);
            recordDto.setConsumption(String.format("%.2f", consumptionKwh));
        } else if (startBattery != null && endBattery != null) {
            // 退而求其次，百分比 × 电池容量
            int consumptionPercent = startBattery - endBattery;
            consumptionKwh = consumptionPercent * commonConfig.getBatteryCapacity() / 100.0;
            cost = BigDecimal.valueOf(consumptionKwh * commonConfig.getCostPerKwh()).setScale(2, RoundingMode.HALF_UP);
            recordDto.setConsumption(String.format("%.2f", consumptionKwh));
        } else {
            recordDto.setConsumption("0.00");
        }
        recordDto.setCost(cost.toString());

        // 续航变化
        if (session.getRangeKmStart() != null && session.getRangeKmEnd() != null) {
            long startRange = Math.round(session.getRangeKmStart().doubleValue());
            long endRange = Math.round(session.getRangeKmEnd().doubleValue());
            recordDto.setRangeChange(String.format("%dkm → %dkm", startRange, endRange));
        } else {
            recordDto.setRangeChange("N/A");
        }

        return recordDto;
    }

    /**
     * 辅助方法：将秒数格式化为 "X时Y分"
     */
    private String formatDuration(long totalSeconds) {
        if (totalSeconds < 60) {
            return "不到1分钟";
        }
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分");
        }
        return sb.toString();
    }
}