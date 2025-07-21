package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("updates")
public class Updates {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("start_date")
    private LocalDateTime startDate;

    @TableField("end_date")
    private LocalDateTime endDate;

    private String version;
    
    @TableField("car_id")
    private Integer carId;

    // Getters and Setters
}