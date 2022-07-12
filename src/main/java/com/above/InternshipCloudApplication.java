package com.above;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com/above/dao")
@EnableTransactionManagement
/*配置文件解密注解*/
@EnableEncryptableProperties
public class InternshipCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternshipCloudApplication.class, args);
    }



}
