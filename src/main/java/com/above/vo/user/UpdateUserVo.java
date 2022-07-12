package com.above.vo.user;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Decription: 添加用户需要用到的vo类
 * @params:
 * @return:
 * @Author:hxj
 * @Date:2022/1/11 13:45
 */
@ApiModel("修改用户")
@Data
public class UpdateUserVo {

    // 用户的密码长度
    public static final Integer PASSWORD_LENGTH = 6;

    @ApiModelProperty(value = "用户的id")
    private Integer id;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    private String userAvatar;

    @ApiModelProperty(value = "用户的手机号")
    private String telephone;

    @ApiModelProperty(value = "用户的原来的手机号")
    private String oldTelephone;

    @ApiModelProperty(value = "用户的邮箱")
    private String email;

    @ApiModelProperty(value = "用户的微信号")
    private String wechat;

    @ApiModelProperty(value = "用户的支付宝")
    private String alipay;

    @ApiModelProperty(value = "原先的密码")
    private String oldPassword;

    @ApiModelProperty(value = "现在的密码")
    private String newPassword;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "生日")
    private String birth;

    @ApiModelProperty(value = "个性签名")
    private String sign;

    @ApiModelProperty(value = "所在地点 areaId")
    private String addressCode;

    @ApiModelProperty(value = "所在地点")
    private String address;


}
