package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("schema_migrations")
public class SchemaMigrations {
    @TableId(value = "version", type = IdType.AUTO)
    private Long version;

    @TableField("inserted_at")
    private LocalDateTime insertedAt;

    // Getters and Setters
}