package com.above.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayrollDto  {

    @ApiModelProperty(value = "工资单id")
    private Integer id;

    @ApiModelProperty(value = "年级")
    private String gradeYear;

    @ApiModelProperty(value = "专业名")
    private String majorName;

    @ApiModelProperty(value = "专业id")
    private Integer majorId;

    @ApiModelProperty(value = "班级")
    private String className;

    @ApiModelProperty(value = "学号")
    private String studentNumber;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "学生id")
    private Integer studentId;

    @ApiModelProperty(value = "图片地址（JSON格式）")
    private String imgUrl;

    @ApiModelProperty(value = "实习单位")
    private String companyName;

    @ApiModelProperty(value = "实习岗位")
    private String jobName;

    @ApiModelProperty(value = "实习工资")
    @TableField("salary")
    private BigDecimal salary;

    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern = "yyyy-MM")
    private Date dateTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "逻辑删除 0-未删除 1-已删除")
    private Integer deleted;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    private String userAvatar;

}
