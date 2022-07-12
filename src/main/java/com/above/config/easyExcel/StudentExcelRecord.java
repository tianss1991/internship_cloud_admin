package com.above.config.easyExcel;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * 学生信息（Excel表读取的对象）
 *
 * @author imtss
 * @since 2022-07-06
 */
@Data
public class StudentExcelRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    //二级学院名称（系部名称）
    private String departmentName;

    //年级
    private String gradeName;

    //专业
    private String majorName;

    //班级
    private String className;

    //学号
    private String studentNumber;

    //姓名
    private String studentName;

    //性别
    private String gender;

    //手机号
    private String telephone;

    //邮箱
    private String mail;

}
