package com.h2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class H2ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(H2ServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("🤖 H2 TCP Service 启动成功!");
        System.out.println("📡 服务端口: 8083");
        System.out.println("🔗 H2控制台: http://localhost:8084/h2-console");
        System.out.println("=================================");
    }
}
