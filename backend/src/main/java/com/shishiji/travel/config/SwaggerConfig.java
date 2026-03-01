package com.shishiji.travel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/Knife4j 配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("时拾纪旅行社管理平台 API")
                        .description("完整的旅行社管理系统 - 包含客户、行程、订单、资源、工单等模块")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("系统管理员")
                                .email("admin@shishiji.com")));
    }
}
