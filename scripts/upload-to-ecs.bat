@echo off
chcp 65001 >nul
echo ========================================
echo  AI-CHAT 平台 - 文件上传工具
echo ========================================
echo.

if "%1"=="" (
    echo 使用方法: upload-to-ecs.bat ECS_IP [用户名] [密钥路径]
    echo.
    echo 示例: upload-to-ecs.bat 192.168.1.100
    echo 示例: upload-to-ecs.bat 192.168.1.100 ec2-user C:\path\to\key.pem
    echo.
    set /p ECS_IP="请输入 ECS IP 地址: "
    if "!ECS_IP!"=="" exit /b
) else (
    set ECS_IP=%1
)

if "%2"=="" (
    set ECS_USER=root
) else (
    set ECS_USER=%2
)

if "%3"=="" (
    set SSH_CMD=ssh
    set SCP_CMD=scp
) else (
    set SSH_CMD=ssh -i %3
    set SCP_CMD=scp -i %3
)

echo 正在上传文件到 %ECS_USER%@%ECS_IP%...
echo.

:: 上传安装脚本
echo [1/5] 上传依赖安装脚本...
%SCP_CMD% scripts\install-dependencies.sh %ECS_USER%@%ECS_IP%:/tmp/
if errorlevel 1 (
    echo 错误: 文件上传失败
    pause
    exit /b 1
)

:: 上传应用文件
echo [2/5] 上传应用配置文件...
%SCP_CMD% docker-compose.yml %ECS_USER%@%ECS_IP%:/app/ai-chat-project/
%SCP_CMD% app.env %ECS_USER%@%ECS_IP%:/app/ai-chat-project/

:: 上传部署脚本
echo [3/5] 上传部署脚本...
%SCP_CMD% scripts\deploy.sh %ECS_USER%@%ECS_IP%:/app/ai-chat-project/scripts/

:: 上传部署脚本
echo [4/5] 上传环境变化检查脚本...
%SCP_CMD% scripts\env-chekcer.sh %ECS_USER%@%ECS_IP%:/app/ai-chat-project/scripts/

:: 设置权限
echo [5/5] 设置文件权限...
%SSH_CMD% %ECS_USER%@%ECS_IP% "chmod +x /app/ai-chat-project/scripts/deploy.sh"

echo.
echo ========================================
echo  文件上传完成！
echo ========================================
echo.
echo 下一步操作:
echo 1. 登录 ECS: ssh %ECS_USER%@%ECS_IP%
echo 2. 安装依赖: sudo /tmp/install-dependencies.sh
echo 3. 执行部署: cd /app/ai-chat-project && scripts/deploy.sh
echo.
pause