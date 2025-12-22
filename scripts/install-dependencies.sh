#!/bin/bash
# scripts/install-dependencies.sh
# ECS 依赖安装脚本

set -e

echo "=========================================="
echo "  AI-CHAT 系统依赖安装"
echo "=========================================="

# 颜色定义（支持 GitHub Actions 日志）
log_info() { echo "🐳 [INFO] $1"; }
log_success() { echo "✅ [SUCCESS] $1"; }
log_warning() { echo "⚠️ [WARNING] $1"; }
log_error() { echo "❌ [ERROR] $1"; }

# 检查并安装 Docker
install_docker() {
    log_info "检查 Docker..."
    
    if command -v docker &> /dev/null; then
        log_success "Docker 已安装: $(docker --version)"
        return 0
    fi
    
    log_info "安装 Docker..."
    
    # 检测操作系统
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$ID
        log_info "操作系统: $PRETTY_NAME"
    else
        log_error "无法检测操作系统"
        exit 1
    fi
    
    case $OS in
        "ubuntu"|"debian")
            # Ubuntu/Debian 安装
            sudo apt-get update
            sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
            
            # 添加 Docker 官方 GPG 密钥
            curl -fsSL https://download.docker.com/linux/$OS/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
            
            # 添加稳定版仓库
            echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/$OS $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
            
            sudo apt-get update
            sudo apt-get install -y docker-ce docker-ce-cli containerd.io
            ;;
        "centos"|"rhel")
            # CentOS/RHEL 安装
            sudo yum install -y yum-utils
            sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
            sudo yum install -y docker-ce docker-ce-cli containerd.io
            ;;
        "amzn")
            # Amazon Linux
            sudo yum install -y docker
            ;;
        *)
            # 通用安装脚本
            log_info "使用通用安装脚本"
            curl -fsSL https://get.docker.com -o get-docker.sh
            sudo sh get-docker.sh
            ;;
    esac
    
    # 启动 Docker 服务
    sudo systemctl enable docker
    sudo systemctl start docker
    
    # 将当前用户添加到 docker 组
    sudo usermod -aG docker $USER
    
    log_success "Docker 安装完成: $(docker --version)"
}

# 安装 Docker Compose
install_docker_compose() {
    log_info "检查 Docker Compose..."
    
    if command -v docker-compose &> /dev/null; then
        log_success "Docker Compose 已安装: $(docker-compose --version)"
        return 0
    fi
    
    log_info "安装 Docker Compose..."
    
    # 获取最新版本
    COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep '"tag_name":' | sed -E 's/.*"([^"]+)".*/\1/')
    
    # 下载并安装
    sudo curl -L "https://github.com/docker/compose/releases/download/$COMPOSE_VERSION/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    
    # 创建符号链接
    sudo ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose
    
    log_success "Docker Compose 安装完成: $(docker-compose --version)"
}

# 验证安装
verify_installation() {
    log_info "验证安装结果..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker 安装失败"
        return 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose 安装失败"
        return 1
    fi
    
    if ! sudo systemctl is-active docker > /dev/null; then
        log_error "Docker 服务未运行"
        return 1
    fi
    
    log_success "所有依赖安装成功"
    echo "Docker: $(docker --version)"
    echo "Docker Compose: $(docker-compose --version)"
    echo "Docker 服务: $(sudo systemctl is-active docker)"
}

# 主函数
main() {
    log_info "开始系统依赖安装..."
    
    install_docker
    install_docker_compose
    verify_installation
    
    log_success "系统依赖安装完成!"
}

# 执行主函数
main "$@"