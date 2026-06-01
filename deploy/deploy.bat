@echo off
REM =============================================
REM  WuyouBlog 部署脚本
REM  用法: deploy.bat [backend|frontend|all]
REM =============================================

set SERVER=root@39.106.215.48
set ASKPASS=C:\Users\m'q\AppData\Local\Temp\ssh_askpass.bat
set SSH_ASKPASS=%ASKPASS%
set DISPLAY=dummy
set SSH_ASKPASS_REQUIRE=force

if "%1"=="backend" goto :backend
if "%1"=="frontend" goto :frontend
if "%1"=="all" goto :all

:all
:backend
echo === 构建后端 JAR ===
cd /d E:\my_blog\backend
call mvn clean package -DskipTests
echo === 上传 JAR 到服务器 ===
scp -o StrictHostKeyChecking=no target\blog-backend-0.0.1-SNAPSHOT.jar %SERVER%:/opt/myblog/myblog.jar
echo === 重启后端服务 ===
ssh -o StrictHostKeyChecking=no %SERVER% "systemctl restart myblog"
echo === 后端部署完成 ===
if not "%1"=="all" goto :end

:frontend
echo === 构建前端 ===
cd /d E:\my_blog\frontend
call npx vite build
echo === 上传前端到服务器 ===
cd dist
scp -o StrictHostKeyChecking=no -r * %SERVER%:/opt/myblog/frontend/
echo === 重启 Nginx ===
ssh -o StrictHostKeyChecking=no %SERVER% "systemctl restart nginx"
echo === 前端部署完成 ===
goto :end

:end
echo === 全部完成 ===