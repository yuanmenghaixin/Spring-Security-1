package com.mayikt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName AppSecurity
 * @Author 蚂蚁课堂余胜军 QQ644064779 www.mayikt.com
 * @Version V1.0
 **/
@MapperScan("com.mayikt.mapper")
@SpringBootApplication
public class AppSecurity {
    public static void main(String[] args) {
        SpringApplication.run(AppSecurity.class);
    }
}
