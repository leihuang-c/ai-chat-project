package com.ai.chat.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("🤖 Chat Service 启动成功!");
        System.out.println("📡 服务端口: 8082");
        System.out.println("🔗 健康检查: http://localhost:8082/api/chat/health");
        System.out.println("=================================");

    }
}
