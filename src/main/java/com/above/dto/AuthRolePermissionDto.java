package com.above.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *@Decription:
 *@params:
 *@return:
 *@Author:hxj
 *@Date:2022/1/13 14:27
 */
@Data
public class AuthRolePermissionDto {

    @ApiModelProperty(value = "角色编号，关联auth_role表id")
    private Integer roleId;

    @ApiModelProperty(value = "权限编号，关联auth_permisson表id")
    private Integer permissionId;

    @ApiModelProperty(value = "权限名称")
    private String permissionName;


}
