package com.above.dto;

import com.above.po.ClassInfo;
import com.above.po.ClassTeacherRelation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClassInfoDto extends ClassInfo {

    private String schoolName;

    private String gradeYear;

    private String departmentName;

    private String majorName;

    @ApiModelProperty("辅导员列表")
    private List<ClassTeacherRelation> classLeader = new ArrayList<>();
    @ApiModelProperty("班主任列表")
    private List<ClassTeacherRelation> classTeacher  = new ArrayList<>();

    @ApiModelProperty("辅导员id")
    private List<Integer> classLeaderId;
    @ApiModelProperty("辅导员名称")
    private String classLeaderName;
    @ApiModelProperty("班主任名称")
    private String classTeacherName;
}
