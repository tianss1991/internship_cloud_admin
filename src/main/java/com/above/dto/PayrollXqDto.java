package com.above.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayrollXqDto {
    @ApiModelProperty(value = "编号")
    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM")
    @ApiModelProperty(value = "时间")
    @TableField("date_time")
    private Date dateTime;

    @ApiModelProperty(value = "学生id")
    private Integer studentId;

    @ApiModelProperty(value = "图片地址（JSON格式）")
    private String imgUrl;

    @ApiModelProperty(value = "工资")
    private BigDecimal salary;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    private String userAvatar;
}
