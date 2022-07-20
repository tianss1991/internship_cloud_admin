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

