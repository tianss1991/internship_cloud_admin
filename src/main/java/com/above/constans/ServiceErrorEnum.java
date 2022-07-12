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

package com.above.constans;

/**
 * ClassName ServiceErrorEnum
 * 业务异常枚举
 *
 * @author <a href="mailto:liukl@zjport.gov.cn">liukl</a>
 * @date 2021/7/8 18:52
 * @since 1.0
 */

/**
 * 服务异常
 * <p>
 * 参考 https://www.kancloud.cn/onebase/ob/484204 文章
 * <p>
 * 一共 10 位，分成四段
 * <p>
 * 第一段，1 位，类型
 * 1 - 业务级别异常
 * 2 - 系统级别异常
 * 第二段，3 位，系统类型
 * 001 - ceb后台异常
 * 002 - ceb大屏异常
 * <p>
 * ... - ...
 * 第三段，3 位，模块
 * 不限制规则。
 * 一般建议，每个系统里面，可能有多个模块，可以再去做分段。
 * 第四段，3 位，错误码
 * 不限制规则。
 * 一般建议，每个模块自增。
 */
public enum ServiceErrorEnum {
    USER_NAME_IS_HAVE(1001001001, "用户名已存在！"),
    MENU_IS_NO_DATA(1001001002, "该菜单下不存在数据"),
    REMOTE_UCT_INTERFACE_ERROR(1001001003, "调用用户中心接口异常"),
    NO_FIELD(1001001004, "该字段不存在"),
    BASE_DATA_EXIST(1001001005, "数据已存在，请勿重复添加"),
    NOT_FOUND_MENU_FIELD_REF(1001001006, "未找到菜单映射的数据项，请联系管理员添加");
    private final Integer code;
    private final String message;

    ServiceErrorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }}
