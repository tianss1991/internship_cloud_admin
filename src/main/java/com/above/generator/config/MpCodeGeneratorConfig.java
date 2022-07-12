package com.above.generator.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author BANGO
 */
@Component
@EnableTransactionManagement
public class MpCodeGeneratorConfig {


    /**
     * @Description: 乐观锁
     * @Author: LZH
     * @Date: 2022/1/9 14:11
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor(){return new OptimisticLockerInterceptor(); }
    /**
     * @Description: 分页
     * @Author: LZH
     * @Date: 2022/1/9 14:11
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){return  new PaginationInterceptor();}

    /**
     * @Description: 逻辑删除组件
     * @Author: LZH
     * @Date: 2022/1/9 14:11
     */
    @Bean
    public ISqlInjector sqlInjector(){return new LogicSqlInjector(); }

    /**
     * @Description: SQL执行效率组件
     * @Author: LZH
     * @Date: 2022/1/9 14:11
     */
    @Bean
    @Profile({"dev","test"})
    public PerformanceInterceptor performanceInterceptor(){
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setMaxTime(1000);
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }

}
