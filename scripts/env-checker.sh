#!/bin/bash
# scripts/env-checker.sh
# 环境变化检查脚本

set -e

WORK_DIR="/app/ai-chat-project"
ENV_FILE="$WORK_DIR/app.env"
COMPOSE_FILE="$WORK_DIR/docker-compose.yml"
HASH_FILE="$WORK_DIR/.env-hash"
LOG_FILE="$WORK_DIR/logs/env-checker.log"

# 创建日志目录
mkdir -p "$(dirname "$LOG_FILE")"

# 日志函数
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOG_FILE"
}

# 检查 Docker 服务
check_docker() {
    if ! docker info &> /dev/null; then
        log "Docker 服务异常，尝试重启..."
        sudo systemctl restart docker
        sleep 10
    fi
}

# 主逻辑
main() {
    cd "$WORK_DIR" || { log "错误: 无法进入工作目录"; exit 1; }
    
    # 检查 Docker
    check_docker
    
    # 计算环境文件哈希
    if [ -f "$ENV_FILE" ]; then
        current_hash=$(md5sum "$ENV_FILE" | awk '{print $1}')
    else
        log "错误: 环境变量文件不存在"
        exit 1
    fi
    
    # 检查哈希变化
    if [ -f "$HASH_FILE" ]; then
        previous_hash=$(cat "$HASH_FILE")
    else
        previous_hash=""
    fi
    
    if [ "$current_hash" != "$previous_hash" ]; then
        log "检测到环境变量变化，重新部署..."
        echo "$current_hash" > "$HASH_FILE"
        
        # 执行部署
        if scripts/deploy.sh >> "$LOG_FILE" 2>&1; then
            log "✅ 重新部署成功"
        else
            log "❌ 重新部署失败"
        fi
    else
        log "环境变量无变化"
        
        # 检查服务状态
        if ! docker-compose ps | grep -q "Up"; then
            log "检测到服务异常，尝试重启..."
            docker-compose up -d
            log "服务已重启"
        fi
    fi
}

# 执行
main "$@"