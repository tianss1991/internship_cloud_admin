package com.above.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;

/**
 * @Description: Mybatis-plus代码生成器
 * @Author: LZH
 * @Date: 2021/9/26 11:34
 */
public class Codegenerator {

/*//    private static final String URL= YamlUtil.getDateSource("url");
//    private static final String USERNAME = YamlUtil.getDateSource("username");
//    private static final String PASSWORD = YamlUtil.getDateSource("password");*/

    public static void main(String[] args) {
        //构建代码生成对象
        AutoGenerator mpg = new AutoGenerator();
        //配置策略

        //1、全局配置
        GlobalConfig gc = new GlobalConfig();
        //输出目录
        String path = System.getProperty("user.dir");
        gc.setOutputDir(path + "/src/main/java");
        gc.setAuthor("mp");
        gc.setOpen(false);
        //是否覆盖原文件
        gc.setFileOverride(true);
        // 去I前缀
        gc.setServiceName("%sService");
        gc.setIdType(IdType.AUTO);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setSwagger2(true);
        gc.setServiceName("%sService");
        //放入全局配置
        mpg.setGlobalConfig(gc);

        //2、设置数据源
        DataSourceConfig dc = new DataSourceConfig();

        //测试数据库
        dc.setUrl("jdbc:mysql://101.35.116.79/internship_cloud?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true");
        dc.setUsername("root");
        dc.setPassword("root123!@#");
        dc.setDriverName("com.mysql.cj.jdbc.Driver");
        dc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dc);

        //3、包配置（生成文件地址）
        PackageConfig pc = new PackageConfig();
        /*pc.setModuleName("model");*/
        pc.setParent("com.above");
        pc.setEntity("po");
        pc.setMapper("dao");
        pc.setService("service");
        pc.setController("controller");
        pc.setServiceImpl("service.impl");
        pc.setXml("mapper");
        mpg.setPackageInfo(pc);
//第一次生成不需要开以下代码，后续生成需要再次使用到以下代码
//        TemplateConfig tc = new TemplateConfig();
//
//        tc.setController(null);
//
//        tc.setService(null);
//
//        tc.setServiceImpl(null);
//
//        tc.setMapper(null);
//
//        tc.setXml(null);
//
//        mpg.setTemplate(tc);

        //4、策略配置
        StrategyConfig sc = new StrategyConfig();
        //设置映射的表名,多张表用逗号“，”隔开
        sc.setInclude("area");
        //表名转驼峰
        sc.setNaming(NamingStrategy.underline_to_camel);
        //字段名转驼峰
        sc.setColumnNaming(NamingStrategy.underline_to_camel);
        //lombok注释
        sc.setEntityLombokModel(true);
        //生成@RestController注解
        sc.setRestControllerStyle(true);
        //生成字段注解
        sc.setEntityTableFieldAnnotationEnable(true);

        //自动填充
        TableFill createTime = new TableFill("create_time", FieldFill.INSERT);
        TableFill updateTime = new TableFill("update_time", FieldFill.UPDATE);
        TableFill createDatetime = new TableFill("create_datetime", FieldFill.INSERT);
        TableFill updateDatetime = new TableFill("update_datetime", FieldFill.UPDATE);
        TableFill deleted = new TableFill("deleted",FieldFill.INSERT);
        ArrayList<TableFill> tableFiles = new ArrayList<>();
        tableFiles.add(createTime);
        tableFiles.add(updateTime);
        tableFiles.add(createDatetime);
        tableFiles.add(updateDatetime);
        tableFiles.add(deleted);
        sc.setTableFillList(tableFiles);
        //乐观锁
        sc.setVersionFieldName("version");
        sc.setRestControllerStyle(true);
//        sc.setControllerMappingHyphenStyle(true);//localhost:8080/hello_id_2 url转下划线

        mpg.setStrategy(sc);

        mpg.execute();//执行
    }

}
