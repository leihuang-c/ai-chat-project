#!/bin/bash
# scripts/setup-ecs.sh
# 一键安装脚本，用于首次设置 ECS

set -e

echo "=== ECS 环境一键安装脚本 ==="

# 检查参数
if [ $# -lt 2 ]; then
    echo "用法: $0 <ECS_HOST> <ECS_USER> [SSH_KEY_PATH]"
    echo "示例: $0 192.168.1.100 root ~/.ssh/id_rsa"
    exit 1
fi

ECS_HOST=$1
ECS_USER=$2
SSH_KEY=${3:-"~/.ssh/id_rsa"}

# 检查 SSH 连接
check_ssh() {
    echo "检查 SSH 连接..."
    if ! ssh -i "$SSH_KEY" -o BatchMode=yes -o ConnectTimeout=5 $ECS_USER@$ECS_HOST echo "SSH 连接成功"; then
        echo "❌ SSH 连接失败"
        exit 1
    fi
}

# 安装依赖
install_dependencies() {
    echo "安装系统依赖..."
    scp -i "$SSH_KEY" scripts/install-dependencies.sh $ECS_USER@$ECS_HOST:/tmp/
    ssh -i "$SSH_KEY" $ECS_USER@$ECS_HOST "sudo /tmp/install-dependencies.sh"
}

# 创建目录结构
setup_directories() {
    echo "创建目录结构..."
    ssh -i "$SSH_KEY" $ECS_USER@$ECS_HOST "
        sudo mkdir -p /app/ai-chat-platform/scripts
        sudo chown $ECS_USER:$ECS_USER /app/ai-chat-platform
    "
}

# 验证安装
verify_installation() {
    echo "验证安装..."
    ssh -i "$SSH_KEY" $ECS_USER@$ECS_HOST "
        echo 'Docker: ' && docker --version
        echo 'Docker Compose: ' && docker-compose --version
        echo '系统准备就绪'
    "
}

# 主函数
main() {
    echo "开始设置 ECS 服务器: $ECS_USER@$ECS_HOST"
    
    check_ssh
    install_dependencies
    setup_directories
    verify_installation
    
    echo "=== ECS 环境设置完成 ==="
    echo "现在您可以运行部署脚本: scripts/deploy-to-ecs.sh"
}

main "$@"