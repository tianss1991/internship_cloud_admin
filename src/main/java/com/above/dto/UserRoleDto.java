package com.above.dto;

import com.above.po.AuthRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRoleDto {

    @ApiModelProperty("用户的权限")
    private AuthRole userRoleDto;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("用户类型")
    private Integer userType;

    @ApiModelProperty("学校id")
    private Integer schoolId;

    @ApiModelProperty("二级学院id")
    private Integer departmentId;

    @ApiModelProperty("学校名称")
    private String schoolName;

    @ApiModelProperty("二级学院名称")
    private String departmentName;

}
