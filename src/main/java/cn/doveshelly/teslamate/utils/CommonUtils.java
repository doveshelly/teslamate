package cn.doveshelly.teslamate.utils;

import cn.doveshelly.teslamate.bo.DriveDTO;
import cn.doveshelly.teslamate.data.Gps;
import cn.doveshelly.teslamate.mapper.DrivesMapper;
import cn.doveshelly.teslamate.mapper.GpsMapper;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class CommonUtils {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private GpsMapper gpsMapper;

    public String getAddress(String latitude, String longitude) {
        //先从数据库查
        List<Gps> gpsList = gpsMapper.selectList(Wrappers.<Gps>lambdaQuery().eq(Gps::getLatitude, latitude).eq(Gps::getLongitude, longitude));
        if (CollUtil.isNotEmpty(gpsList)) {
            return gpsList.get(0).getAddress();
        }

        String name = null;
        try {
            String url = "http://api.map.baidu.com/reverse_geocoding/v3/?ak={}&output=json&coordtype=wgs84ll&location={},{}&extensions_poi=1&radius=100";
            String json = HttpUtil.get(StrUtil.format(url, commonConfig.getBaiduKey(), latitude, longitude));
            JSONObject jsonObject = JSONUtil.parseObj(json);
            JSONObject poi = (JSONObject) jsonObject.getJSONObject("result").getJSONArray("pois").get(0);
            name = poi.getStr("name");
        } catch (Exception e) {
            log.info("获取经纬度失败.");
            throw new RuntimeException(e);
        }

        //将查出来的数据插入过去
        Gps gps = new Gps();
        gps.setLatitude(latitude);
        gps.setLongitude(longitude);
        gps.setAddress(name);
        gpsMapper.insert(gps);

        return name;
    }

    /**
     * 发送消息到钉钉机器人
     */
    public void sendTextMessage(String messageText, boolean isAtAll, List<String> phones) {
        DingTalkClient client = new DefaultDingTalkClient(commonConfig.getWebhookToken());

        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
		markdown.setText(messageText);
		markdown.setTitle("特斯拉行程报告");
		request.setMarkdown(markdown);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        if (!isAtAll && CollUtil.isNotEmpty(phones)) {
            at.setAtMobiles(phones);
        }
        at.setIsAtAll(isAtAll);
        request.setAt(at);
        request.setMarkdown(markdown);
        try {
            client.execute(request);
        } catch (ApiException e) {
            log.info("[ERROR] sendMessage", e);
        }
    }

    public static LocalDateTime toBeijingTime(LocalDateTime utcTime) {
        return utcTime.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                .toLocalDateTime();
    }

    public DriveDTO convertToDto(DrivesMapper.DriveDetailView detail) {
        DriveDTO dto = new DriveDTO();
        dto.setId(detail.getId());
        dto.setStartLocation(getAddress(detail.getStartLatitude(), detail.getStartLongitude()));
        dto.setEndLocation(getAddress(detail.getEndLatitude(), detail.getEndLongitude()));

        // Format DateTime: '2025-07-15 17:52-18:46 (53分)'
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedDateTime = detail.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                "-" + detail.getEndDate().format(timeFormatter) +
                " (" + detail.getDurationMin() + "分)";
        dto.setDateTime(formattedDateTime);

        // Set Distance
        dto.setDistance(String.format("%.1f", detail.getDistance()));

        // 优化能耗计算逻辑
        Double startRange = detail.getStartIdealRangeKm();
        Double endRange = detail.getEndIdealRangeKm();
        boolean hasRange = startRange != null && endRange != null && startRange > endRange;
        double powerConsumptionKwh = 0.0;
        double avgConsumptionWhKm = 0.0;
        double estimatedCost = 0.0;
        if (hasRange && detail.getDistance() > 0) {
            // 用续航里程变化 × 标定能耗
            double rangeDelta = startRange - endRange;
            // 估算消耗 = 续航掉了多少公里 × 标定百公里能耗 / 100
            powerConsumptionKwh = rangeDelta * commonConfig.getRatedConsumptionKwh() / 100.0;
            estimatedCost = powerConsumptionKwh * commonConfig.getCostPerKwh();
            avgConsumptionWhKm = (powerConsumptionKwh * 1000) / detail.getDistance();
        } else if (detail.getStartBatteryLevel() != null && detail.getEndBatteryLevel() != null && detail.getDistance() > 0) {
            // 退而求其次，百分比 × 电池容量
            double batteryUsedPercent = detail.getStartBatteryLevel() - detail.getEndBatteryLevel();
            powerConsumptionKwh = (batteryUsedPercent / 100.0) * commonConfig.getBatteryCapacity();
            estimatedCost = powerConsumptionKwh * commonConfig.getCostPerKwh();
            avgConsumptionWhKm = (powerConsumptionKwh * 1000) / detail.getDistance();
        }
        if (detail.getDistance() > 0) {
            dto.setPowerConsumption(String.format("%.2f", powerConsumptionKwh));
            dto.setEstimatedCost(String.format("%.2f", estimatedCost));
            dto.setAvgConsumption(String.format("%.0f", avgConsumptionWhKm));
        } else {
            dto.setPowerConsumption("0.00");
            dto.setEstimatedCost("0.00");
            dto.setAvgConsumption("0");
        }

        // Format Battery Change: '62% → 57%'
        if (detail.getStartBatteryLevel() != null && detail.getEndBatteryLevel() != null) {
            dto.setBatteryChange(detail.getStartBatteryLevel() + "% → " + detail.getEndBatteryLevel() + "%");
        } else {
            dto.setBatteryChange("N/A");
        }

        // Format Range Change: '214 → 192'
        if (startRange != null && endRange != null) {
            dto.setRangeChange(String.format("%.0f", startRange) + " → " + String.format("%.0f", endRange));
        } else {
            dto.setRangeChange("N/A");
        }

        return dto;
    }
}
