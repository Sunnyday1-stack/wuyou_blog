#!/bin/bash
# =============================================
#  WuyouBlog 部署脚本 (Linux/Mac)
#  用法: bash deploy.sh [backend|frontend|all]
# =============================================

SERVER="root@39.106.215.48"

deploy_backend() {
    echo "=== 构建后端 JAR ==="
    cd E:/my_blog/backend || exit
    mvn clean package -DskipTests
    echo "=== 上传 JAR ==="
    scp -o StrictHostKeyChecking=no target/blog-backend-0.0.1-SNAPSHOT.jar $SERVER:/opt/myblog/myblog.jar
    echo "=== 重启后端 ==="
    ssh -o StrictHostKeyChecking=no $SERVER "systemctl restart myblog"
    echo "=== 后端完成 ==="
}

deploy_frontend() {
    echo "=== 构建前端 ==="
    cd E:/my_blog/frontend || exit
    npx vite build
    echo "=== 上传前端 ==="
    cd dist || exit
    scp -o StrictHostKeyChecking=no -r * $SERVER:/opt/myblog/frontend/
    echo "=== 重启 Nginx ==="
    ssh -o StrictHostKeyChecking=no $SERVER "systemctl restart nginx"
    echo "=== 前端完成 ==="
}

case "${1:-all}" in
    backend)  deploy_backend ;;
    frontend) deploy_frontend ;;
    all)      deploy_backend && deploy_frontend ;;
    *)        echo "用法: bash deploy.sh [backend|frontend|all]" ;;
esac