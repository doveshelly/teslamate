package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("settings")
public class Settings {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("inserted_at")
    private LocalDateTime insertedAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("unit_of_length")
    private String unitOfLength;

    @TableField("unit_of_temperature")
    private String unitOfTemperature;

    @TableField("preferred_range")
    private String preferredRange;

    @TableField("base_url")
    private String baseUrl;

    @TableField("grafana_url")
    private String grafanaUrl;

    private String language;

    @TableField("unit_of_pressure")
    private String unitOfPressure;

    // Getters and Setters
}