# 红色教育平台 - 后端项目

## 项目简介

红色教育资源数字化管理平台后端系统，基于SpringBoot 2.7.18开发，采用前后端分离架构。

## 技术栈

- **核心框架**: Spring Boot 2.7.18
- **安全框架**: Spring Security + JWT
- **持久层**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **API文档**: Knife4j (Swagger) 3.0.3
- **工具库**: Hutool、Lombok、FastJSON

## 项目结构

```
red-education-platform/
├── database/              # 数据库脚本
│   ├── schema.sql        # 表结构（17张表）
│   └── data.sql          # 初始化数据
├── pom.xml               # Maven配置
├── src/main/
│   ├── java/com/red/education/
│   │   ├── RedEducationApplication.java  # 启动类
│   │   ├── common/       # 公共模块
│   │   │   ├── config/   # 配置类
│   │   │   ├── exception/# 异常处理
│   │   │   ├── result/   # 统一响应
│   │   │   └── utils/    # 工具类
│   │   ├── security/     # 安全模块
│   │   │   ├── config/   # Security配置
│   │   │   ├── filter/   # JWT过滤器
│   │   │   └── handler/  # 认证处理器
│   │   └── module/       # 业务模块
│   │       └── user/     # 用户模块（已完成）
│   └── resources/
│       └── application.yml
```

## 快速开始

### 1. 环境要求

- JDK 1.8+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行数据库脚本
source /path/to/database/schema.sql
source /path/to/database/data.sql
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/red_education?...
    username: root
    password: your_password
```

**注意**：Redis配置是可选的，如果不使用Redis，可以注释掉相关配置。

### 4. 启动项目

```bash
# 使用Maven
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/red-education-platform-1.0.0.jar
```

### 5. 访问API文档

启动成功后，访问：http://localhost:8080/api/doc.html

## 已实现功能

### ✅ 用户管理模块

- 用户注册（字段验证、密码加密）
- 用户登录（JWT认证、登录日志）
- 个人信息管理
- 修改密码
- 忘记密码（验证码重置，控制台模式）

**API接口**：
- `POST /user/register` - 用户注册
- `POST /user/login` - 用户登录
- `GET /user/info` - 获取用户信息
- `PUT /user/info` - 更新用户信息
- `PUT /user/password` - 修改密码
- `POST /user/reset-code` - 发送重置验证码
- `POST /user/reset-password` - 重置密码

### 测试账号

- 系统管理员：`admin` / `admin123`
- 内容管理员：`content_admin` / `admin123`
- 普通用户：`testuser` / `user123`

## 核心特性

### 安全性
- ✅ BCrypt密码加密
- ✅ JWT Token认证
- ✅ 基于角色的权限控制（RBAC）
- ✅ 登录日志记录
- ✅ 参数校验

### 开发效率
- ✅ MyBatis-Plus自动CRUD
- ✅ 全局异常处理
- ✅ 统一响应格式
- ✅ Swagger API文档
- ✅ Hutool工具库

### 功能特色
- ✅ 验证码控制台输出（无需配置邮件服务）
- ✅ Redis缓存支持（可选）
- ✅ 详细的操作日志
- ✅ 完整的数据验证

## 待开发模块

根据需求文档，以下模块待实现：

1. **课程学习模块** - 在线学习、学习记录、学习报告
2. **在线测试模块** - 题库管理、在线答题、自动评分、错题本
3. **红色资源模块** - 资源浏览、上传、审核
4. **互动交流模块** - 发帖、评论、点赞
5. **积分商城模块** - 商品管理、积分兑换
6. **系统管理模块** - 后台管理、数据统计

## 开发规范

- 遵循阿里巴巴Java开发规范
- 使用RESTful API设计风格
- 统一的异常处理和响应格式
- 完善的代码注释

## 注意事项

### 验证码功能
当前验证码采用**控制台输出模式**，验证码会直接打印在控制台中，方便开发测试。

示例输出：
```
=== 邮件发送（控制台模式） ===
收件人: user@example.com
主题: 【红色教育平台】密码重置验证码
内容: 您好，您正在重置密码，验证码为：123456
============================
```

### Redis配置
如果本地没有Redis，可以注释掉`application.yml`中的Redis相关配置，不影响其他功能运行。

## 联系方式

如有问题，请查看项目文档或联系开发团队。

---

**版本**: v1.0.0  
**最后更新**: 2025-11-27
