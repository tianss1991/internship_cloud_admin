package com.above.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserList {
    @ApiModelProperty(value = "创建者头像")
    private String userAva;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "教师工号 或 学生学号 或 管理员账号")
    private String number;

    @ApiModelProperty(value = "性别")
    private Integer gender;

    @ApiModelProperty(value = "id")
    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "评价时间")
    private Date createTime;

    @ApiModelProperty(value = "分数")
    private Integer score;


}
