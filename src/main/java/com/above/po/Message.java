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
 * 消息表
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Message对象", description="消息表")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "消息是否已读 0-未读 1-已读")
    @TableField("unread")
    private Integer unread;

    @ApiModelProperty(value = "消息类型 0-系统消息  1-审核消息  2-预警消息")
    @TableField("msg_type")
    private Integer msgType;

    @ApiModelProperty(value = "预留字段（多权限时分类使用）")
    @TableField("msg_user_type")
    private Integer msgUserType;

    @ApiModelProperty(value = "消息发送人")
    @TableField("send_user_id")
    private Integer sendUserId;

    @ApiModelProperty(value = "消息接收人")
    @TableField("accept_user_id")
    private Integer acceptUserId;

    @ApiModelProperty(value = "消息内容")
    @TableField("msg_content")
    private String msgContent;

    @ApiModelProperty(value = "说明 1 文字消息   2 图片消息  3 文件消息 4 语音消息 5 视频消息 6 表情 7 链接消息")
    @TableField("chat_type")
    private Integer chatType;

    @ApiModelProperty(value = "消息标题 系统消息")
    @TableField("msg_title")
    private String msgTitle;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "附件地址")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "图片地址（JSON格式）")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty(value = "逻辑删除 0-未删除 1-已删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    @ApiModelProperty(value = "1-日报 2-周报 3-月报 4-总结")
    @TableField("audit_type")
    private String auditType;


}
