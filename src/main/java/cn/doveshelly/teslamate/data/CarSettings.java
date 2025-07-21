package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;

public class CarSettings {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("suspend_min")
    private Integer suspendMin;

    @TableField("suspend_after_idle_min")
    private Integer suspendAfterIdleMin;

    @TableField("req_not_unlocked")
    private Boolean reqNotUnlocked;

    @TableField("free_supercharging")
    private Boolean freeSupercharging;

    @TableField("use_streaming_api")
    private Boolean useStreamingApi;

    private Boolean enabled;
    private Boolean lfpBattery;

    // Getters and Setters
}