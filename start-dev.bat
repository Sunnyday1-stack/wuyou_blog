@echo off
title WuyouBlog 本地开发

echo ========================================
echo   WuyouBlog 本地开发环境
echo ========================================
echo.
echo 启动前请确保:
echo   1. MySQL 已运行 (端口 3306)
echo   2. 数据库 blog_db 已创建
echo   3. JDK 17+ 和 Maven 已安装
echo   4. Node.js 18+ 已安装
echo.
echo ========================================

REM 启动后端
echo [1/2] 启动后端 (Spring Boot :8080)...
start "Blog Backend" cmd /c "cd /d E:\my_blog\backend && mvn spring-boot:run"

REM 等待后端启动
echo 等待后端启动 (15秒)...
timeout /t 15 /nobreak >nul

REM 启动前端
echo [2/2] 启动前端 (Vite :5173)...
start "Blog Frontend" cmd /c "cd /d E:\my_blog\frontend && npm run dev"

echo.
echo ========================================
echo  后端: http://localhost:8080
echo  前端: http://localhost:5173
echo ========================================
echo.
pause