package com.above.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserAccountVo extends BaseVo{


    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "工号/学号")
    private String accountNum;

    @ApiModelProperty(value = "学校名称")
    private String schoolName;

}
