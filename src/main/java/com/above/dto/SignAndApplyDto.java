package com.above.dto;


import com.above.po.SignInfoByStudent;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SignAndApplyDto extends SignInfoByStudent {

    @ApiModelProperty(value = "申请编号")
    private Integer applyId;

    @ApiModelProperty(value = "申请状态 1-待审核 2-驳回 3-通过（打卡记录改为正常）")
    private Integer applyStatus;

    @ApiModelProperty(value = "学生名称")
    private String studentName;

    @ApiModelProperty(value = "头像")
    private String userAva;

}
