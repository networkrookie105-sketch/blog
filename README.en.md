# Rookie Blog

A full-stack personal blog system built with **Spring Boot + Vue 3**. The backend exposes a RESTful API, while the frontend is a single-page app featuring a sleek dark "Aurora Dark" theme. It covers articles, categories, tags, archives, comments, and full-text search, plus a complete admin panel with a data dashboard.

> Repository: `git@gitee.com:NaiSiTuMaiTiYou/blog.git`

**Language:** [简体中文](./README.md) | English

---

## Table of Contents

- [Screenshots](#screenshots)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [API Overview](#api-overview)
- [FAQ](#faq)
- [Roadmap](#roadmap)

---

## Screenshots

> Placeholders: put real screenshots into `docs/screenshots/` and update the links below.

| Home | Article Detail |
|:---:|:---:|
| ![home](docs/screenshots/home.png) | ![article](docs/screenshots/article.png) |

| Admin Dashboard | Comment Management |
|:---:|:---:|
| ![dashboard](docs/screenshots/dashboard.png) | ![comment](docs/screenshots/comment.png) |

---

## Features

### Frontend (Visitors)
- 🏠 **Home**: featured article + article list, pinned articles first
- 📖 **Article detail**: Markdown rendering, prev/next navigation, auto read-count increment
- 💬 **Comments**: guest comments + nested replies
- 🔍 **Full-text search**: match by title / content keywords
- 🗂️ **Categories / Tags / Archive**: multi-dimensional browsing
- 🎨 **Dark theme**: aurora background, glassmorphism cards, gradient headings, scroll-reveal animations

### Backend (Admin)
- 📝 **Article management**: create / edit / delete, with **draft** and **pin (top)** support
- 🏷️ **Category / Tag management**: CRUD + select dropdowns
- 🛡️ **Comment moderation**: approve / reject / delete (including nested replies)
- 📊 **Data dashboard**: counts of articles / categories / tags / comments, total reads, 7-day publish trend
- 🖼️ **File upload**: image storage via MinIO
- ⚙️ **Blog settings**: site info & social links

### Engineering
- 🔐 Stateless JWT auth + Spring Security role-based access + **logout blacklist**
- ⚡ **Caffeine local cache** for hot data (categories / tags / blog settings)
- 🌐 Global **CORS** support
- 🧾 Unified response body, global exception handling, AOP request logging
- 🐢 P6Spy slow-SQL monitoring, Knife4j online API docs

---

## Tech Stack

### Backend
| Category | Technology |
|----------|-----------|
| Core | Spring Boot 2.6.3, Java 8 |
| Security | Spring Security, JJWT 0.11.2 |
| Persistence | MyBatis-Plus 3.5.2, MySQL, HikariCP |
| Cache | Spring Cache + Caffeine |
| Object storage | MinIO |
| API docs | Knife4j (Swagger2) |
| Rendering | commonmark (Markdown → HTML) |
| Others | MapStruct, Lombok, P6Spy, Guava |

### Frontend
| Category | Technology |
|----------|-----------|
| Framework | Vue 3.3, Vue Router 4, Pinia |
| Build | Vite 4 |
| UI | Element Plus 2.4, Tailwind CSS 3.3, Flowbite |
| Editor | md-editor-v3 |
| HTTP | axios |
| UX | animate.css, nprogress |

---

## Project Structure

```
blog/                            # monorepo root
├── README.md / README.en.md
├── .gitignore
├── rookie-blog/                 # Backend (Maven multi-module)
│   ├── pom.xml                  # parent POM, dependency management
│   ├── rookie-common/           # shared: DO/Mapper, response, exception, cache, AOP, CORS
│   ├── rookie-jwt/              # auth: JWT gen/verify, filters, token blacklist
│   ├── rookie-admin/            # admin: article/category/tag/comment/user/dashboard/file
│   ├── rookie-web/              # public API + application entry / packaging
│   └── sql/
│       ├── schema.sql           # fresh-install schema (with seed data)
│       └── migration_p1.sql     # incremental migration for existing DBs
└── rookie-blog-vue3/            # Frontend (Vue 3 + Vite)
    └── src/
        ├── api/                 # API wrappers (frontend / admin)
        ├── pages/               # pages (frontend / admin)
        ├── layouts/             # layouts & components
        ├── components/          # shared components (comment section, etc.)
        ├── stores/              # Pinia stores
        ├── router/              # routes
        └── assets/main.css      # global styles + Aurora Dark design system
```

### Backend module dependencies
```
rookie-web ──► rookie-admin ──► rookie-common
     └────────► rookie-jwt ────►────┘
```
> Application entry and final packaging live in `rookie-web`, which scans `com.rookie.*` to load all modules.

---

## Architecture

- **Decoupled front/back end**: the frontend proxies `/api` to the backend at `http://localhost:8080` via Vite.
- **Auth flow**: `POST /login` → validate credentials → issue JWT; subsequent requests carry `Authorization: Bearer <token>`, verified by `TokenAuthenticationFilter`; after logout the token is blacklisted and invalidated immediately.
- **Authorization**: `/admin/**` requires login; write operations use `@PreAuthorize("hasRole('ROLE_ADMIN')")`.
- **Caching**: category list, tag list, and blog settings are cached with Caffeine; corresponding writes evict via `@CacheEvict`.
- **Data model**: article metadata (`t_article`) and content (`t_article_content`) are stored separately; articles relate to categories/tags via relation tables; comments use a "top-level + nested replies" model.

---

## Getting Started

### Requirements
- JDK **1.8**
- Maven 3.6+
- MySQL **5.7+**
- Node.js **16+** (18 recommended)
- (Optional) MinIO — only needed for image uploads

### Database initialization

1. Ensure a local MySQL is running (default `127.0.0.1:3306`).
2. Fresh install — run the schema script (includes default admin & blog settings):
   ```sql
   source rookie-blog/sql/schema.sql
   ```
   > Default admin account: **admin / 123456** (password is a BCrypt hash bundled in the script).
3. Existing DB (upgrading to P1 features) — run the migration:
   ```sql
   source rookie-blog/sql/migration_p1.sql
   ```

> Tip: when importing scripts with Chinese comments from the CLI, add `--default-character-set=utf8mb4` to avoid garbled text.

### Run the backend

```bash
cd rookie-blog
mvn -DskipTests install
mvn -pl rookie-web spring-boot:run   # port 8080, dev profile
```

- API docs: http://localhost:8080/doc.html

### Run the frontend

```bash
cd rookie-blog-vue3
npm install
npm run dev   # port 5173
```

- Home: http://localhost:5173/
- Admin login: http://localhost:5173/#/login (admin / 123456)

Production build:
```bash
npm run build     # output to dist/
npm run preview
```

---

## Configuration

Sensitive backend settings are **externalized** and can be overridden via environment variables (falling back to local defaults). Config files live in `rookie-blog/rookie-web/src/main/resources/`.

| Env var | Description | Default |
|---------|-------------|---------|
| `DB_URL` | JDBC connection string | local `rookieblog` DB |
| `DB_USERNAME` | DB username | `root` |
| `DB_PASSWORD` | DB password | `123456` |
| `JWT_SECRET` | JWT signing secret | built-in default (override in prod) |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | Security built-in account | `admin` / `123456` |
| `MINIO_ENDPOINT` | MinIO endpoint | see config |
| `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` | MinIO keys | see config |
| `MINIO_BUCKET` | MinIO bucket | `rookieblog` |

> ⚠️ In production, always override the DB password, JWT secret, and MinIO keys — never keep the defaults.

Multi-env config: `application.yml` (shared), `application-dev.yml`, `application-test.yml`, `application-prod.yml`, switched via `spring.profiles.active`.

---

## Docker Deployment

> Reference configs — adjust versions, paths, and secrets as needed.

### 1. Backend image

Create `Dockerfile` under `rookie-blog/`:

```dockerfile
# ---- build ----
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests clean package

# ---- run ----
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /app/rookie-web/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Frontend image

Create `Dockerfile` under `rookie-blog-vue3/` (Nginx serves static build and proxies `/api`):

```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# provide your own nginx.conf that proxies /api to the backend at :8080
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### 3. docker-compose

Create `docker-compose.yml` at the repo root:

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
      JWT_SECRET: "replace-with-your-own-strong-secret"
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

Start:

```bash
docker compose up -d --build
```

- Frontend: http://localhost/
- Backend: http://localhost:8080/doc.html

> Note: `schema.sql` runs automatically on the MySQL container's first init. Always change the DB password and `JWT_SECRET` in production.

---

## API Overview

> Unified response: `{ success, message, errorCode, data }`; paginated responses also include `current / size / total / pages`.

### Public APIs (no login)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/login` | login, returns JWT |
| POST | `/article/list` | home article page |
| POST | `/detail` | article detail (read count +1) |
| POST | `/search` | full-text search |
| POST | `/archive/list` | archive page |
| POST | `/category/list` | category list |
| POST | `/category/article/list` | articles in a category |
| POST | `/tag/list` | tag list |
| POST | `/tag/article/list` | articles for a tag |
| POST | `/blog/settings/detail` | blog settings |
| POST | `/comment/publish` | post a comment |
| POST | `/comment/list` | article comments (nested tree) |

### Admin APIs (token + admin role)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/admin/logout` | logout (blacklist token) |
| POST | `/admin/article/{publish,delete,list,detail,update}` | article management |
| POST | `/admin/category/*` | category management |
| POST | `/admin/tag/*` | tag management |
| POST | `/admin/comment/{list,update/status,delete}` | comment moderation |
| GET  | `/admin/dashboard/statistics` | dashboard stats |
| POST | `/admin/file/*` | file upload (MinIO) |
| POST | `/admin/blogSettings/*` | blog settings |
| POST | `/admin/user/*` | user info / change password |

Full API reference: Knife4j at `http://localhost:8080/doc.html`.

---

## FAQ

**Q: 401 / CORS errors from the frontend?**
The backend ships global CORS and a JWT filter; make sure the backend is running on 8080 and the `/api` proxy points to it.

**Q: Startup error `Unknown column 'status' / 't_comment' doesn't exist`?**
The DB hasn't been upgraded to P1 — run `rookie-blog/sql/migration_p1.sql`.

**Q: Login says wrong password?**
Default is `admin / 123456`; if you built the DB yourself, ensure `t_user` stores a BCrypt hash (bundled in `schema.sql`).

**Q: How to run the long-lived dev servers?**
`mvn spring-boot:run` and `npm run dev` are long-running processes — run them in separate terminals.

---

## Roadmap

- [ ] Likes / favorites
- [ ] RSS + Sitemap + SEO (TDK / OG)
- [ ] Friend links, About page, announcements
- [ ] API rate limiting
- [ ] Email notifications for comments
- [ ] Local image storage alternative
- [ ] Persisted audit logs

---

## Acknowledgements

Built for learning and practicing full-stack development with Spring Boot + Vue 3. Stars and feedback are welcome.
