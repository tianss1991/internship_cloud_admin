package com.above.vo;

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
public class CompanyAccessVo extends BaseVo{

    private Integer id;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "图片地址（JSON格式）")
    private String imgUrl;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "辅导员寻访次数")
    private Integer instructorSize;

    @ApiModelProperty(value = "指导老师寻访次数")
    private Integer adviserSize;

}
