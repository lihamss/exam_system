# 在线考试答题评分系统

技术栈：**Spring Boot 3 + Vue 3 + Element Plus + MySQL**

## 项目结构

```
exam-system/
├── backend/                 # 后端 (Spring Boot)
│   ├── src/main/java/com/exam/
│   │   ├── entity/           # 实体
│   │   ├── mapper/           # MyBatis-Plus Mapper
│   │   ├── service/          # 业务层
│   │   ├── controller/       # 接口
│   │   ├── config/           # 配置、Security、JWT
│   │   ├── security/         # JWT 过滤器
│   │   ├── dto/              # 请求/响应 DTO
│   │   └── common/           # 统一结果封装
│   └── src/main/resources/
│       └── application.yml
├── sql/
│   └── schema.sql            # 数据库建表脚本
├── src/                      # 前端 (Vue 3 + Vite + Element Plus)
│   ├── api/                  # 接口封装
│   ├── views/
│   │   ├── user/             # 学生端：首页、考试中心、我的考试、成绩、个人中心、帮助
│   │   └── admin/            # 管理端：控制台、考试/题库/用户/成绩/试卷管理、系统设置
│   ├── router/
│   ├── stores/
│   └── utils/
├── package.json
└── 功能需求分析.md
```

## 功能概览

- **学生端**：首页公告与考试、考试中心、我的考试、成绩查询、个人中心、帮助中心；在线答题、倒计时、自动保存、交卷后自动评分（客观题）。
- **教师/管理员端**：控制台统计、考试管理、题库管理（单选/多选/判断/填空/简答）、用户管理、成绩管理与批改主观题、试卷管理、系统公告。
- **权限**：RBAC，角色 student / teacher / admin；JWT 鉴权。

## 快速开始

### 1. 数据库

创建库并执行脚本：

```bash
mysql -u root -p < sql/schema.sql
```

或在 MySQL 客户端中执行 `sql/schema.sql`。修改 `backend/src/main/resources/application.yml` 中的 `spring.datasource.url/username/password`。

### 2. 后端

```bash
cd backend
mvn spring-boot:run
```

- 端口：**8080**，上下文路径：**/api**
- 首次启动会自动创建管理员：**用户名 admin，密码 admin123**

### 3. 前端

```bash
npm install
npm run dev
```

- 开发端口：**5174**（见 `vite.config.js`），已配置代理将 `/api` 转发到 `http://localhost:8080`。

### 4. 模拟数据（可选）

执行 `sql/data.sql` 可导入题目、试卷、考试、公告等演示数据。应用启动时会自动创建演示用户：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| teacher | 123456 | 教师 |
| student1 | 123456 | 学生 |
| student2 | 123456 | 学生 |

### 5. 使用

- 打开浏览器访问：`http://localhost:5174`
- 管理员登录：admin / admin123 → 进入管理端
- 学生登录：student1 / 123456 或注册新账号；学生登录后进入学生端

## 接口说明

- 登录：`POST /api/auth/login`，请求体 `{ "username", "password" }`，返回 `{ "token", "username", "realName", "role" }`
- 除 `/api/auth/login`、`/api/auth/register` 外，其余请求需在 Header 中携带：`Authorization: Bearer <token>`
- 统一响应格式：`{ "code": 200, "message": "success", "data": ... }`，失败时 `code` 非 200，`message` 为错误信息

## 文档

- **数据库与功能说明.md**：数据库表结构、表关系、已实现功能清单、模拟数据说明

## 构建与部署

- 前端生产构建：`npm run build`，产物在 `dist/`
- 后端打包：`cd backend && mvn package`，运行：`java -jar target/exam-system-backend-1.0.0.jar`
- 生产环境请修改 JWT 密钥（`application.yml` 中 `jwt.secret`）与数据库密码
