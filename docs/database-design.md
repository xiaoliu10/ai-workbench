# AI Workbench — 数据库设计

> **版本**: v0.1
> **日期**: 2026-06-04
> **负责人**: Jason
> **数据库**: PostgreSQL 16.x ＋ MyBatis-Plus 3.5.x
> **状态**: 草稿（首版聚焦"基础表 + 对话式编排层"，其余业务模块表逐步补充）

---

## 1. 通用约定

- 主键统一为 `id BIGINT`（雪花/自增），命名 `snake_case`，表名单数。
- 公共字段（除关系/日志类表外，所有业务表均含）：

  | 字段 | 类型 | 说明 |
  |------|------|------|
  | `id` | `BIGINT` | 主键 |
  | `created_by` | `BIGINT` | 创建人（关联 `sys_user.id`） |
  | `created_at` | `TIMESTAMPTZ` | 创建时间，默认 `now()` |
  | `updated_at` | `TIMESTAMPTZ` | 更新时间 |
  | `is_deleted` | `SMALLINT` | 逻辑删除（MyBatis-Plus `@TableLogic`），0=正常 1=删除 |

- 结构化但弹性的内容统一用 `JSONB`，便于演进且可建 GIN 索引。
- 时间一律 `TIMESTAMPTZ`（带时区）。

---

## 2. 基础表（追溯模型，对应架构第 6 章）

对话动作要落入全链路追溯图，依赖以下基础表。此处给出最小定义，细节随各模块补充。

### 2.1 project（项目）

```sql
CREATE TABLE project (
    id          BIGINT       PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    description TEXT,
    owner_id    BIGINT       NOT NULL,
    status      VARCHAR(32)  NOT NULL DEFAULT 'active',  -- active | archived
    created_by  BIGINT       NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    is_deleted  SMALLINT     NOT NULL DEFAULT 0
);
```

### 2.2 artifact（资产 / 交付物，对应架构 6.1）

```sql
CREATE TABLE artifact (
    id              BIGINT       PRIMARY KEY,
    project_id      BIGINT       NOT NULL REFERENCES project(id),
    type            VARCHAR(32)  NOT NULL,  -- 需求 | 设计稿 | 文档 | 构建产物 | 发布脚本 | 知识条目 ...
    storage_backend VARCHAR(16)  NOT NULL,  -- DB | OSS | GIT | ES
    uri             TEXT,                   -- GIT/外链 引用
    object_key      VARCHAR(512),           -- OSS object key
    version         VARCHAR(64),
    meta            JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_by      BIGINT       NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    is_deleted      SMALLINT     NOT NULL DEFAULT 0
);
CREATE INDEX idx_artifact_project ON artifact(project_id, type);
```

### 2.3 relation（追溯关系图，对应架构 6.1）

```sql
CREATE TABLE relation (
    id            BIGINT       PRIMARY KEY,
    from_type     VARCHAR(32)  NOT NULL,  -- requirement | pr | testcase | release | agent_action ...
    from_id       BIGINT       NOT NULL,
    to_type       VARCHAR(32)  NOT NULL,
    to_id         BIGINT       NOT NULL,
    relation_kind VARCHAR(32)  NOT NULL,  -- 实现 | 验证 | 发布于 | 部署到 | 由动作产生 ...
    created_by    BIGINT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_relation_from ON relation(from_type, from_id);
CREATE INDEX idx_relation_to   ON relation(to_type, to_id);
```

> Agent 动作作为可追溯节点，`from_type/to_type` 取值新增 `agent_action`，使对话操作进入追溯图。

---

## 3. 对话式编排层（对应架构第 7 章）

记录 chat 入口的会话、消息，以及多专家 Agent 执行的动作。三张表的关系：

```
conversation 1 ──< message 1 ──< agent_action >── artifact (经 agent_action_artifact)
   (会话)          (消息)           (Agent 动作)        (产物)
```

### 3.1 conversation（会话）

一次对话会话，归属某用户，可绑定一个项目（也允许未选项目时先开聊）。

```sql
CREATE TABLE conversation (
    id              BIGINT       PRIMARY KEY,
    project_id      BIGINT       REFERENCES project(id),  -- 可空：尚未聚焦项目
    title           VARCHAR(256),                          -- 可由首条消息自动生成
    status          VARCHAR(16)  NOT NULL DEFAULT 'active', -- active | archived
    context         JSONB        NOT NULL DEFAULT '{}'::jsonb,
        -- 当前聚焦上下文：{ focused_requirement_id, repo_id, branch, ... }
    last_message_at TIMESTAMPTZ,
    created_by      BIGINT       NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    is_deleted      SMALLINT     NOT NULL DEFAULT 0
);
CREATE INDEX idx_conversation_owner ON conversation(created_by, last_message_at DESC);
CREATE INDEX idx_conversation_project ON conversation(project_id);
```

### 3.2 message（消息）

会话中的每一轮消息。`role=agent` 时由 `agent_type` 标明是哪个专家在发言。

```sql
CREATE TABLE message (
    id                BIGINT       PRIMARY KEY,
    conversation_id   BIGINT       NOT NULL REFERENCES conversation(id),
    parent_message_id BIGINT       REFERENCES message(id),  -- 线程/追问
    role              VARCHAR(16)  NOT NULL,  -- user | assistant | agent | system
    agent_type        VARCHAR(32),            -- orchestrator | project | requirement | code | test | release
    content           TEXT,
    meta              JSONB        NOT NULL DEFAULT '{}'::jsonb,
        -- { model, prompt_tokens, completion_tokens, latency_ms, ... }
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_message_conversation ON message(conversation_id, created_at);
```

> 消息内容历史不可变，仅追加；不设 `updated_at` / 逻辑删除（会话级归档即可）。

### 3.3 agent_action（Agent 动作）

专家 Agent 通过"工具（平台模块 API）"执行的每一个动作，含风险分级与人在回路状态机。这是对话驱动可审计、可追溯的核心。

```sql
CREATE TABLE agent_action (
    id              BIGINT       PRIMARY KEY,
    conversation_id BIGINT       NOT NULL REFERENCES conversation(id),
    message_id      BIGINT       REFERENCES message(id),  -- 触发/承载该动作的消息
    agent_type      VARCHAR(32)  NOT NULL,  -- 执行动作的专家：project | requirement | code | test | release
    action_type     VARCHAR(64)  NOT NULL,  -- 工具名：createProject | proposeCodeChange | triggerPipeline ...
    target_module   VARCHAR(16),            -- M3 | M4 | M7 | M8 | M9
    risk_level      VARCHAR(16)  NOT NULL,  -- read | low_write | high_write
    status          VARCHAR(16)  NOT NULL DEFAULT 'pending',
        -- pending | confirmed | executing | succeeded | failed | rejected | cancelled
    params          JSONB        NOT NULL DEFAULT '{}'::jsonb,  -- 工具调用入参
    preview         JSONB,                                       -- 高风险动作的预览/diff
    result          JSONB,                                       -- 执行结果
    error           TEXT,
    confirmed_by    BIGINT,                                      -- 人在回路：确认人
    confirmed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_action_conversation ON agent_action(conversation_id, created_at);
CREATE INDEX idx_action_status ON agent_action(status) WHERE status IN ('pending','executing');
```

**状态机**（呼应架构 7.5 风险分级）：

```
read       : pending → executing → succeeded/failed              （直接执行）
low_write  : pending → executing → succeeded/failed              （执行后告知，可撤销）
high_write : pending → confirmed → executing → succeeded/failed  （需 confirmed_by 确认）
                     ↘ rejected / cancelled
```

### 3.4 agent_action_artifact（动作 ↔ 产物，接入追溯）

动作执行后对产物的影响，使对话操作汇入追溯图。

```sql
CREATE TABLE agent_action_artifact (
    id          BIGINT      PRIMARY KEY,
    action_id   BIGINT      NOT NULL REFERENCES agent_action(id),
    artifact_id BIGINT      NOT NULL REFERENCES artifact(id),
    effect      VARCHAR(16) NOT NULL,  -- created | modified | referenced
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_action_artifact_action   ON agent_action_artifact(action_id);
CREATE INDEX idx_action_artifact_artifact ON agent_action_artifact(artifact_id);
```

> 同时，动作产生的业务级关联（如"需求#12 由 action#88 创建"）写入 `relation` 表：
> `relation(from_type='agent_action', from_id=88, to_type='requirement', to_id=12, relation_kind='由动作产生')`。

---

## 4. ER 关系（对话式编排层）

```
            sys_user
               │ creates
               ▼
project ──< conversation ──< message ──< agent_action ──< agent_action_artifact >── artifact
   │            (会话)        (消息)       (动作)                                      │
   └────────────────────────── 均归属同一 project ──────────────────────────────────┘

agent_action ──(写入)──> relation  ──> 任意可追溯节点（requirement / pr / release ...）
```

---

## 5. 索引与性能策略

- **会话列表**：`idx_conversation_owner(created_by, last_message_at DESC)` 支撑"我的最近会话"。
- **消息拉取**：`idx_message_conversation(conversation_id, created_at)` 按会话顺序分页。
- **待办动作**：`idx_action_status` 为部分索引（仅 `pending/executing`），高效查"待确认/执行中"动作。
- **追溯遍历**：`relation` 的 `from` / `to` 双向索引支撑图的正反向遍历。
- **JSONB 检索**：如需对 `params` / `context` / `meta` 内字段查询，按需补 `GIN` 索引。
- 大文本（`message.content`、`agent_action.result`）随会话归档冷热分离，热数据保留近期，历史可转存。

---

## 6. 关联文档

| 文档 | 路径 |
|------|------|
| 系统架构设计 | [system-architecture.md](./system-architecture.md) |
| 项目总体规划 | [project-plan.md](./project-plan.md) |
| API 接口设计 | [api-design.md](./api-design.md) |
