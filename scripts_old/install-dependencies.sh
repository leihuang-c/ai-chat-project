#!/bin/bash
# scripts/install-dependencies.sh
set -e

echo "=== 开始检查并安装系统依赖 ==="

# 检测操作系统
detect_os() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        echo "检测到操作系统: $NAME $VERSION"
        OS=$ID
        OS_VERSION=$VERSION_ID
    else
        echo "警告: 无法检测操作系统类型，假设为 Ubuntu"
        OS="ubuntu"
        OS_VERSION="20.04"
    fi
}

# 安装 Docker
install_docker() {
    echo "检查 Docker..."
    if command -v docker &> /dev/null; then
        echo "✓ Docker 已安装: $(docker --version)"
        return 0
    fi
    
    echo "安装 Docker..."
    
    case $OS in
        "ubuntu"|"debian")
            # Ubuntu/Debian 安装
            sudo apt-get update
            sudo apt-get install -y \
                apt-transport-https \
                ca-certificates \
                curl \
                gnupg \
                lsb-release
            
            # 添加 Docker 官方 GPG 密钥
            curl -fsSL https://download.docker.com/linux/$OS/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
            
            # 添加 Docker 仓库
            echo \
                "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] \
                https://download.docker.com/linux/$OS \
                $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
            
            # 安装 Docker Engine
            sudo apt-get update
            sudo apt-get install -y \
                docker-ce \
                docker-ce-cli \
                containerd.io \
                docker-compose-plugin
            
            ;;
        "centos"|"rhel"|"amzn")
            # CentOS/RHEL/Amazon Linux 安装
            sudo yum install -y yum-utils
            sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
            sudo yum install -y docker-ce docker-ce-cli containerd.io
            ;;
        *)
            echo "不支持的操作系统，使用通用安装脚本"
            curl -fsSL https://get.docker.com -o get-docker.sh
            sudo sh get-docker.sh
            ;;
    esac
    
    # 启动 Docker 服务
    sudo systemctl enable docker
    sudo systemctl start docker
    
    # 将当前用户添加到 docker 组
    sudo usermod -aG docker $USER
    
    echo "✓ Docker 安装完成: $(docker --version)"
}

# 安装 Docker Compose
install_docker_compose() {
    echo "检查 Docker Compose..."
    if command -v docker-compose &> /dev/null; then
        echo "✓ Docker Compose 已安装: $(docker-compose --version)"
        return 0
    fi
    
    echo "安装 Docker Compose..."
    
    # 下载最新版本的 Docker Compose
    COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep '"tag_name":' | sed -E 's/.*"([^"]+)".*/\1/')
    sudo curl -L "https://github.com/docker/compose/releases/download/$COMPOSE_VERSION/docker-compose-$(uname -s)-$(uname -m)" \
        -o /usr/local/bin/docker-compose
    
    # 赋予执行权限
    sudo chmod +x /usr/local/bin/docker-compose
    
    # 创建符号链接（可选）
    sudo ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose
    
    echo "✓ Docker Compose 安装完成: $(docker-compose --version)"
}

# 验证安装
verify_installation() {
    echo "验证安装..."
    
    if ! command -v docker &> /dev/null; then
        echo "❌ Docker 安装失败"
        return 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo "❌ Docker Compose 安装失败"
        return 1
    fi
    
    # 验证 Docker 服务状态
    if ! sudo systemctl is-active docker > /dev/null; then
        echo "❌ Docker 服务未运行"
        return 1
    fi
    
    echo "✓ 所有依赖安装成功"
    echo "  - Docker: $(docker --version)"
    echo "  - Docker Compose: $(docker-compose --version)"
}

# 主函数
main() {
    echo "开始系统依赖检查与安装"
    
    # 检测操作系统
    detect_os
    
    # 安装依赖
    install_docker
    install_docker_compose
    
    # 验证安装
    verify_installation
    
    echo "=== 系统依赖安装完成 ==="
}

# 执行主函数
main "$@"