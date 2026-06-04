# AI Workbench — 项目总体规划

> **版本**: v1.0  
> **日期**: 2026-05-27  
> **负责人**: Jason  

---

## 1. 项目愿景

**AI Workbench（AI 工作台）** 是一款面向软件研发团队的一站式 AI 增强开发平台，覆盖从需求分析、原型设计、编码开发、测试验证、构建发布到运维监控的完整软件开发生命周期（SDLC）。

**核心价值主张**：
- 🔗 **全流程贯通**：打破工具孤岛，一个平台完成需求 → 设计 → 开发 → 测试 → 发布 → 运维全链路
- 🤖 **AI 原生**：每个环节内嵌 AI 能力，智能辅助需求分析、原型生成、代码编写、测试生成、日志诊断
- 📊 **数据驱动**：全链路可追溯，需求到代码到发布到线上状态的完整追踪链
- 👥 **团队协作**：内置项目管理、代码审查、评审流程，支持敏捷/Scrum 开发模式

---

## 2. 技术栈

| 层级 | 技术选型 | 版本 |
|------|---------|------|
| **前端框架** | Next.js (App Router) | 16.x |
| **UI 框架** | React + TypeScript | 19.x / 5.x |
| **CSS 方案** | TailwindCSS | 4.x |
| **状态管理** | Zustand | 5.x |
| **后端框架** | Spring Boot | 4.0.x |
| **编程语言** | Java | 21 |
| **数据库** | PostgreSQL | 16.x |
| **ORM** | MyBatis-Plus | 3.5.x |
| **缓存** | Redis | 7.x |
| **对象存储** | 阿里云 OSS | - |
| **搜索引擎** | Elasticsearch | 8.x |
| **消息队列** | RabbitMQ | 3.x |
| **认证** | Spring Security + JWT | - |
| **容器化** | Docker + Docker Compose | - |
| **编排** | Kubernetes | 1.28+ |
| **CI/CD** | GitHub Actions | - |
| **AI 模型** | OpenAI / Claude / Gemini (可插拔) | - |

---

## 3. 项目结构

```
ai-workbench/
├── frontend/                # Next.js 前端应用
│   ├── src/
│   │   ├── app/            # App Router 页面
│   │   ├── components/     # 通用组件
│   │   ├── features/       # 业务模块组件
│   │   ├── hooks/          # 自定义 Hooks
│   │   ├── lib/            # 工具库
│   │   ├── services/       # API 调用层
│   │   ├── stores/         # Zustand 状态管理
│   │   └── types/          # TypeScript 类型定义
│   └── public/             # 静态资源
├── backend/                 # Spring Boot 后端应用
│   └── src/main/java/com/aiworkbench/backend/
│       ├── config/         # 配置类
│       ├── controller/     # 控制器层
│       ├── service/        # 业务逻辑层
│       ├── mapper/         # MyBatis-Plus Mapper 层
│       ├── entity/         # 数据实体
│       ├── dto/            # 数据传输对象
│       ├── security/       # 安全模块
│       ├── ai/             # AI 集成模块
│       ├── common/         # 公共工具
│       └── exception/      # 异常处理
├── docs/                    # 项目文档
├── design/                  # 原型设计稿
├── deploy/                  # 部署配置
│   ├── docker/
│   ├── k8s/
│   └── nginx/
└── scripts/                 # 自动化脚本
```

---

## 4. 核心功能模块

| 编号 | 模块 | 描述 | 优先级 |
|------|------|------|--------|
| M1 | **工作台 Dashboard** | 全局概览、快捷入口、数据统计看板 | P0 |
| M2 | **用户与权限** | 注册/登录、RBAC 权限、组织管理 | P0 |
| M3 | **项目管理** | 项目 CRUD、成员管理、项目配置 | P0 |
| M4 | **需求管理** | 需求采集、评审、变更追踪、AI 需求分析 | P0 |
| M5 | **原型设计** | 拖拽式原型、AI 原型生成、组件库、版本管理 | P1 |
| M6 | **任务管理** | 看板（Kanban）、Sprint 管理、工时统计 | P0 |
| M7 | **代码开发** | Monaco 在线编辑器、Git 集成、代码审查 | P1 |
| M8 | **测试管理** | 用例管理、测试计划、执行记录、AI 生成测试 | P1 |
| M9 | **发布管理** | CI/CD 流水线配置与执行、版本管理、审批流 | P1 |
| M10 | **运维监控** | 应用监控、日志聚合与搜索、告警管理 | P2 |
| M11 | **维护管理** | Bug 追踪、性能分析、知识库 | P2 |
| M12 | **AI 助手** | 统一 AI 对话、各场景 AI 能力调用 | P0 |

---

## 5. 里程碑与时间线

### Phase 1：基础框架 & 核心模块（第 1-4 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W1 | 工程脚手架搭建、开发规范制定 | 前后端工程可运行、CI 流水线 |
| W2 | 用户认证与权限系统 | 注册/登录/JWT/RBAC 完整流程 |
| W3 | 项目管理 + Dashboard 基础 | 项目 CRUD、Dashboard 框架 |
| W4 | 需求管理模块 | 需求 CRUD、评审流程 |

### Phase 2：开发流程模块（第 5-8 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W5 | 任务管理 — Kanban 看板 | 拖拽看板、任务 CRUD |
| W6 | 任务管理 — Sprint & 统计 | Sprint 管理、燃尽图 |
| W7 | AI 助手基础能力 | AI 对话、需求分析 AI |
| W8 | 原型设计模块 | 拖拽画布、组件库、AI 原型生成 |

### Phase 3：代码 & 测试（第 9-12 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W9-10 | 代码开发模块 | Monaco 编辑器、Git 集成 |
| W11 | 代码审查 | CR 流程、评论、AI 代码审查 |
| W12 | 测试管理模块 | 用例管理、执行记录、AI 测试生成 |

### Phase 4：发布 & 运维（第 13-16 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W13-14 | 发布管理 | CI/CD 流水线、版本管理 |
| W15 | 运维监控 | 监控看板、日志搜索 |
| W16 | 维护管理 & 收尾 | Bug 追踪、知识库、全面测试 |

---

## 6. 文档索引

| 文档 | 路径 | 描述 |
|------|------|------|
| 项目总体规划 | [project-plan.md](./project-plan.md) | 本文档 |
| 产品需求文档 | [prd.md](./prd.md) | 详细功能需求、用户故事、验收标准 |
| 系统架构设计 | [system-architecture.md](./system-architecture.md) | 分层架构、技术方案、模块设计 |
| 数据库设计 | [database-design.md](./database-design.md) | 表结构、ER 图、索引策略 |
| API 接口设计 | [api-design.md](./api-design.md) | RESTful API 规范、接口清单 |
| 部署与运维 | [deployment-plan.md](./deployment-plan.md) | Docker/K8s 部署、CI/CD、监控 |
| 开发规范 | [development-conventions.md](./development-conventions.md) | 编码规范、Git 规范、审查规范 |

---

## 7. 风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| AI 模型 API 不稳定或调用成本高 | 核心 AI 功能不可用 | 多模型适配 + 本地模型降级 + 调用限流 |
| 前后端开发人力不足 | 进度延迟 | 优先 P0 模块，其余分阶段交付 |
| 模块间耦合度高 | 维护困难 | 严格分层架构，模块间通过接口通信 |
| 数据量增长导致性能瓶颈 | 系统变慢 | 读写分离、缓存策略、分表方案预留 |
