package com.above.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DepartmentVo extends BaseVo{

    @ApiModelProperty(value = "二级学院名称")
    private String departmentName;


    @ApiModelProperty(value = "教师删除id")
    private List<Integer> deleteTeacherIdList;

//    @ApiModelProperty(value = "是否为学生显示（学生的二级学院信息中不包含职能部门）")
//    private int forStudent;

}
