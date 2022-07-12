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
 * 资源类型枚举
 *
 * @author <a href="mailto:liukl@zjport.gov.cn">liukl</a>
 * @date 2021/7/12 14:49
 * @since 1.0
 */
public enum ResourceTypeEnum {

    MENU("菜单", "1"),
    BUTTON("按钮", "2");

    private final String name;

    private final String code;

    ResourceTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }}
