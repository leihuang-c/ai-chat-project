#!/bin/bash
# scripts/deploy-to-ecs.sh
set -e

echo "开始部署..."

# 转到脚本所在目录
cd "$(dirname "$0")"
cd ..

# 从环境变量或参数中获取文件路径
COMPOSE_FILE=${COMPOSE_FILE:-"docker-compose.yml"}
ENV_FILE=${ENV_FILE:-"app.env"}

# 检查并安装系统依赖
check_dependencies() {
    echo "检查系统依赖..."
    
    # 如果依赖安装脚本存在，使用它
    if [ -f "scripts/install-dependencies.sh" ]; then
        echo "使用独立依赖安装脚本"
        scripts/install-dependencies.sh
    else
        # 备用方案：直接检查并安装
        if ! command -v docker &> /dev/null || ! command -v docker-compose &> /dev/null; then
            echo "系统依赖不完整，请先运行 scripts/install-dependencies.sh"
            exit 1
        fi
    fi
}

# 检查必要文件
check_required_files() {
    echo "检查必要文件..."
    
    if [ ! -f "$COMPOSE_FILE" ]; then
        echo "错误: Docker Compose 文件 $COMPOSE_FILE 不存在"
        return 1
    fi

    if [ ! -f "$ENV_FILE" ]; then
        echo "错误: 环境变量文件 $ENV_FILE 不存在"
        return 1
    fi
    
    echo "✓ 所有必要文件存在"
}

# 加载环境变量
load_environment() {
    echo "加载环境变量..."
    
    if [ ! -f "$ENV_FILE" ]; then
        echo "错误: 环境变量文件不存在"
        return 1
    fi
    
    # 导出所有变量
    set -a
    source "$ENV_FILE"
    set +a
    
    # 验证必要环境变量
    required_vars=("DB_PASSWORD" "DB_USER" "DB_NAME")
    for var in "${required_vars[@]}"; do
        if [ -z "${!var}" ]; then
            echo "错误: 必需环境变量 $var 未设置"
            return 1
        fi
    done
    
    echo "✓ 环境变量加载成功"
}

# 执行部署
run_deployment() {
    echo "执行部署..."
    
    # 停止现有服务（如果存在）
    echo "停止现有服务..."
    docker-compose -f "$COMPOSE_FILE" down || true

    # 拉取最新镜像
    echo "拉取最新 Docker 镜像..."
    docker-compose -f "$COMPOSE_FILE" pull

    # 启动服务
    echo "启动服务..."
    docker-compose -f "$COMPOSE_FILE" up -d

    # 等待服务健康检查
    echo "等待服务启动..."
    sleep 30

    # 检查服务状态
    echo "检查服务状态..."
    docker-compose -f "$COMPOSE_FILE" ps
}

# 健康检查
health_check() {
    echo "执行健康检查..."
    
    local max_attempts=10
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:${API_GATEWAY_PORT:-8080}/actuator/health > /dev/null; then
            echo "✅ 服务健康检查通过"
            return 0
        fi
        
        echo "等待服务就绪... ($attempt/$max_attempts)"
        sleep 10
        ((attempt++))
    done
    
    echo "⚠️ 服务健康检查超时，但部署已完成"
    return 0
}

# 主部署函数
main_deployment() {
    echo "=== 开始部署流程 ==="
    
    # 1. 检查并安装依赖
    check_dependencies
    
    # 2. 检查必要文件
    check_required_files || exit 1
    
    # 3. 加载环境变量
    load_environment || exit 1
    
    # 4. 执行部署
    run_deployment
    
    # 5. 健康检查
    health_check
    
    echo "=== 部署完成 ==="
}

# 执行主函数
main_deployment "$@"