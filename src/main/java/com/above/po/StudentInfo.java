package com.above.po;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学生信息表
 * </p>
 *
 * @author mp
 * @since 2022-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StudentInfo对象", description="学生信息表")
public class StudentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户编号")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "年级id")
    @TableField("grade_id")
    private Integer gradeId;

    @ApiModelProperty(value = "学校编号")
    @TableField("school_id")
    private Integer schoolId;

    @ApiModelProperty(value = "二级学院编号")
    @TableField("department_id")
    private Integer departmentId;

    @ApiModelProperty(value = "班级编号")
    @TableField("class_id")
    private Integer classId;

    @ApiModelProperty(value = "专业编号")
    @TableField("major_id")
    private Integer majorId;

    @ApiModelProperty(value = "邮箱")
    @TableField("mail")
    private String mail;

    @ApiModelProperty(value = "学号")
    @TableField("student_number")
    private String studentNumber;

    @ApiModelProperty(value = "姓名")
    @TableField("student_name")
    private String studentName;

    @ApiModelProperty(value = "出生年月日 样式为：1990-10-01")
    @TableField("birth_date")
    private String birthDate;

    @ApiModelProperty(value = "性别;0-未设置  1-男  2-女")
    @TableField("gender")
    private Integer gender;

    @ApiModelProperty(value = "学生电话")
    @TableField("telephone")
    private String telephone;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_datetime", fill = FieldFill.UPDATE)
    private Date updateDatetime;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_datetime", fill = FieldFill.INSERT)
    private Date createDatetime;

    @ApiModelProperty(value = "学生状态 0-未上岗 1-上岗")
    @TableField("study_status")
    private Integer studyStatus;

    @ApiModelProperty(value = "逻辑删除：0=未删除;1=删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * @Description: 用于业务中关联使用
     */
    @ApiModelProperty(value = "实习计划")
    @TableField(exist = false)
    private InternshipPlanInfo planInfo;

}
