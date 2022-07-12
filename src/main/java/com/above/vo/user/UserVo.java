package com.above.vo.user;

import com.above.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *@Decription: 用户的基础类
 *@params:
 *@return:
 *@Author:hxj
 *@Date:2022/1/10 13:58
 */
@Data
@ApiModel("用户")
public class UserVo extends BaseVo {

    /**
     *  管理员
     */
    public static final Integer ADMIN = 0;
    /**
     *  游客
     */
    public static final Integer STUDENT = 1;
    /**
     *  教职工
     */
    public static final Integer TEACHER = 2;

    /**
     *  手机号
     */
    @ApiModelProperty("手机号")
    private String telephone;
    /**
     *  验证码
     */
    @ApiModelProperty("验证码")
    private String code;
    /**
     *  登录账号
     */
    @ApiModelProperty("登录账号")
    private String accountNum;
    /**
     *  密码
     */
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("小程序id")
    private String openid;

    @ApiModelProperty("教师角色")
    private Integer roleId;

    @ApiModelProperty("登录端类型 1-小程序用户端 2-管理端")
    private Integer loginType;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("学校姓名")
    private String schoolName;

}
