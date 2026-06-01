# WuyouBlog 开发 & 部署文档

## 本地开发

### 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### 数据库
```sql
CREATE DATABASE blog_db DEFAULT CHARSET utf8mb4;
```
用户名 root，密码 mq060403（见 `backend/src/main/resources/application.yaml`）

### 一键启动
```
start-dev.bat
```
- 后端: http://localhost:8080
- 前端: http://localhost:5173

### 手动启动
```bash
# 后端
cd backend
mvn spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```

## 项目结构
```
E:\my_blog\
├── backend/              # Spring Boot 后端
│   └── src/main/
│       ├── java/com/blog/blog_backend/
│       │   ├── config/       # WebConfig, 拦截器
│       │   ├── controller/   # API 控制器
│       │   ├── entity/       # 数据实体
│       │   ├── mapper/       # MyBatis Mapper
│       │   ├── service/      # 业务逻辑
│       │   └── utils/        # 工具类
│       └── resources/
│           ├── application.yaml      # 本地配置
│           └── application-prod.yaml # 生产配置
├── frontend/             # Vue 3 前端
│   ├── src/
│   │   ├── components/  # 组件
│   │   ├── views/       # 页面
│   │   └── router/      # 路由
│   └── public/          # 静态资源 (图片/视频)
├── deploy/              # 部署文件
│   ├── deploy.bat       # 部署脚本
│   ├── nginx-myblog.conf
│   └── myblog.service
└── start-dev.bat        # 本地启动脚本
```

## 服务器部署

| 项目 | 路径 |
|------|------|
| 后端 JAR | /opt/myblog/myblog.jar |
| 前端文件 | /opt/myblog/frontend/ |
| 上传目录 | /opt/myblog/uploads/ |
| Nginx 配置 | /etc/nginx/conf.d/myblog.conf |
| Systemd 服务 | /etc/systemd/system/myblog.service |

部署命令见 `deploy/deploy.bat`