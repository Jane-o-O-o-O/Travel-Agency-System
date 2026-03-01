package com.shishiji.travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 时拾纪旅行社管理平台
 * 应用启动类
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class TravelPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelPlatformApplication.class, args);
        System.out.println("=== 时拾纪旅行社管理平台启动成功 ===");
        System.out.println("API文档: http://localhost:8080/api/doc.html");
    }
}
