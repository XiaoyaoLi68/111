package com.kingbo.petserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MySwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("宠物商城项目接口")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("项目API文档")
                        .version("6.6.6")
                        .description("项目描述信息")
                        .contact(new Contact()
                                .name("作者姓名")
                                .url("https://example.com/contact")  // 可选：作者个人主页
                                .email("author@example.com")         // 可选：作者邮箱
                        )
                        .license(null)  // 可选：许可证信息
                );
    }
}
