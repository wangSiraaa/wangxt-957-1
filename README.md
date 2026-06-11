# 医院陪护证管理系统

## 系统简介

医院陪护证管理系统是一个集申请、审核、查验、失效管理于一体的全流程管理系统，旨在规范医院陪护人员管理，提高医院管理效率。

## 功能模块

### 1. 陪护申请管理
- 家属提交陪护申请
- 支持患者信息选择
- 陪护人员信息录入
- 申请状态查询

### 2. 护士站审核
- 待审核申请列表
- 审核通过/拒绝
- 审核意见录入
- 审核历史查询

### 3. 门禁查验
- 陪护证号/身份证号查验
- 入院/出院查验
- 查验结果实时展示
- 查验记录查询

### 4. 陪护证管理
- 陪护证列表查询
- 证件详情查看
- 证件失效处理
- 失效管理统计

## 业务规则

1. **隔离病区限制**：隔离病区不能新增陪护
2. **陪护人数限制**：陪护人数超过床位限制时拒绝申请
3. **证件有效期**：身份证过期不能申请陪护、不能入院
4. **陪护证有效期**：过期的陪护证自动失效
5. **全程留痕**：申请、审核、查验、失效操作均有记录

## 技术栈

### 后端
- Java 8
- Spring Boot 2.7.x
- MyBatis-Plus 3.5.x
- MySQL 8.0
- Knife4j (API文档)

### 前端
- Vue 3
- Element Plus
- Vite
- Vue Router
- Axios

### 部署
- Docker
- Docker Compose
- Nginx

## 快速开始

### 方式一：Docker Compose 部署（推荐）

```bash
# 1. 克隆或下载项目
cd 957

# 2. 复制环境变量文件
cp .env.example .env

# 3. 启动服务
docker-compose up -d

# 4. 访问系统
# 前端: http://localhost
# 后端API: http://localhost:8080/api
# API文档: http://localhost:8080/api/doc.html
```

### 方式二：本地开发运行

#### 后端启动

```bash
cd backend

# 1. 配置数据库
# 修改 src/main/resources/application.yml 中的数据库连接信息

# 2. 初始化数据库
# 执行 src/main/resources/sql/init.sql

# 3. 启动项目
mvn spring-boot:run
```

#### 前端启动

```bash
cd frontend

# 1. 安装依赖
npm install

# 2. 启动开发服务器
npm run dev
```

## 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 护士站 | nurse001 | 123456 | 内科一病区护士 |
| 护士站 | nurse002 | 123456 | 外科一病区护士 |
| 门禁 | gate001 | 123456 | 门禁人员 |
| 管理员 | admin | 123456 | 系统管理员 |

## 接口文档

系统集成了 Knife4j API 文档，启动后端后访问：
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- Knife4j: `http://localhost:8080/api/doc.html`

### 主要接口

#### 申请相关
- `POST /api/apply/submit` - 提交陪护申请
- `GET /api/apply/{id}` - 获取申请详情
- `GET /api/apply/page` - 分页查询申请列表

#### 审核相关
- `POST /api/audit/audit` - 审核陪护申请
- `GET /api/audit/pending` - 获取待审核列表

#### 门禁相关
- `GET /api/gate/check/{certNoOrIdCard}` - 查询陪护证信息
- `POST /api/gate/check` - 门禁查验
- `GET /api/gate/records` - 查验记录列表

#### 陪护证相关
- `GET /api/certificate/{id}` - 获取陪护证详情
- `GET /api/certificate/page` - 分页查询陪护证
- `POST /api/certificate/invalid` - 陪护证失效

## 数据库表结构

| 表名 | 说明 |
|------|------|
| ward | 病区表 |
| patient | 患者表 |
| accompany_person | 陪护人员表 |
| accompany_apply | 陪护申请表 |
| accompany_certificate | 陪护证表 |
| gate_record | 门禁出入记录表 |
| sys_user | 系统用户表 |

## 目录结构

```
957/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/hospital/accompany/
│   │   │   │   ├── controller/  # 控制器层
│   │   │   │   ├── service/     # 服务层
│   │   │   │   ├── mapper/      # 数据访问层
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── vo/          # 视图对象
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── common/      # 通用类
│   │   │   │   ├── config/      # 配置类
│   │   │   │   └── AccompanyApplication.java
│   │   │   └── resources/
│   │   │       ├── mapper/      # MyBatis XML
│   │   │       ├── sql/         # SQL脚本
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/               # 页面组件
│   │   ├── api/                 # API接口
│   │   ├── router/              # 路由配置
│   │   ├── utils/               # 工具函数
│   │   ├── styles/              # 样式文件
│   │   ├── layout/              # 布局组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── public/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── vite.config.js
│   └── package.json
├── docker-compose.yml           # Docker Compose配置
├── .env.example                 # 环境变量示例
└── README.md
```

## 业务流程图

```
家属提交申请 → 护士站审核 → [审核通过 → 生成陪护证] / [审核拒绝]
                          ↓
                    门禁查验（入院/出院）
                          ↓
                    失效管理（主动失效/过期失效）
```

## 注意事项

1. 系统默认使用MySQL数据库，需确保数据库服务正常运行
2. Docker部署方式会自动初始化数据库和测试数据
3. 生产环境请修改默认密码和敏感配置
4. 建议配置HTTPS确保数据传输安全

## 许可证

MIT License
