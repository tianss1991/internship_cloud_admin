package com.above.vo;

import com.above.po.Payroll;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
public class PayrollVo extends Payroll {

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    private String userAvatar;

    @ApiModelProperty("实习单位")
    private String companyName;

    @ApiModelProperty("实习岗位")
    private String jobName;

    @ApiModelProperty(value = "年级")
    private String gradeYear;

    @ApiModelProperty("教师id")
    private Integer teacherId;

    @ApiModelProperty(value = "专业名")
    private String majorName;

    @ApiModelProperty("专业id")
    private Integer majorId;

    @ApiModelProperty("学生名字")
    private String studentName;

    @ApiModelProperty(value = "页数")
    private Integer page = 1;

    @ApiModelProperty(value = "每页个数")
    private Integer size = 10;

    private Integer start;

    public Integer getStart() {
        if (page != null && size != null && page != 0) {
            start = (this.page - 1) * size;
        }
        return start;
    }


    public void setStart(Integer start) {
        this.start = start;
    }

    public static Integer calculationPages(Integer size, Integer totalCount) {
        return (totalCount + size - 1) / size;
    }

}
