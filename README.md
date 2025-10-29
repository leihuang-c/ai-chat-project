# ai-chat-project

一个基于现代微服务架构的全栈智能聊天问答项目。项目提供用户管理、聊天交互等功能，并通过 CI/CD 流水线实现自动化部署。

## 🌟 功能特性

- **用户系统** - 用户注册、登录、个人信息管理
- **问答系统** - 提问、回答、内容管理
- **微服务架构** - Spring Cloud 驱动的分布式系统
- **API 网关** - 统一的 API 入口和路由控制
- **自动化部署** - 完整的 CI/CD 流水线支持

## 🏗 技术架构

### 后端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.x | 后端主框架 |
| Spring Cloud | 2021.0.x | 微服务治理 |
| Spring Cloud Gateway | 3.1.x | API 网关 |
| MySQL | 8.0 | 主数据库 |
| JPA/Hibernate | - | ORM 框架 |
| Nacos | 2.2.x | 服务发现和配置中心 |

### 前端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Next.js | 14+ | React 框架 |
| TypeScript | 5+ | 类型安全 |
| Tailwind CSS | 3+ | 样式框架 |

## 📁 项目结构
ai-chat-platform/
├── backend-services/  # 后端微服务
│ ├── api-gateway/     # API 网关服务
│ ├── user-service/    # 用户服务
│ └── chat-service/    # 问答服务
├── frontend-nextjs/   # Next.js 前端
├── docker-compose.yml # 容器编排
└── README.md

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Docker 20.10+
- Docker Compose 2.0+
- next.js 16.0.0+