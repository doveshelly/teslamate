package cn.doveshelly.teslamate.task;

import cn.doveshelly.teslamate.bo.DriveDTO;
import cn.doveshelly.teslamate.mapper.DrivesMapper;
import cn.doveshelly.teslamate.utils.CommonUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
public class AlertTask {

    @Autowired
    private DrivesMapper drivesMapper;

    @Autowired
    private CommonUtils commonUtils;

    @Value("${alert.enabled}")
    public boolean enabled;


    /**
     * 推送行程
     *
     * @return
     * @throws Exception
     */
    @Scheduled(fixedRate = 60000)
    public void pushAlertMessage() {
        if (!enabled) {
            return;
        }
        List<DrivesMapper.DriveDetailView> list = drivesMapper.findNeedPushDriveDetails();

        if (CollUtil.isEmpty(list)) {
            log.info("未通知行程数量为0.");
            return;
        }
        log.info("未通知行程数量为{}.", list.size());
        ZoneId zone = ZoneId.systemDefault(); // 使用系统默认时区
        for (DrivesMapper.DriveDetailView driveDetailView : list) {
            long diff = DateUtil.between(Date.from(driveDetailView.getStartDate().atZone(zone).toInstant()), Date.from(driveDetailView.getEndDate().atZone(zone).toInstant()), DateUnit.SECOND, true);
            if (diff <= 120) {
                log.info("2分钟内行程不进行推送");
                drivesMapper.updatePushResult(driveDetailView.getId(), "02");
                return;
            }

            driveDetailView.setStartDate(CommonUtils.toBeijingTime(driveDetailView.getStartDate()));
            driveDetailView.setEndDate(CommonUtils.toBeijingTime(driveDetailView.getEndDate()));
            DriveDTO driveDTO = commonUtils.convertToDto(driveDetailView);
            commonUtils.sendTextMessage(driveDTO.toString(), false, null);

            drivesMapper.updatePushResult(driveDetailView.getId(), "01");
        }
    }
}
