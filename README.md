# Rookie Blog · 菜鸟博客

一个基于 **Spring Boot + Vue 3** 的前后端分离个人博客系统。后端提供 RESTful API，前端为深色炫酷风格（Aurora Dark）的单页应用，涵盖文章、分类、标签、归档、评论、全文搜索，以及一套完整的后台管理与数据看板。

> 仓库：`git@gitee.com:NaiSiTuMaiTiYou/blog.git`

**语言：** 简体中文 | [English](./README.en.md)

---

## 目录

- [界面预览](#界面预览)
- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [架构说明](#架构说明)
- [快速开始](#快速开始)
  - [环境要求](#环境要求)
  - [数据库初始化](#数据库初始化)
  - [启动后端](#启动后端)
  - [启动前端](#启动前端)
- [配置说明](#配置说明)
- [Docker 部署](#docker-部署)
- [接口概览](#接口概览)
- [常见问题](#常见问题)
- [路线图](#路线图)

---

## 界面预览

> 截图占位：将实际截图放入 `docs/screenshots/` 目录后，替换下方图片链接即可。

| 前台首页 | 文章详情 |
|:---:|:---:|
| ![首页](docs/screenshots/home.png) | ![详情](docs/screenshots/article.png) |

| 后台看板 | 评论管理 |
|:---:|:---:|
| ![看板](docs/screenshots/dashboard.png) | ![评论](docs/screenshots/comment.png) |

---

## 功能特性

### 前台（访客）
- 🏠 **首页**：精选文章 + 文章列表，置顶优先展示
- 📖 **文章详情**：Markdown 渲染、上一篇/下一篇、阅读量自增
- 💬 **评论系统**：游客评论 + 楼中楼回复
- 🔍 **全文搜索**：按标题 / 正文关键字检索
- 🗂️ **分类 / 标签 / 归档**：多维度聚合浏览
- 🎨 **深色炫酷主题**：极光氛围背景、玻璃拟态卡片、渐变标题、滚动进场动效

### 后台（管理员）
- 📝 **文章管理**：发布 / 编辑 / 删除，支持**草稿**与**置顶**
- 🏷️ **分类 / 标签管理**：增删查 + 下拉选择
- 🛡️ **评论管理**：审核（通过 / 拒绝）、删除（含楼中楼）
- 📊 **数据看板**：文章 / 分类 / 标签 / 评论数、总阅读量、近 7 天发布趋势
- 🖼️ **文件上传**：基于 MinIO 的图片存储
- ⚙️ **博客设置**：站点信息、社交主页配置

### 工程能力
- 🔐 JWT 无状态认证 + Spring Security 角色鉴权 + **登出黑名单**
- ⚡ **Caffeine 本地缓存**（分类 / 标签 / 博客设置等热点数据）
- 🌐 全局 **CORS** 跨域支持
- 🧾 统一响应体、全局异常处理、AOP 接口日志
- 🐢 P6Spy 慢 SQL 监控、Knife4j 在线接口文档

---

## 技术栈

### 后端
| 分类 | 技术 |
|------|------|
| 基础框架 | Spring Boot 2.6.3、Java 8 |
| 安全认证 | Spring Security、JJWT 0.11.2 |
| 持久层 | MyBatis-Plus 3.5.2、MySQL、HikariCP |
| 本地缓存 | Spring Cache + Caffeine |
| 对象存储 | MinIO |
| 接口文档 | Knife4j（Swagger2 增强） |
| 内容渲染 | commonmark（Markdown → HTML） |
| 其它 | MapStruct、Lombok、P6Spy、Guava |

### 前端
| 分类 | 技术 |
|------|------|
| 框架 | Vue 3.3、Vue Router 4、Pinia |
| 构建 | Vite 4 |
| UI | Element Plus 2.4、Tailwind CSS 3.3、Flowbite |
| 编辑器 | md-editor-v3 |
| 网络 | axios |
| 体验 | animate.css、nprogress |

---

## 项目结构

```
blog/                            # 单仓库根目录
├── README.md
├── .gitignore
├── rookie-blog/                 # 后端（Maven 多模块）
│   ├── pom.xml                  # 父 POM，统一版本管理
│   ├── rookie-common/           # 通用模块：DO/Mapper、统一响应、异常、缓存、AOP、CORS
│   ├── rookie-jwt/              # 认证模块：JWT 生成/校验、过滤器、Token 黑名单
│   ├── rookie-admin/            # 后台业务：文章/分类/标签/评论/用户/看板/文件
│   ├── rookie-web/              # 前台业务 + 程序入口/打包
│   └── sql/
│       ├── schema.sql           # 全新环境建表脚本（含种子数据）
│       └── migration_p1.sql     # 存量库增量迁移脚本
└── rookie-blog-vue3/            # 前端（Vue 3 + Vite）
    └── src/
        ├── api/                 # 接口封装（frontend / admin）
        ├── pages/               # 页面（frontend / admin）
        ├── layouts/             # 前台 / 后台布局与组件
        ├── components/          # 通用组件（评论区等）
        ├── stores/              # Pinia 状态
        ├── router/              # 路由
        └── assets/main.css      # 全局样式 + Aurora Dark 设计系统
```

### 后端模块依赖
```
rookie-web ──► rookie-admin ──► rookie-common
     └────────► rookie-jwt ────►────┘
```
> 程序入口与最终打包在 `rookie-web`，它扫描 `com.rookie.*` 加载全部模块。

---

## 架构说明

- **前后端分离**：前端通过 Vite 代理 `/api` 转发到后端 `http://localhost:8080`。
- **认证流程**：`POST /login` → 校验用户名密码 → 签发 JWT；后续请求携带 `Authorization: Bearer <token>`，由 `TokenAuthenticationFilter` 校验并重建认证上下文；登出后 Token 进入黑名单立即失效。
- **鉴权**：`/admin/**` 需登录，写操作使用 `@PreAuthorize("hasRole('ROLE_ADMIN')")`。
- **缓存**：分类列表、标签列表、博客设置使用 Caffeine 缓存，对应写操作通过 `@CacheEvict` 主动失效。
- **数据模型**：文章元信息（`t_article`）与正文（`t_article_content`）分表；文章与分类 / 标签通过关联表关联；评论采用「一级评论 + 楼中楼」模型。

---

## 快速开始

### 环境要求
- JDK **1.8**
- Maven 3.6+
- MySQL **5.7+**
- Node.js **16+**（推荐 18）
- （可选）MinIO —— 仅图片上传功能需要

### 数据库初始化

1. 确保本地 MySQL 已启动（默认 `127.0.0.1:3306`）。
2. 全新环境：直接执行建表脚本（含默认管理员与博客设置）：
   ```sql
   source rookie-blog/sql/schema.sql
   ```
   > 默认管理员账号：**admin / 123456**（密码为 BCrypt 密文，脚本已内置）。
3. 已有旧库（升级 P1 功能）：执行增量脚本：
   ```sql
   source rookie-blog/sql/migration_p1.sql
   ```

> 提示：用命令行导入含中文注释的脚本时，建议加 `--default-character-set=utf8mb4` 避免乱码。

### 启动后端

```bash
cd rookie-blog

# 安装依赖（首次或改动后）
mvn -DskipTests install

# 启动 Web 模块（默认端口 8080，dev 环境）
mvn -pl rookie-web spring-boot:run
```

启动成功后：
- 接口文档：http://localhost:8080/doc.html

### 启动前端

```bash
cd rookie-blog-vue3

# 安装依赖
npm install

# 启动开发服务器（默认端口 5173）
npm run dev
```

访问：
- 前台首页：http://localhost:5173/
- 后台登录：http://localhost:5173/#/login （admin / 123456）

前端生产构建：
```bash
npm run build      # 产物输出到 dist/
npm run preview    # 本地预览构建产物
```

---

## 配置说明

后端敏感配置已**外部化**，支持通过环境变量覆盖（不配则使用本地默认值），配置文件位于 `rookie-blog/rookie-web/src/main/resources/`。

| 环境变量 | 说明 | 默认值 |
|----------|------|--------|
| `DB_URL` | 数据库连接串 | 本地 `rookieblog` 库 |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `123456` |
| `JWT_SECRET` | JWT 签名密钥 | 内置默认值（生产务必覆盖） |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | Security 内置账号 | `admin` / `123456` |
| `MINIO_ENDPOINT` | MinIO 地址 | 见配置文件 |
| `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` | MinIO 密钥 | 见配置文件 |
| `MINIO_BUCKET` | MinIO 桶名 | `rookieblog` |

> ⚠️ 生产环境请务必通过环境变量覆盖数据库密码、JWT 密钥与 MinIO 密钥，切勿使用默认值。

多环境配置：`application.yml`（公共）、`application-dev.yml`、`application-test.yml`、`application-prod.yml`，通过 `spring.profiles.active` 切换。

---

## Docker 部署

> 项目已内置以下文件，无需手动创建，可直接一键启动：
> `rookie-blog/Dockerfile`、`rookie-blog-vue3/Dockerfile`、`rookie-blog-vue3/nginx.conf`、根目录 `docker-compose.yml`（含 MySQL 健康检查，后端会等待数据库就绪后再启动）。
>
> ```bash
> docker compose up -d --build
> ```
> 前台 http://localhost/ ，后端 http://localhost:8080/doc.html 。生产环境务必修改数据库密码与 `JWT_SECRET`。
>
> 以下为各文件内容参考。

### 1. 后端打包镜像

在 `rookie-blog/` 下新建 `Dockerfile`：

```dockerfile
# ---- 构建阶段 ----
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests clean package

# ---- 运行阶段 ----
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /app/rookie-web/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. 前端打包镜像

在 `rookie-blog-vue3/` 下新建 `Dockerfile`（Nginx 托管静态产物并反代 `/api`）：

```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# 需自备 nginx.conf：将 /api 反向代理到后端 8080
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### 3. docker-compose 一键编排

在仓库根目录新建 `docker-compose.yml`：

```yaml
version: "3.8"
services:
  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: "123456"
      MYSQL_DATABASE: rookieblog
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./rookie-blog/sql/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  backend:
    build: ./rookie-blog
    depends_on:
      - mysql
    environment:
      DB_URL: "jdbc:p6spy:mysql://mysql:3306/rookieblog?useUnicode=true&characterEncoding=UTF-8&useSSL=false"
      DB_USERNAME: root
      DB_PASSWORD: "123456"
      JWT_SECRET: "请替换为你自己的强随机密钥"
    ports:
      - "8080:8080"

  frontend:
    build: ./rookie-blog-vue3
    depends_on:
      - backend
    ports:
      - "80:80"

volumes:
  mysql-data:
```

启动：

```bash
docker compose up -d --build
```

- 前台：http://localhost/
- 后端：http://localhost:8080/doc.html

> 说明：`schema.sql` 会在 MySQL 容器首次初始化时自动执行；生产环境请务必修改数据库密码与 `JWT_SECRET`。

---

## 接口概览

> 统一返回结构：`{ success, message, errorCode, data }`；分页额外包含 `current / size / total / pages`。

### 前台接口（无需登录）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/login` | 登录，返回 JWT |
| POST | `/article/list` | 首页文章分页 |
| POST | `/detail` | 文章详情（阅读量 +1） |
| POST | `/search` | 全文搜索 |
| POST | `/archive/list` | 归档分页 |
| POST | `/category/list` | 分类列表 |
| POST | `/category/article/list` | 分类下文章 |
| POST | `/tag/list` | 标签列表 |
| POST | `/tag/article/list` | 标签下文章 |
| POST | `/blog/settings/detail` | 博客设置 |
| POST | `/comment/publish` | 发表评论 |
| POST | `/comment/list` | 文章评论（楼中楼树形） |

### 后台接口（需 Token + 管理员角色）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin/logout` | 退出登录（Token 拉黑） |
| POST | `/admin/article/{publish,delete,list,detail,update}` | 文章管理 |
| POST | `/admin/category/*` | 分类管理 |
| POST | `/admin/tag/*` | 标签管理 |
| POST | `/admin/comment/{list,update/status,delete}` | 评论管理 |
| GET  | `/admin/dashboard/statistics` | 数据看板统计 |
| POST | `/admin/file/*` | 文件上传（MinIO） |
| POST | `/admin/blogSettings/*` | 博客设置 |
| POST | `/admin/user/*` | 用户信息 / 改密 |

完整接口请查看 Knife4j 文档：`http://localhost:8080/doc.html`。

---

## 常见问题

**Q：前端请求 401 / 跨域失败？**
后端已配置全局 CORS 与 JWT 过滤器；请确认后端已在 8080 启动、前端 `/api` 代理指向正确。

**Q：启动报 `Unknown column 'status' / 't_comment' doesn't exist`？**
说明数据库未升级到 P1，请执行 `rookie-blog/sql/migration_p1.sql`。

**Q：登录提示密码错误？**
默认 `admin / 123456`；如自行建库，请确保 `t_user` 中密码为 BCrypt 密文（`schema.sql` 已内置）。

**Q：开发服务器等长时进程如何运行？**
`mvn spring-boot:run` 与 `npm run dev` 均为常驻进程，请在独立终端中手动运行。

---

## 路线图

- [ ] 点赞 / 收藏
- [ ] RSS 订阅 + Sitemap + SEO（TDK / OG）
- [ ] 友情链接、关于页、公告
- [ ] 接口限流防刷
- [ ] 评论邮件通知
- [ ] 图片本地存储备选方案
- [ ] 操作审计日志落库

---

## 致谢

本项目用于学习与实践 Spring Boot + Vue 3 前后端分离开发。欢迎 Star 与交流。
