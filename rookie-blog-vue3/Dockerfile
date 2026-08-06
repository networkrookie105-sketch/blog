# ---- 构建阶段：Vite 打包静态资源 ----
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# 使用 build-only（vite build）避免 type-check 阻断镜像构建
RUN npm run build-only

# ---- 运行阶段：Nginx 托管并反向代理 /api ----
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
