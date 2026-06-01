#!/bin/bash
set -e
echo "========================================"
echo " WuyouBlog DevBox 一键部署脚本"
echo "========================================"

PROJECT_DIR="/home/devbox/project/wuyou_blog"
cd "$PROJECT_DIR"

# ---------- 1. 安装依赖 ----------
echo "[1/6] 安装系统依赖..."
apt-get update -qq
apt-get install -y -qq mysql-server openjdk-17-jdk-headless maven nginx nodejs npm > /dev/null 2>&1
echo "  ✓ MySQL + Java17 + Maven + Nginx + Node.js"

# ---------- 2. 启动 MySQL 并初始化数据库 ----------
echo "[2/6] 启动 MySQL 并初始化数据库..."
service mysql start
mysql -u root -e "CREATE DATABASE IF NOT EXISTS blog_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root blog_db < deploy/blog_db.sql
# 创建应用用户（不使用 root）
mysql -u root -e "CREATE USER IF NOT EXISTS 'blog'@'localhost' IDENTIFIED BY 'blog_password_2024'; GRANT ALL PRIVILEGES ON blog_db.* TO 'blog'@'localhost'; FLUSH PRIVILEGES;"
echo "  ✓ 数据库 blog_db 已初始化"

# ---------- 3. 构建后端 ----------
echo "[3/6] 构建后端 (Maven, 首次约2-3分钟)..."
cd "$PROJECT_DIR/backend"
mvn clean package -DskipTests -q
echo "  ✓ 后端构建完成"

# ---------- 4. 构建前端 ----------
echo "[4/6] 构建前端 (npm, 首次约1-2分钟)..."
cd "$PROJECT_DIR/frontend"
npm install --silent 2>/dev/null
npm run build 2>/dev/null
echo "  ✓ 前端构建完成"

# ---------- 5. 配置 Nginx ----------
echo "[5/6] 配置 Nginx..."
# 复制前端产物
rm -rf /var/www/html/*
cp -r "$PROJECT_DIR/frontend/dist/"* /var/www/html/

# 写入 nginx 配置
cat > /etc/nginx/sites-available/default << 'NGINX'
server {
    listen 80;
    server_name _;
    root /var/www/html;
    index index.html;

    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /uploads/ {
        proxy_pass http://127.0.0.1:8080;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }

    gzip on;
    gzip_types text/css application/javascript application/json;
    gzip_min_length 1000;
}
NGINX

service nginx restart
echo "  ✓ Nginx 已配置"

# ---------- 6. 启动后端 ----------
echo "[6/6] 启动后端服务..."
# 杀掉旧进程（如果有）
pkill -f "blog-backend" 2>/dev/null || true
mkdir -p /opt/myblog/uploads

# 使用环境变量启动
export MYSQL_HOST=127.0.0.1
export MYSQL_PORT=3306
export MYSQL_DATABASE=blog_db
export MYSQL_USERNAME=blog
export MYSQL_PASSWORD=blog_password_2024
export UPLOAD_DIR=/opt/myblog/uploads/
export SPRING_PROFILES_ACTIVE=sealos

nohup java -jar "$PROJECT_DIR/backend/target/blog-backend-0.0.1-SNAPSHOT.jar" > /var/log/myblog-backend.log 2>&1 &
echo "  ✓ 后端已启动 (日志: /var/log/myblog-backend.log)"

# ---------- 完成 ----------
echo ""
echo "========================================"
echo " 🎉 部署完成！"
echo " 后端: http://$(hostname -I | awk '{print $1}'):8080"
echo " 前端: http://$(hostname -I | awk '{print $1}')"
echo "========================================"
echo ""
echo " 查看后端日志: tail -f /var/log/myblog-backend.log"
echo " 查看后端状态: curl http://127.0.0.1:8080/api/"