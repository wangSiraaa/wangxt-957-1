#!/bin/bash

echo "正在停止医院陪护证管理系统..."

cd "$(dirname "$0")"

if command -v docker-compose &> /dev/null; then
    docker-compose down
elif docker compose version &> /dev/null; then
    docker compose down
else
    echo "错误: 未检测到 Docker Compose"
    exit 1
fi

echo "服务已停止"
