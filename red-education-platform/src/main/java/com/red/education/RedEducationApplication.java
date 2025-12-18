package com.red.education;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 红色教育平台启动类
 */
@SpringBootApplication
@EnableScheduling
@MapperScan({ "com.red.education.module.*.mapper", "com.red.education.common.mapper" })
public class RedEducationApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedEducationApplication.class, args);
        System.out.println("========================================");
        System.out.println("红色教育平台启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/api/doc.html");
        System.out.println("========================================");
    }
}
