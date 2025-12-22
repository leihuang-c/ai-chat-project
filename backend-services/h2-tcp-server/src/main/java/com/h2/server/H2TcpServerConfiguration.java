package com.h2.server;

import java.sql.SQLException;

import javax.annotation.PreDestroy;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;


// @Configuration
@Slf4j
public class H2TcpServerConfiguration {
    
    @Value("${h2.database.path:./data/ai_chat_system}")
    private String databasePath;
    
    @Value("${h2.tcp.port:9092}")
    private String tcpPort;
    
    @Value("${h2.web.port:8084}")
    private String webPort;
    
    private Server tcpServer;
    private Server webServer;
    
    /**
     * 启动H2 TCP服务器，允许远程连接
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        log.info("启动H2 TCP服务器...");
        
        // 启动TCP服务器（用于JDBC连接）
        tcpServer = Server.createTcpServer(
            "-tcp", "-tcpAllowOthers", "-tcpPort", tcpPort,
            "-baseDir", "./data",
            "-ifNotExists"
        );
        
        log.info("H2 TCP服务器启动成功，端口: {}，数据库路径: {}", tcpPort, databasePath);
        log.info("JDBC连接URL: jdbc:h2:tcp://localhost:{}/{}", tcpPort, databasePath);
        
        return tcpServer;
    }
    
    /**
     * 启动H2 Web控制台（可选）
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebConsole() throws SQLException {
        log.info("启动H2 Web控制台...");
        
        // 启动Web控制台
        webServer = Server.createWebServer(
            "-web", "-webAllowOthers", "-webPort", webPort
        );
        
        log.info("H2 Web控制台启动成功，访问地址: http://localhost:{}", webPort);
        
        return webServer;
    }
    
    @PreDestroy
    public void destroy() {
        log.info("正在关闭H2服务器...");
        if (tcpServer != null) {
            tcpServer.stop();
        }
        if (webServer != null) {
            webServer.stop();
        }
        log.info("H2服务器已关闭");
    }
}