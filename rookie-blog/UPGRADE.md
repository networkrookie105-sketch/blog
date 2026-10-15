# Java 21 / Spring Boot 3.5 升级

所有后端模块统一使用 Java 21、Spring Boot 3.5.16。前端 API 地址、请求格式和业务响应结构保持兼容，数据库表结构无需因本次升级而修改。

## 兼容性修改

- Maven 使用 `release=21` 和 `parameters=true`，Docker 构建与运行镜像均切换至 Eclipse Temurin 21。
- Servlet、Bean Validation、PostConstruct 迁移到 Jakarta 包名。
- Spring Security 使用 SecurityFilterChain、EnableMethodSecurity 和 lambda DSL；登录配置使用 AbstractHttpConfigurer。Token 过滤器只在 Security 链运行，禁用重复 Servlet 注册。
- MyBatis-Plus 升至 3.5.12，使用 Boot 3 Starter，并显式添加 JSQLParser 分页模块。MySQL 驱动迁移至 com.mysql:mysql-connector-j。
- 移除 Springfox/Swagger2，模型和控制器改用 OpenAPI 3 注解。springdoc 2.8.17 生成文档，Knife4j 4.5.0 UI 保留 `/doc.html`；不引入与新版 springdoc 不兼容的 Knife4j 自动配置。
- OpenAPI 默认关闭，dev 环境启用，保留前台/后台两组接口及文档元信息。
- 测试 JVM 显式加载 Mockito agent，兼容 Java 21 的 instrumentation 机制，避免运行时自附加。
- Jackson、Caffeine 和 MySQL 驱动版本交由 Boot 管理；MapStruct 1.6.3 配合 Lombok 的绑定处理器生成映射代码。
- Docker Compose 的 JWT_SECRET 支持环境变量覆盖，开发默认值为合法 Base64；自定义值须解码后至少为 64 字节。

## 构建与运行

确认 `java -version` 与 `mvn -version` 都显示 Java 21，Maven 至少 3.6.3，推荐 3.9.x。

```bash
cd rookie-blog
mvn clean verify
mvn install -DskipTests
mvn -pl rookie-web spring-boot:run
```

或直接运行构建产物：

```bash
java -jar rookie-web/target/rookie-web-0.0.1-SNAPSHOT.jar
```

后端运行需要初始化好的 MySQL 数据库；上传需要 MinIO。现有数据库无需重新导入 schema.sql。

## 回归测试

原有占位测试及无关外部文件下载测试替换为 10 项本地回归测试：日期 JSON 兼容、JWT 签发/校验、黑名单、Jakarta 校验，以及完整 Spring 容器中的登录/登出、错误密码/非法 Token、方法权限、公共参数校验/CORS、OpenAPI 分组/Knife4j、分页/批量插入。

完整容器测试使用 H2 的 MySQL 兼容模式，不连接真实 MySQL 或 MinIO，不改动现有数据库。H2 用于校验框架集成；真实 MySQL 与 MinIO 的部署联调仍需在目标环境执行。
