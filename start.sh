#!/bin/bash

echo "==============================================="
echo "  医院陪护证管理系统 - Docker 一键启动脚本"
echo "==============================================="
echo ""

cd "$(dirname "$0")"

if [ ! -f .env ]; then
    echo "正在创建环境配置文件..."
    cp .env.example .env
    echo "已创建 .env 文件，请根据需要修改配置"
    echo ""
fi

echo "正在检查 Docker 是否安装..."
if ! command -v docker &> /dev/null; then
    echo "错误: 未检测到 Docker，请先安装 Docker"
    exit 1
fi

echo "正在检查 Docker Compose 是否安装..."
if ! command -v docker-compose &> /dev/null; then
    if ! docker compose version &> /dev/null; then
        echo "错误: 未检测到 Docker Compose，请先安装 Docker Compose"
        exit 1
    fi
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

echo ""
echo "正在构建并启动服务..."
echo "这可能需要几分钟时间，请耐心等待..."
echo ""

$COMPOSE_CMD up -d --build

echo ""
echo "==============================================="
echo "  服务启动完成！"
echo "==============================================="
echo ""
echo "访问地址："
echo "  前端页面: http://localhost"
echo "  后端API:  http://localhost:8080/api"
echo "  API文档:  http://localhost:8080/api/doc.html"
echo ""
echo "测试账号："
echo "  护士站: nurse001 / 123456"
echo "  门禁:   gate001  / 123456"
echo "  管理员: admin    / 123456"
echo ""
echo "常用命令："
echo "  查看日志: $COMPOSE_CMD logs -f"
echo "  停止服务: $COMPOSE_CMD stop"
echo "  重启服务: $COMPOSE_CMD restart"
echo "  删除服务: $COMPOSE_CMD down"
echo ""
