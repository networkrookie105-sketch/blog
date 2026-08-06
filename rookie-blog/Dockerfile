# ---- 构建阶段：Maven 编译打包 ----
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
# 先复制 POM 以尽量利用依赖缓存
COPY pom.xml .
COPY rookie-common/pom.xml rookie-common/
COPY rookie-jwt/pom.xml rookie-jwt/
COPY rookie-admin/pom.xml rookie-admin/
COPY rookie-web/pom.xml rookie-web/
# 复制源码并打包（入口/可执行 jar 在 rookie-web 模块）
COPY . .
RUN mvn -q -DskipTests clean package

# ---- 运行阶段：仅保留 JRE 与 jar ----
FROM openjdk:8-jre-alpine
WORKDIR /app
ENV TZ=Asia/Shanghai
COPY --from=build /app/rookie-web/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
