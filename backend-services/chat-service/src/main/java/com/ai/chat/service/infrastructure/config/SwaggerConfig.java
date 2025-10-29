package com.ai.chat.service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Swagger/OpenAPI 配置类 
 * 用于配置API文档的生成和展示
 */
@Configuration
public class SwaggerConfig {

    /**
     * 自定义OpenAPI配置 配置API文档的基本信息、服务器地址和安全认证方案
     *
     * @return OpenAPI对象，包含所有API文档配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("聊天问答服务API")
                        .description("聊天问答服务接口")
                        .version("1.0.0"))
                .servers(java.util.Arrays.asList(
                        new Server().url("http://chat-service:8082")
                                .description("development environment")));
    }
}
