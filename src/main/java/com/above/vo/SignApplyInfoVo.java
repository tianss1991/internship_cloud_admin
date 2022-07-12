package com.above.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SignApplyInfoVo {
    @ApiModelProperty(value = "补卡事由")
    private Integer reason;

    @ApiModelProperty(value = "图片")
    private Integer imgUrl;
}
