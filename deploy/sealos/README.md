# WuyouBlog - Sealos 部署指南

## 项目架构

`
用户浏览器
    │
    ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Frontend App    │────▶│  Backend App     │────▶│  MySQL 数据库    │
│  (Nginx + Vue)   │     │  (Spring Boot)   │     │  (Sealos DB)    │
│  Port: 80 公开    │     │  Port: 8080 内部  │     │  Port: 3306 内部  │
└─────────────────┘     └─────────────────┘     └─────────────────┘
`

---

## 第一步：构建并推送镜像

在**项目根目录** E:\my_blog 执行以下命令，替换 your-dockerhub-username 为你的 Docker Hub 用户名。

### 构建后端镜像

`ash
docker build -t your-dockerhub-username/myblog-backend:latest -f deploy/sealos/Dockerfile.backend .
docker push your-dockerhub-username/myblog-backend:latest
`

### 构建前端镜像

`ash
docker build -t your-dockerhub-username/myblog-frontend:latest -f deploy/sealos/Dockerfile.frontend .
docker push your-dockerhub-username/myblog-frontend:latest
`

> 也可以推送到 Sealos 自带的镜像仓库或其他私有仓库（阿里云 ACR、腾讯云 TCR 等）。

---

## 第二步：在 Sealos 创建 MySQL 数据库

1. 打开 [Sealos 桌面](https://hzh.sealos.run/)
2. 点击左侧 **数据库** → **新建数据库**
3. 选择 **MySQL**，配置：
   - 数据库名：log_db
   - 用户名：oot（或自定义）
   - 密码：设定一个安全密码
4. 创建完成后，记录以下连接信息：
   - **内网地址**（类似 mysql-xxx.sealos.svc.cluster.local）
   - **端口**：3306
   - **用户名** 和 **密码**

---

## 第三步：部署后端

1. 在 Sealos 桌面点击 **应用管理** → **新建应用**
2. 填写：
   - **应用名**：myblog-backend（**必须用这个名字**，与前端约定）
   - **镜像**：your-dockerhub-username/myblog-backend:latest
   - **端口**：8080
   - **对外暴露**：关闭（后端只需内部访问）
3. 在 **环境变量** 中添加：

| 变量名 | 值 |
|--------|-----|
| MYSQL_HOST | MySQL 内网地址 |
| MYSQL_PORT | 3306 |
| MYSQL_DATABASE | log_db |
| MYSQL_USERNAME | 数据库用户名 |
| MYSQL_PASSWORD | 数据库密码 |
| UPLOAD_DIR | /opt/myblog/uploads/ |

4. 点击 **部署**

---

## 第四步：初始化数据库表结构

### 方式一：通过 Sealos 数据库管理界面
1. 进入 Sealos **数据库** → 找到你的 MySQL → 点击 **连接**
2. 将 deploy/blog_db.sql 的内容复制到 SQL 终端执行

### 方式二：通过终端
`ash
kubectl exec -it <mysql-pod-name> -- mysql -u root -p blog_db < deploy/blog_db.sql
`

---

## 第五步：部署前端

1. 在 Sealos 桌面点击 **应用管理** → **新建应用**
2. 填写：
   - **应用名**：myblog-frontend
   - **镜像**：your-dockerhub-username/myblog-frontend:latest
   - **端口**：80
   - **对外暴露**：**开启**（让用户访问）
3. 在 **环境变量** 中添加：

| 变量名 | 值 |
|--------|-----|
| BACKEND_HOST | myblog-backend |

4. 如需绑定自定义域名，在 **网络** 中配置域名
5. 点击 **部署**

---

## 部署完成！

Sealos 会为前端分配一个公网地址（或你的自定义域名），访问即可看到博客。

### 默认管理员账号

数据库已预置管理员用户，可通过 API 或数据库直接管理用户。

---

## 常见问题

### 1. 前端访问报 502 / 后端无法连接
- 确认后端应用名是 myblog-backend（与前端 BACKEND_HOST 一致）
- 确认后端容器端口是 8080
- 在后端 Pod 终端中执行 curl http://myblog-backend:8080/api/ 测试

### 2. 数据库连接失败
- 确认环境变量 MYSQL_HOST 使用的是 Sealos 提供的**内网地址**
- 确认两个应用和数据库在**同一集群**

### 3. 图片上传后丢失
- 当前文件存储在容器内，Pod 重启会丢失
- 如需持久化：在 Sealos 中为后端挂载**对象存储**，修改 UPLOAD_DIR 指向挂载路径

### 4. 如何更新部署
- 重新构建镜像并推送（相同 tag）
- 在 Sealos 应用管理中找到对应应用 → **重启**，会自动拉取最新镜像
