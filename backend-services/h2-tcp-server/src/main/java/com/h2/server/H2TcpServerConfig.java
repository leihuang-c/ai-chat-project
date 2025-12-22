package com.h2.server;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class H2TcpServerConfig {

    @Value("${h2.tcp.port:9092}")
    private String h2TcpPort;

    @Value("${h2.web.port:8084}")
    private String h2WebPort;

    @Value("${h2.database.path:./data/ai_chat_system}")
    private String h2DatabasePath;

    @Bean
    CommandLineRunner initAndStartH2Server() {
        log.info("Initialize and start H2 TCP Server...");
        return args -> {
            try {
                // 加载H2驱动
                Class.forName("org.h2.Driver");

                File databaseFile = new File(h2DatabasePath + ".mv.db");
                File databaseDir = databaseFile.getParentFile();

                // 确保数据库目录存在
                if (!databaseDir.exists()) {
                    databaseDir.mkdirs();
                    System.out.println("数据库目录已创建：" + databaseDir.getAbsolutePath());
                }

                // 检查数据库文件是否存在
                boolean databaseExists = databaseFile.exists();
                System.out.println("数据库文件是否存在：" + databaseExists);

                if (!databaseExists) {
                    log.info("Database file does not exist. Creating new database file. Path: {}", databaseFile.getAbsolutePath());
                    createDatabaseWithEmbeddedMode(databaseFile);
                }

                // 启动TCP服务器
                startTcpServer(databaseDir.getAbsolutePath());

                // 启动Web控制台
                startWebConsole(databaseDir.getAbsolutePath());

                System.out.println("=====执行初始化脚本=====");
                initDatabaseWithTcpMode();

                log.info("H2 TCP Server started successfully...");
                log.info("Web console: http://localhost:{}" + h2WebPort);
                log.info("JDBC URL: jdbc:h2:tcp://localhost:{}/{}", h2TcpPort, h2DatabasePath);

            } catch (Exception e) {
                System.out.println("H2服务器启动过程中出现错误：" + e.getMessage());
                log.error(e.getMessage());
            }
        };
    }

    /**
     * 使用嵌入式模式创建数据库文件
     */
    private void createDatabaseWithEmbeddedMode(File databaseFile) throws Exception {
        log.info("Create database via embedded mode...");

        // 嵌入式模式URL
        String embeddedUrl = "jdbc:h2:" + databaseFile.getAbsolutePath().replace(".mv.db", "");
        System.out.println("嵌入式模式URL：" + embeddedUrl);

        // 创建连接
        try (Connection conn = DriverManager.getConnection(embeddedUrl, "sa", "")) {
            log.info("The database file was created...");

            // 验证数据库是否可用
            boolean isValid = conn.isValid(5);
            System.out.println("数据库连接有效性：" + isValid);
        }
    }

    /**
     * 启动TCP服务器
     */
    private void startTcpServer(String baseDir) throws Exception {
        log.info("Start H2 TCP Server...");

        // 启动TCP服务器
        Server tcpServer = Server.createTcpServer(
                "-tcp",
                "-tcpAllowOthers",
                "-tcpPort", h2TcpPort,
                "-baseDir", baseDir
        ).start();

        System.out.println("TCP服务器状态：" + tcpServer.getStatus());
    }

    /**
     * 启动Web控制台
     */
    private void startWebConsole(String baseDir) throws Exception {
        log.info("Start H2 Sever web console...");

        // 启动Web控制台
        Server webServer = Server.createWebServer(
                "-web",
                "-webAllowOthers",
                "-webPort", h2WebPort,
                "-baseDir", baseDir
        ).start();

        System.out.println("Web控制台状态：" + webServer.getStatus());
    }

    /**
     * 使用TCP模式连接并初始化数据库
     */
    private void initDatabaseWithTcpMode() throws Exception {
        log.info("Connect to database via TCP mode...");

        // 提取数据库名称
        File databaseFile = new File(h2DatabasePath + ".mv.db");
        String databaseName = databaseFile.getName().replace(".mv.db", "");
        String baseDir = databaseFile.getParentFile().getAbsolutePath();

        // TCP模式URL
        String tcpUrl = "jdbc:h2:tcp://localhost:" + h2TcpPort + "/" + baseDir + "/" + databaseName;
        System.out.println("TCP模式URL：" + tcpUrl);

        // 创建TCP连接
        try (Connection conn = DriverManager.getConnection(tcpUrl, "sa", "")) {
            log.info("Connected to DB via TCP...");

            // 执行初始化脚本
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("./sql/schema.sql"));
            System.out.println("=====执行schema.sql成功=====");

            ScriptUtils.executeSqlScript(conn, new ClassPathResource("./sql/data.sql"));
            System.out.println("=====执行data.sql成功=====");

            log.info("Scripts execution successful.");
        }
    }
}
