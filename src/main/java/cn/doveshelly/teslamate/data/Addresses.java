package cn.doveshelly.teslamate.data;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.time.LocalDateTime;

@TableName("addresses")
public class Addresses {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("display_name")
    private String displayName;

    private Double latitude;
    private Double longitude;
    private String name;
    private String houseNumber;
    private String road;
    private String neighbourhood;
    private String city;
    private String county;
    private String postcode;
    private String state;
    private String stateDistrict;
    private String country;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object raw; // 可根据实际结构替换为具体类

    @TableField("inserted_at")
    private LocalDateTime insertedAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    private Long osmId;

    @TableField("osm_type")
    private String osmType;

    // Getters and Setters
}