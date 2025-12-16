package com.ai.chat.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("🤖 User Service 启动成功!");
        System.out.println("📡 服务端口: 8081");
        System.out.println("🔗 健康检查: http://localhost:8081/api/user/health");
        System.out.println("=================================");
    }
}
