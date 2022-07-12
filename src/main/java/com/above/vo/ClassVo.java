package com.above.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClassVo extends BaseVo{



    @ApiModelProperty(value = "班级名称")
    @TableField("class_name")
    private String className;

    @ApiModelProperty(value = "教师删除id")
    private List<Integer> deleteTeacherIdList;

    @ApiModelProperty(value = "班主任名称")
    private String classTeacherName;


}
