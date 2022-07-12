package com.above.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 教师前端接收类
 * @Author: LZH
 * @Date: 2022/1/11 10:33
 */
@Data
public class TeacherVo extends BaseVo{

    @ApiModelProperty(value = "编号")
    private Integer id;

    @ApiModelProperty(value = "姓名")
    private String teacherName;

    @ApiModelProperty(value = "工号")
    private String workNumber;

    @ApiModelProperty(value = "是否在职 0-在职 1-离职")
    private Integer workStatus;

    @ApiModelProperty(value = "性别;0-未设置  1-男  2-女")
    private Integer gender;

    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "教职工编制类别")
    private String workType;

    @ApiModelProperty(value = "移动电话")
    private String telephone;

    @ApiModelProperty(value = "邮箱")
    private String mail;
}
