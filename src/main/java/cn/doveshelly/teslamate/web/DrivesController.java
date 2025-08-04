package cn.doveshelly.teslamate.web;


import cn.doveshelly.teslamate.bo.DriveDateGroupDTO;
import cn.doveshelly.teslamate.api.CommonResult;
import cn.doveshelly.teslamate.bo.DriveDTO;
import cn.doveshelly.teslamate.mapper.DrivesMapper;
import cn.doveshelly.teslamate.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drives")
public class DrivesController {

    @Autowired
    private DrivesMapper drivesMapper;

    @Autowired
    private CommonUtils commonUtils;

    @RequestMapping("/monthly")
    public CommonResult<List<DriveDateGroupDTO>> getDrivesByMonth(@RequestParam("month") String month, @RequestParam(value = "carId", defaultValue = "1") Integer carId) {
        // Parse the year-month string like "2025-07"
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        // Call the custom mapper method
        List<DrivesMapper.DriveDetailView> driveDetails = drivesMapper.findDriveDetailsByMonth(carId, startDate, endDate);
        if (driveDetails == null || driveDetails.isEmpty()) {
            return CommonResult.success(Collections.emptyList());
        }
        
        // 过滤掉1分钟以内的行驶记录
        driveDetails = driveDetails.stream()
                .filter(detail -> detail.getDurationMin() != null && detail.getDurationMin() >= 1)
                .collect(Collectors.toList());
        
        if (driveDetails.isEmpty()) {
            return CommonResult.success(Collections.emptyList());
        }

        // 定义日期分组的 Key 的格式，例如 "07-16"
        DateTimeFormatter groupByFormatter = DateTimeFormatter.ofPattern("MM-dd");

        // 使用 Collectors.groupingBy 进行分组
        // 1. 第一个参数 `detail -> detail.getStartDate().format(groupByFormatter)` 指定按什么进行分组（日期字符串）
        // 2. 第二个参数 `LinkedHashMap::new` 保证了 Map 中的日期顺序与数据库查询出的顺序一致（通常是倒序）
        // 3. 第三个参数 `Collectors.mapping(...)` 指定对每个分组内的元素做什么操作（转换为 DTO 并收集为 List）
        Map<String, List<DriveDTO>> groupedByDate = driveDetails.stream()
                .collect(Collectors.groupingBy(
                        detail -> {
                            detail.setStartDate(CommonUtils.toBeijingTime(detail.getStartDate()));
                            detail.setEndDate(CommonUtils.toBeijingTime(detail.getEndDate()));
                            return detail.getStartDate().format(groupByFormatter);
                        },
                        LinkedHashMap::new,
                        Collectors.mapping(commonUtils::convertToDto, Collectors.toList())
                ));
        List<DriveDateGroupDTO> finalResult = groupedByDate.entrySet().stream()
                .map(entry -> {
                    DriveDateGroupDTO groupDTO = new DriveDateGroupDTO();
                    groupDTO.setDate(entry.getKey()); // 设置日期 "07-15"
                    groupDTO.setRecords(entry.getValue()); // 设置该日期的记录列表
                    return groupDTO;
                }).collect(Collectors.toList());
        return CommonResult.success(finalResult);
    }

}