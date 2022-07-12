package com.above.vo;

import com.above.po.GradeInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GradeVo extends GradeInfo {

    @ApiModelProperty(value = "年级id")
    private Integer gradeId;

}
