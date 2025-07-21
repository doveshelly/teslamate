package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("cars")
public class Cars {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long eid;
    private Long vid;
    private String model;
    private Double efficiency;

    @TableField("inserted_at")
    private LocalDateTime insertedAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    private String vin;
    private String name;
    private String trimBadging;

    @TableField("settings_id")
    private Long settingsId;

    @TableField("exterior_color")
    private String exteriorColor;

    @TableField("spoiler_type")
    private String spoilerType;

    @TableField("wheel_type")
    private String wheelType;

    @TableField("display_priority")
    private Integer displayPriority;

    @TableField("marketing_name")
    private String marketingName;

    // Getters and Setters
}