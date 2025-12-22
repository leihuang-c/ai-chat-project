#!/bin/bash
# scripts/env-checker.sh
# 定时检查环境变量变化并自动重启服务

set -e

# 工作目录
WORK_DIR="/app/ai-chat-project"
ENV_FILE="$WORK_DIR/app.env"
COMPOSE_FILE="$WORK_DIR/docker-compose.yml"
HASH_FILE="$WORK_DIR/.env-hash"

# 切换到工作目录
cd "$WORK_DIR" || exit 1

# 计算当前环境文件的哈希值
current_hash=$(md5sum "$ENV_FILE" | awk '{print $1}')

# 检查是否有保存的哈希值
if [ -f "$HASH_FILE" ]; then
    previous_hash=$(cat "$HASH_FILE")
else
    previous_hash=""
fi

# 比较哈希值
if [ "$current_hash" != "$previous_hash" ]; then
    echo "环境变量文件有变化，重新部署服务..."
    echo "$current_hash" > "$HASH_FILE"
    
    # 执行部署脚本
    "$WORK_DIR/scripts/deploy-to-ecs.sh"
    
    # 记录日志
    echo "$(date): 环境变量变化检测，服务已重启" >> "$WORK_DIR/deploy.log"
else
    echo "环境变量文件无变化"
fi