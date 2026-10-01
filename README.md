# 学生综合信息管理系统 · Web 后端（Spring Boot 3）

> 前后端分离 · RESTful API · JWT 鉴权 · MyBatis-Plus
>
> 🌐 **在线演示：** http://47.98.192.161
> （用户名 `admin` / 密码 `123456`）
> （用户名 `T001` / 密码 `123456`）
> （用户名 `202401` / 密码 `123456`）

## 项目简介

基于 Spring Boot 3.4.1 构建的学生信息管理系统后端，提供完整的 RESTful 接口。采用 JWT 无状态鉴权，通过拦截器实现接口级 RBAC 权限控制；使用 MyBatis-Plus 简化 CRUD，支持分页与多条件模糊查询；成绩总分由后端计算（平时分×0.4 + 期末分×0.6），并配有事务控制。

## 技术栈

- Java 17
- Spring Boot 3.4.1
- MyBatis-Plus 3.5.9
- MySQL 8.0
- JWT（jjwt 0.12.6）
- BCrypt（spring-security-crypto）
- Maven
- Lombok

## 功能接口

| 模块 | 接口 | 说明 |
|---|---|---|
| 认证 | `POST /api/auth/login` | 登录，返回 JWT |
| 用户 | `GET /api/user/me` | 获取当前登录用户 |
| 学生管理 | `GET /api/admin/students` | 分页 + 多条件查询 |
| 学生管理 | `POST /api/admin/students` | 新增学生（同步建账号） |
| 学生管理 | `PUT /api/admin/students/{sid}` | 修改学生 |
| 学生管理 | `DELETE /api/admin/students/{sid}` | 删除学生 |
| 成绩管理 | `GET /api/teacher/students/{sid}/scores` | 查询学生成绩 |
| 成绩管理 | `POST /api/teacher/scores` | 录入/更新成绩 |
| 统计 | `GET /api/admin/statistics/score-distribution` | 成绩分布 |
| 统计 | `GET /api/admin/statistics/class-average` | 班级平均分 |

## 数据库

使用 `student_web.sql` 脚本初始化，共 7 张表，含索引与外键：

| 表名 | 说明 |
|---|---|
| sys_user | 登录用户表（认证与业务分离） |
| student | 学生学籍表 |
| teacher | 教师表 |
| course | 课程表 |
| score | 成绩表（联合主键 sid+cid+term） |
| timetable | 课表 |
| reward_punish | 奖惩表 |

默认账号密码均为 `123456`（BCrypt 密文已预置）。

## 项目结构

```
src/main/java/com/manage/student/
├── controller/   # 接口层
├── service/      # 业务逻辑
├── mapper/       # MyBatis-Plus Mapper
├── entity/       # 数据库实体
├── common/       # 统一返回 Result
├── config/       # 配置（分页、BCrypt、拦截器）
├── interceptor/  # JWT 拦截器
└── utils/        # JwtUtil
```

## 如何运行

1. 确保已安装 JDK 17、Maven、MySQL 8.0
2. 在 MySQL 中执行 `student_web.sql`
3. 修改 `src/main/resources/application.yml` 中的数据库密码
4. 在项目根目录执行：
   ```bash
   mvn spring-boot:run
   ```
5. 访问 `http://localhost:8080/api/auth/login?username=admin&password=123456` 验证

## 部署

- **云服务器**：阿里云 ECS（Ubuntu 22.04，2核2G）
- **反向代理**：Nginx
- **在线地址**：http://47.98.192.161

## 默认账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | admin | 123456 |
| 教师 | T001 | 123456 |
| 学生 | 202401 | 123456 |

## 作者

- 李林罡
- GitHub：[angnury](https://github.com/angnury)