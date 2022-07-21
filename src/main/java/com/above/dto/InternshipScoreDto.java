package com.above.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InternshipScoreDto {

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "名称")
    private String content;
}
