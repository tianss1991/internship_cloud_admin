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


/**
 * 错误码对象
 * <p>
 * 全局错误码，占用 [0, 999]，参见 {@link GlobalException}
 */
public class ErrorCode {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String message;

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
