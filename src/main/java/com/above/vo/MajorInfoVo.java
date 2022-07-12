package com.above.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("专业")
@Data
public class MajorInfoVo extends BaseVo{

        @ApiModelProperty(value = "编号")
        private Integer id;

        @ApiModelProperty(value = "院内专业代码")
        private String majorCode;

        @ApiModelProperty(value = "班级id集合（批量删除用）")
        private List<Integer> majorIdList;

        @ApiModelProperty(value = "教师删除id")
        private List<Integer> deleteTeacherIdList;

        @ApiModelProperty(value = "班主任名称")
        private String classTeacherName;

        @ApiModelProperty(value = "专业名称")
        private String majorName;

        @ApiModelProperty(value = "班级名称")
        private String className;

        @ApiModelProperty(value = "逻辑删除：0=未删除 1=删除")
        private Integer deleted;

}
