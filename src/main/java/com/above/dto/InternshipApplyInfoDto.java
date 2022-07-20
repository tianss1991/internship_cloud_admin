package com.above.dto;

import com.above.po.InternshipInfoByStudent;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class InternshipApplyInfoDto extends InternshipInfoByStudent {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "头像")
    private String userAvatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @ApiModelProperty(value = "时间")
    private Date time;

    @ApiModelProperty(value = "学生名字")
    private String studentName;

    @ApiModelProperty(value = "学号")
    private String studentNumber;



}
