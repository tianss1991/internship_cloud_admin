package com.above.vo;

import com.above.po.StudentInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentVo extends BaseVo{

    @ApiModelProperty("学生id")
    private Integer studentId;

    @ApiModelProperty("学号")
    private String studentNumber;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("性别")
    private Integer gender;

    @ApiModelProperty("手机号")
    private String telephone;

    @ApiModelProperty("邮箱")
    private String mail;

    @ApiModelProperty("批量删除学生集合")
    private List<Integer> studentIds;


}
