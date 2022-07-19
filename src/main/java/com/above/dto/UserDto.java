package com.above.dto;

import com.above.po.*;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * @Decription:
 * @params:
 * @return:
 * @Author:hxj
 * @Date:2022/1/10 16:19
 */
@Data
@ApiModel("用户参数")
public class UserDto {

    private static final long serialVersionUID = 1L;

    public static final transient Integer VISITOR = 3;

    public static final transient Integer TEACHER = 2;

    public static final transient Integer STUDENT = 1;

    public static final transient Integer MANAGER = 0;

    @ApiModelProperty(value = "编号")
    private Integer id;

    @ApiModelProperty(value = "教师工号 或 学生学号 或 管理员账号")
    private String number;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "微信")
    private String wechat;

    @ApiModelProperty(value = "支付宝")
    private String alipay;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "学生信息")
    private StudentInfo studentInfo;

    @ApiModelProperty(value = "学生信息")
    private TeacherInfo teacherInfo;

    @ApiModelProperty(value = "类型 1-学生 2-教师 3-管理员")
    private Integer userType;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    private String userAvatar;

    @ApiModelProperty(value = "类型 0-管理端 2-教师端")
    private Integer jumpType;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "生日")
    private String birth;

    @ApiModelProperty(value = "个性签名")
    private String sign;

    @ApiModelProperty(value = "所在地点")
    private String address;

    @ApiModelProperty(value = "所在地点 areaId")
    private Integer addressCode;

    @ApiModelProperty("用户的权限")
    private AuthRole userRoleDto;

    @ApiModelProperty("学校的id集合")
    private List<Integer> schoolIds;

    @ApiModelProperty("二级学院的id集合")
    private List<Integer> departmentIds;

    @ApiModelProperty("班级的id集合")
    private List<Integer> classIds;

    @ApiModelProperty("学校信息")
    private SchoolInfo schoolInfo;

    @ApiModelProperty(value = "实习计划信息")
    private SimplePlanInfoDto internshipPlanInfo;

    @ApiModelProperty(value = "实习信息")
    private InternshipInfoByStudent internshipInfo;

}
