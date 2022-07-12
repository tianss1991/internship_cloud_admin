/*
 *  *************************************************************************
 *  Copyright (c) 2006-2022 ZheJiang Electronic Port, Inc.
 *  All rights reserved.
 *
 *  项目名称：天网数据中心
 *  版权说明：本软件属浙江电子口岸有限公司所有，在未获得浙江电子口岸有限公司正式授权
 *             情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *             识产权保护的内容。
 * *************************************************************************
 */

package com.above.utils;


import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.annotation.JSONField;
import com.above.constans.GlobalErrorCodeConstants;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 通用返回
 *
 * @param <T> 数据泛型
 */
public final class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = 1520219930055169296L;

    /**
     * 错误编码
     */
    @ApiModelProperty("请求状态code 200成功")
    private Integer code;

    /**
     * 传入数据
     */
    @ApiModelProperty("返回数据")
    private T data;

    /**
     * 返回消息
     */
    @ApiModelProperty("返回信息")
    private String message;

    /**
     * 错误详情
     */
    @ApiModelProperty("错误详情")
    private String detailMessage;

    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMessage(), result.detailMessage);
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        return error(code, message, null);
    }

    public static <T> CommonResult<T> error(Integer code, String message, String detailMessage) {
        Assert.isTrue(!GlobalErrorCodeConstants.SUCCESS.getCode().equals(code), "code 必须是错误的！");
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.message = message;
        result.detailMessage = detailMessage;
        return result;
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.message = "";
        return result;
    }

    public static <T> CommonResult<T> success(String message, T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.message = message;
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public CommonResult<T> setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    @JSONField(serialize = false) // 避免序列化
    public boolean isSuccess() {
        return GlobalErrorCodeConstants.SUCCESS.getCode().equals(code);
    }

    @JSONField(serialize = false) // 避免序列化
    public boolean isError() {
        return !isSuccess();
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", detailMessage='" + detailMessage + '\'' +
                '}';
    }

    // ========= 和 Exception 异常体系集成 =========

    /**
     * 判断是否有异常。如果有，则抛出 {@link GlobalException} 或 {@link ServiceException} 异常
     */
    public void checkError() throws GlobalException, ServiceException {
        if (isSuccess()) {
            return;
        }
        // 全局异常
        if (GlobalErrorCodeConstants.isMatch(code)) {
            throw new GlobalException(code, message).setDetailMessage(detailMessage);
        }
        // 业务异常
        throw new ServiceException(code, message).setDetailMessage(detailMessage);
    }

    public static <T> CommonResult<T> error(ServiceException serviceException) {
        return error(serviceException.getCode(), serviceException.getMessage(),
                serviceException.getDetailMessage());
    }

    public static <T> CommonResult<T> error(GlobalException globalException) {
        return error(globalException.getCode(), globalException.getMessage(),
                globalException.getDetailMessage());
    }

}
