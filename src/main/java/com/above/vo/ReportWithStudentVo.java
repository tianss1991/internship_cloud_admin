package com.above.vo;

import com.above.po.ReportWithStudent;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReportWithStudentVo extends ReportWithStudent {

    @ApiModelProperty(value = "教师id")
    private Integer teacherId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    private String userAvatar;

    @ApiModelProperty(value = "专业id")
    private Integer majorId;



}
