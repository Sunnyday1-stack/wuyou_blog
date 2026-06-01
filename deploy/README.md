# WuyouBlog 部署文档

## 服务器信息
- IP: 39.106.215.48
- 系统: Linux (Alibaba Cloud ECS)
- SSH: root@39.106.215.48

## 目录结构
```
/opt/myblog/
├── myblog.jar          # Spring Boot 后端
├── app.log             # 应用日志
├── uploads/            # 图片上传目录
├── frontend/           # 前端静态文件
│   ├── index.html
│   ├── assets/
│   ├── favicon.ico
│   └── *.jpg / *.mp4   # 静态资源
└── blog_db.sql         # 数据库结构（参考）

/etc/nginx/conf.d/
└── myblog.conf         # Nginx 配置

/etc/systemd/system/
└── myblog.service      # 后端 systemd 服务
```

## 端口映射
- 80 → Nginx → 前端静态文件 + 反向代理
- 8080 → Spring Boot 后端（仅本地）

## 部署命令

### 全量部署
```
deploy.bat all
```

### 仅后端
```
deploy.bat backend
```

### 仅前端
```
deploy.bat frontend
```

## 服务管理
```bash
systemctl restart myblog   # 重启后端
systemctl restart nginx    # 重启 Nginx
systemctl status myblog    # 查看后端状态
journalctl -u myblog -f    # 查看后端日志
```

## 数据库
- Host: localhost:3306
- Database: blog_db
- User: root
- Password: mq060403

## 技术栈
- 前端: Vue 3 + Vite + Element Plus + ECharts
- 后端: Spring Boot 3.4 + MyBatis-Plus + MySQL
- 部署: Nginx + systemd