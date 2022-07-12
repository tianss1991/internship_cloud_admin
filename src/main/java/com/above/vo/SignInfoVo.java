package com.above.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SignInfoVo extends BaseVo{

    @ApiModelProperty("签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date signDateTime;

    @ApiModelProperty("月份")
    @JsonFormat(pattern = "yyyy-MM",timezone = "GMT+8")
    private Date month;
}
