package com.above.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InternshipInfoFillDto {
    /**
    *@author: GG
    *@data: 2022/7/19 14:40
    *@function:学生填报未填报用到的dto
    */
    @ApiModelProperty(value = "学生id")
    private Integer studentId;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "头像")
    private String userAvatar;

    @ApiModelProperty(value = "学生名字")
    private String studentName;

    @ApiModelProperty(value = "学号")
    private String studentNumber;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "岗位名称")
    private String jobName;


}
