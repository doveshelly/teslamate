package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("gps")
@Data
public class Gps {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String latitude;
    private String longitude;
    private String address;

    // Getters and Setters
}