package com.above.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 微信用户表
 * </p>
 *
 * @author mp
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WechatUser对象", description="微信用户表")
public class WechatUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "微信openID")
    @TableField("openid")
    private String openid;

    @ApiModelProperty(value = "用户统一标识")
    @TableField("unionid")
    private String unionid;

    @ApiModelProperty(value = "微信昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "性别")
    @TableField("sex")
    private Integer sex;

    @ApiModelProperty(value = "语言")
    @TableField("language")
    private String language;

    @ApiModelProperty(value = "所在市")
    @TableField("city")
    private String city;

    @ApiModelProperty(value = "所在省")
    @TableField("province")
    private String province;

    @ApiModelProperty(value = "国家")
    @TableField("country")
    private String country;

    @ApiModelProperty(value = "用户头像，最后一个数值代表正方形头像大小（有 0、46、64、96、132 数值可选，0 代表 640*640 正方形头像），用户没有头像时该项为空")
    @TableField("headimgurl")
    private String headimgurl;

    @ApiModelProperty(value = "	用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）")
    @TableField("privilege")
    private String privilege;

    @ApiModelProperty(value = "绑定的用户账号")
    @TableField("account_number")
    private String accountNumber;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


}
