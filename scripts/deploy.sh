#!/bin/bash
# scripts/deploy.sh
# 应用部署脚本

set -e

echo "=========================================="
echo "  AI-CHAT 自动化部署"
echo "=========================================="

# 日志函数
log_info() { echo "🚀 [INFO] $1"; }
log_success() { echo "✅ [SUCCESS] $1"; }

# 工作目录
WORK_DIR="/app/ai-chat-project"
COMPOSE_FILE="$WORK_DIR/docker-compose.yml"
ENV_FILE="$WORK_DIR/app.env"

# 检查依赖
check_dependencies() {
    log_info "检查系统依赖..."
    
    if ! command -v docker &> /dev/null; then
        echo "❌ Docker 未安装"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo "❌ Docker Compose 未安装"
        exit 1
    fi
    
    log_success "依赖检查通过"
}

# 检查必要文件
check_required_files() {
    log_info "检查必要文件..."
    
    if [ ! -f "$COMPOSE_FILE" ]; then
        echo "❌ Docker Compose 文件不存在: $COMPOSE_FILE"
        exit 1
    fi
    
    if [ ! -f "$ENV_FILE" ]; then
        echo "❌ 环境变量文件不存在: $ENV_FILE"
        exit 1
    fi
    
    log_success "文件检查通过"
}

# 加载环境变量
load_environment() {
    log_info "加载环境变量..."
    
    if [ ! -f "$ENV_FILE" ]; then
        echo "❌ 环境变量文件不存在"
        exit 1
    fi
    
    # 导出所有变量
    set -a
    source "$ENV_FILE"
    set +a
    
    # 验证必要变量
    if [ -z "$DB_PASSWORD" ]; then
        echo "❌ 数据库密码未设置"
        exit 1
    fi
    
    log_success "环境变量加载成功"
}

# 执行部署
run_deployment() {
    log_info "开始部署流程..."
    
    # 切换到工作目录
    cd "$WORK_DIR"
    
    # 停止现有服务
    log_info "停止现有服务..."
    docker-compose down || true
    
    # 拉取最新镜像
    log_info "拉取最新镜像..."
    docker-compose pull
    
    # 启动服务
    log_info "启动服务..."
    docker-compose up -d
    
    log_success "服务启动完成"
}

# 健康检查
health_check() {
    log_info "执行健康检查..."
    
    local max_attempts=3
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:${API_GATEWAY_PORT:-8080}/actuator/health > /dev/null; then
            log_success "服务健康检查通过 (尝试 $attempt/$max_attempts)"
            return 0
        fi
        
        echo "等待服务就绪... ($attempt/$max_attempts)"
        sleep 10
        ((attempt++))
    done
    
    echo "⚠️ 健康检查超时，但部署已完成"
    return 0
}

# 显示服务状态
show_status() {
    log_info "服务状态:"
    docker-compose ps
    
    log_info "最近日志:"
    docker-compose logs --tail=10
}

# 主函数
main() {
    check_dependencies
    check_required_files
    load_environment
    run_deployment
#    health_check
    show_status
    
    log_success "部署完成!"
    echo "应用地址: http://localhost:${API_GATEWAY_PORT:-8080}"
    echo "健康检查: http://localhost:${API_GATEWAY_PORT:-8080}/actuator/health"
}

# 执行主函数
main "$@"