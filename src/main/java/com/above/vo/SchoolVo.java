package com.above.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 学校信息前端接收类
 * @Author: LZH
 * @Date: 2022/1/10 11:11
 */
@ApiModel("学校信息")
@Data
public class SchoolVo {

    @ApiModelProperty(value = "学校id")
    private Integer schoolId;

    @ApiModelProperty(value = "学校id(批量删除用)")
    private List<Integer> schoolIdList;

    @ApiModelProperty(value = "学校名称")
    private String schoolName;

    @ApiModelProperty(value = "学校logo的url")
    private String schoolLogo;

    @ApiModelProperty(value = "学校描述")
    private String schoolDesc;

    @ApiModelProperty(value = "教师id")
    private List<Integer> teacherIdList;

    @ApiModelProperty(value = "教师删除id")
    private List<Integer> deleteTeacherIdList;



}
