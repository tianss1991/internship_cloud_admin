package com.above.po;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息联系人索引表;本表只存储用户互相沟通的最后一句话，方便消息中联系人列表的内容）
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MessageContactsIndex对象", description="消息联系人索引表;本表只存储用户互相沟通的最后一句话，方便消息中联系人列表的内容）")
public class MessageContactsIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户A 发送人")
    @TableField("user_a")
    private Integer userA;

    @ApiModelProperty(value = "用户B 接收人")
    @TableField("user_b")
    private Integer userB;

    @ApiModelProperty(value = "用户类型;0-兼职用户 1-企业用户")
    @TableField("user_type")
    private Integer userType;

    @ApiModelProperty(value = "是否置顶 0-不置顶 1-置顶")
    @TableField("is_top")
    private Integer isTop;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "消息内容 （放于该用户沟通的最后一条记录）")
    @TableField("message_content")
    private String messageContent;

    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "update_datetime", fill = FieldFill.UPDATE)
    private Date updateDatetime;

    @ApiModelProperty(value = "额外字段;避免添加字段修改代码")
    @TableField("extra")
    private String extra;

    @ApiModelProperty(value = "消息类型 0-系统消息  1-审核消息  2-预警消息")
    @TableField("msg_type")
    private String msgType;

    @ApiModelProperty(value = "1-日报 2-周报 3-月报 4-总结")
    @TableField("audit_type")
    private String auditType;

    @ApiModelProperty(value = "是否删除该联系人，0-未删除1-删除状态")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;


}
