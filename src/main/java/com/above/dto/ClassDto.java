package com.above.dto;

import com.above.po.ClassInfo;
import com.above.po.ClassTeacherRelation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ClassDto extends ClassInfo {

    @ApiModelProperty("辅导员列表")
    private List<ClassTeacherRelation> classLeader = new ArrayList<>();
    @ApiModelProperty("班主任列表")
    private List<ClassTeacherRelation> classTeacher  = new ArrayList<>();
}
