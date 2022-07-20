package com.above.dto;

import com.above.po.InternshipPlanInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: userDto中存放的实习计划
 * @Author: LZH
 * @Date: 2022/7/19 14:04
 */
@Data
public class SimplePlanInfoDto extends InternshipPlanInfo {

    @ApiModelProperty(value = "指导老师id")
    private Integer teacherId;
    @ApiModelProperty(value = "指导老师名称")
    private String teacherName;
}
