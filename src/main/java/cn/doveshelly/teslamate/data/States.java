package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("states")
public class States {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String state;
    
    @TableField("start_date")
    private LocalDateTime startDate;

    @TableField("end_date")
    private LocalDateTime endDate;

    @TableField("car_id")
    private Integer carId;

    // Getters and Setters
}