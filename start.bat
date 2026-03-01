@echo off
REM 快速启动脚本 (Windows)

echo.
echo 🚀 时拾纪旅行社管理平台 快速启动
echo ======================================
echo.

REM 检查Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 需要安装 Docker Desktop，请访问 https://www.docker.com/products/docker-desktop
    exit /b 1
)

echo ✅ Docker 已安装
echo.

REM 检查Docker Compose
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 需要安装 Docker Compose
    exit /b 1
)

echo ✅ Docker Compose 已安装
echo.

echo 📦 版本信息:
docker --version
docker-compose --version
echo.

REM 删除旧容器
echo ⏸️  清理旧容器...
docker-compose down 2>nul

echo.
echo ⏳ 正在启动所有服务... (首次可能需要3-5分钟)
echo.

docker-compose up --build

echo.
echo ✅ 系统已启动！
echo.
echo 📍 访问地址:
echo   • 前端:    http://localhost
echo   • API文档: http://localhost:8080/api/doc.html
echo   • MySQL:   localhost:3306
echo   • Redis:   localhost:6379
echo.
echo 🔐 默认账户:
echo   • 用户名: admin
echo   • 密码: admin123
echo.
echo 📚 更多帮助请查看 快速启动指南.md
echo.
pause
