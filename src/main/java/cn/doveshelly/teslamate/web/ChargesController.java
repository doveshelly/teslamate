package cn.doveshelly.teslamate.web;

import cn.doveshelly.teslamate.bo.ChargeDateGroupDTO;
import cn.doveshelly.teslamate.utils.CommonConfig;
import cn.doveshelly.teslamate.api.CommonResult;
import cn.doveshelly.teslamate.bo.ChargeSessionDTO;
import cn.doveshelly.teslamate.mapper.ChargingProcessesMapper;
import cn.doveshelly.teslamate.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/charges")
public class ChargesController {


    @Autowired
    private ChargingProcessesMapper chargingProcessMapper;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private CommonConfig commonConfig;

    @RequestMapping("/monthly")
    public CommonResult<List<ChargeDateGroupDTO>> getChargesByMonth(
            @RequestParam("month") String month,
            @RequestParam(value = "carId", defaultValue = "1") Integer carId) {

        YearMonth yearMonth = YearMonth.parse(month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        List<ChargingProcessesMapper.ChargeProcessDetailView> chargeDetails = chargingProcessMapper.findChargeSessionDetailsByMonth(carId, startDate, endDate);

        if (chargeDetails == null || chargeDetails.isEmpty()) {
            return CommonResult.success(Collections.emptyList());
        }

        // 定义日期分组的 Key 的格式，例如 "07-16"
        DateTimeFormatter groupByFormatter = DateTimeFormatter.ofPattern("MM-dd");

        // 使用 Collectors.groupingBy 进行分组
        // 1. 第一个参数 `detail -> detail.getStartDate().format(groupByFormatter)` 指定按什么进行分组（日期字符串）
        // 2. 第二个参数 `LinkedHashMap::new` 保证了 Map 中的日期顺序与数据库查询出的顺序一致（通常是倒序）
        // 3. 第三个参数 `Collectors.mapping(...)` 指定对每个分组内的元素做什么操作（转换为 DTO 并收集为 List）
        Map<String, List<ChargeSessionDTO>> groupedByDate = chargeDetails.stream()
                .collect(Collectors.groupingBy(
                        detail -> {
                            detail.setStartDate(CommonUtils.toBeijingTime(detail.getStartDate()));
                            detail.setEndDate(CommonUtils.toBeijingTime(detail.getEndDate()));
                            return detail.getStartDate().format(groupByFormatter);
                        },
                        LinkedHashMap::new,
                        Collectors.mapping(this::convertToDto, Collectors.toList())
                ));
        // 步骤2: 将 Map 转换为 List<ChargeDateGroupDTO>
        List<ChargeDateGroupDTO> finalResult = groupedByDate.entrySet().stream()
                .map(entry -> {
                    ChargeDateGroupDTO groupDTO = new ChargeDateGroupDTO();
                    groupDTO.setDate(entry.getKey()); // 设置日期 "07-15"
                    groupDTO.setRecords(entry.getValue()); // 设置该日期的记录列表
                    return groupDTO;
                })
                .collect(Collectors.toList());

        return CommonResult.success(finalResult);
    }

    private ChargeSessionDTO convertToDto(ChargingProcessesMapper.ChargeProcessDetailView detail) {
        ChargeSessionDTO dto = new ChargeSessionDTO();
        dto.setId(detail.getId());
        dto.setLocation(commonUtils.getAddress(detail.getLatitude(), detail.getLongitude()));

        // --- 主要逻辑变更 ---
        double avgPower = 0.0;
        if (detail.getDurationMin() != null && detail.getDurationMin() > 0 && detail.getChargeEnergyAdded() != null) {
            // 1. 计算平均功率 (kW)
            avgPower = detail.getChargeEnergyAdded() / (detail.getDurationMin() / 60.0);
        }

        // 2. 根据平均功率判断充电类型
        dto.setType(avgPower > commonConfig.getFastChargePowerThresholdKw() ? "快充" : "慢充");
        dto.setAvgPower(String.format("%.1f", avgPower));


        // --- 逻辑变更结束 ---

        dto.setChargeAmount(String.format("%.1f", detail.getChargeEnergyAdded()));
        dto.setCost(String.format("%.2f", new BigDecimal(dto.getChargeAmount()).multiply(new BigDecimal(commonConfig.getCostPerKwh())).doubleValue()));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedDateTime = detail.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                " - " + detail.getEndDate().format(timeFormatter) +
                " (" + detail.getDurationMin() + "分)";
        dto.setDateTime(formattedDateTime);

        dto.setBatteryChange(detail.getStartBatteryLevel() + "% → " + detail.getEndBatteryLevel() + "%");

        double rangeAdded = detail.getEndRatedRangeKm() - detail.getStartRatedRangeKm();
        dto.setRangeAdded(String.format("%.0f", rangeAdded));

        return dto;
    }
}