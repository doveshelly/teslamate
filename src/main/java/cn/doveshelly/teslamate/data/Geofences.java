package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("geofences")
public class Geofences {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private Double latitude;
    private Double longitude;

    @TableField("inserted_at")
    private LocalDateTime insertedAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    private Integer radius;

    @TableField("cost_per_unit")
    private Double costPerUnit;

    @TableField("session_fee")
    private Double sessionFee;

    @TableField("billing_type")
    private String billingType;

    // Getters and Setters
}