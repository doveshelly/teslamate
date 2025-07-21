package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("tokens")
public class Tokens {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("inserted_at")
    private LocalDateTime insertedAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    private byte[] refresh;
    private byte[] access;

    // Getters and Setters
}