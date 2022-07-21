package com.above.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SimpleStudentInfoDto {

    private Integer id;

    @ApiModelProperty(value = "学生学号")
    private String number;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    private String userAvatar;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "生日")
    private String birth;

    @ApiModelProperty(value = "个性签名")
    private String sign;

    @ApiModelProperty(value = "所在地点")
    private String address;
}
