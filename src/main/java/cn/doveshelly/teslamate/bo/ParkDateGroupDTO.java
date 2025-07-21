package cn.doveshelly.teslamate.bo; // 注意：您的 DTO 似乎放在 bo 包下

import lombok.Data;

import java.util.List;

@Data
public class ParkDateGroupDTO {
    private String date;
    private List<ParkDto> records;
}