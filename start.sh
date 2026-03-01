#!/bin/bash
# 快速启动脚本 (Linux/Mac)

echo "🚀 时拾纪旅行社管理平台 快速启动"
echo "======================================"
echo ""

# 检查Docker
if ! command -v docker &> /dev/null; then
    echo "❌ 需要安装 Docker，请访问 https://www.docker.com/products/docker-desktop"
    exit 1
fi

echo "✅ Docker 已安装"
echo ""

# 检查Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ 需要安装 Docker Compose"
    exit 1
fi

echo "✅ Docker Compose 已安装"
echo ""

# 显示版本信息
echo "📦 版本信息:"
docker --version
docker-compose --version
echo ""

# 启动
echo "⏳ 正在启动所有服务... (首次可能需要3-5分钟)"
echo ""

docker-compose down 2>/dev/null
docker-compose up --build

echo ""
echo "✅ 系统已启动！"
echo ""
echo "📍 访问地址："
echo "  • 前端:   http://localhost"
echo "  • API文档: http://localhost:8080/api/doc.html"
echo "  • MySQL:  localhost:3306"
echo "  • Redis:  localhost:6379"
echo ""
echo "🔐 默认账户："
echo "  • 用户名: admin"
echo "  • 密码: admin123"
echo ""
echo "📚 更多帮助请查看 快速启动指南.md"
