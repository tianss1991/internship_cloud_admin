package com.above.generator.config;

import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description: 自动添加创建时间更新时间
 * @Author: LZH
 * @Date: 2021/10/29 10:45
 */
@Component
@Slf4j
public class DateTimeAutoFillingHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
    log.info("start insert field....");
    this.setFieldValByName("createTime", new Date(),metaObject);
        this.setFieldValByName("createDatetime", new Date(),metaObject);
    this.setFieldValByName("deleted", BaseVo.UNDELETE,metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(),metaObject);
    }
}
