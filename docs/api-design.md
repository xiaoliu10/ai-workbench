# AI Workbench — API 接口设计

> **版本**: v0.1
> **日期**: 2026-06-04
> **负责人**: Jason
> **状态**: 草稿（首版聚焦"对话式编排层"的 Chat SSE 与动作确认接口）

---

## 1. 通用约定

- **Base URL**：`/api/v1`
- **认证**：除登录外所有接口需 `Authorization: Bearer <JWT>`（对应 M2，Spring Security + JWT）。
- **普通响应**统一信封（非流式）：

  ```json
  { "code": 0, "message": "ok", "data": { } }
  ```
  `code=0` 成功；非 0 为业务错误码。

- **错误响应**：

  ```json
  { "code": 40001, "message": "conversation not found", "data": null }
  ```

- **时间**：ISO-8601 带时区（如 `2026-06-04T10:00:00+08:00`）。
- **权限**：动作能否执行由当前用户 RBAC 决定（架构 7.3），越权返回 `403`。

---

## 2. 会话管理

### 2.1 创建会话

```
POST /api/v1/conversations
```

请求：
```json
{ "project_id": 1001, "title": null }
```
`project_id` 可空（未聚焦项目时先开聊）。`title` 可空，由首条消息自动生成。

响应：
```json
{ "code": 0, "message": "ok",
  "data": { "id": 5001, "project_id": 1001, "status": "active", "created_at": "2026-06-04T10:00:00+08:00" } }
```

### 2.2 会话列表

```
GET /api/v1/conversations?page=1&size=20
```
按 `last_message_at DESC` 返回当前用户的会话分页。

### 2.3 会话详情（含历史消息）

```
GET /api/v1/conversations/{id}?with_messages=true
```

---

## 3. Chat SSE 接口（核心）

### 3.1 发送消息并接收流式响应

```
POST /api/v1/conversations/{id}/messages
Accept: text/event-stream
```

请求体：
```json
{ "content": "给项目X加一个登录页", "context": { "repo_id": 7001, "branch": "main" } }
```

响应：`Content-Type: text/event-stream`，服务端持续推送编排 / 专家 Agent 的进度事件。

> **客户端实现提示**：因为本接口为 `POST` 且带请求体，原生 `EventSource` 不适用（其仅支持 GET 无体）。前端用 `fetch` + `ReadableStream` 解析 SSE。断线后可用 §3.4 重连。

### 3.2 SSE 事件协议

每个事件采用标准 SSE 帧：

```
event: <type>
data: <JSON>

```

事件类型目录：

| event | data 关键字段 | 含义 / 前端处理 |
|-------|--------------|----------------|
| `conversation.context` | `{ focused_requirement_id, repo_id, branch }` | 聚焦上下文更新 |
| `agent.dispatch` | `{ agent_type }` | 编排 Agent 路由到某专家（左侧显示"代码 Agent 正在处理…"） |
| `message.delta` | `{ message_id, role, agent_type, delta }` | 流式文本增量，左侧逐字渲染 |
| `message.completed` | `{ message_id, content }` | 该条消息结束 |
| `action.proposed` | `{ action_id, agent_type, action_type, risk_level, params, preview }` | Agent 提出一个动作；`high_write` 时**流在此暂停等待确认**，右侧渲染 preview/diff |
| `action.status` | `{ action_id, status, result?, error? }` | 动作状态变更（executing / succeeded / failed …） |
| `artifact.updated` | `{ artifact_id, type, effect }` | 提示右侧工作台刷新对应产物视图（effect: created/modified） |
| `error` | `{ code, message }` | 处理出错 |
| `done` | `{ reason }` | 本轮流结束（`reason`: completed / awaiting_confirm） |

### 3.3 风险分级对流的影响（呼应架构 7.5 / DB 状态机）

- **read / low_write**：Agent 直接执行，流中依次出现 `action.status: executing → succeeded`，随后 `artifact.updated`，最后 `done(completed)`。
- **high_write**（改代码并提交、触发生产发布）：流推送 `action.proposed` 后，以 `done(awaiting_confirm)` 结束本轮；动作落库为 `pending`，**等待用户通过 §4 确认接口**继续。

### 3.4 流式响应示例（high_write：修改代码）

```
event: agent.dispatch
data: {"agent_type":"requirement"}

event: message.delta
data: {"message_id":6001,"role":"agent","agent_type":"requirement","delta":"已为登录页起草需求…"}

event: action.status
data: {"action_id":8801,"status":"succeeded"}

event: artifact.updated
data: {"artifact_id":9001,"type":"需求","effect":"created"}

event: agent.dispatch
data: {"agent_type":"code"}

event: action.proposed
data: {"action_id":8802,"agent_type":"code","action_type":"proposeCodeChange",
       "risk_level":"high_write","params":{"repo_id":7001,"branch":"feature/login"},
       "preview":{"diff":"+ src/app/login/page.tsx\n+ ...","files_changed":2}}

event: done
data: {"reason":"awaiting_confirm"}
```

前端收到 `action.proposed` 后，在右侧工作台渲染 diff 与 `[确认] / [拒绝]` 按钮。

### 3.5 重连 / 续流

```
GET /api/v1/conversations/{id}/stream?after_message_id=6001
Accept: text/event-stream
```
断线后重连，补推 `after_message_id` 之后的事件（GET 无体，可用原生 `EventSource`）。

---

## 4. 动作确认接口（Human-in-the-loop）

针对 `high_write` 处于 `pending` 的动作，由有权限的用户确认或拒绝。

### 4.1 查询待确认动作

```
GET /api/v1/conversations/{id}/actions?status=pending
```
响应：
```json
{ "code": 0, "message": "ok", "data": [
  { "action_id": 8802, "agent_type": "code", "action_type": "proposeCodeChange",
    "risk_level": "high_write", "status": "pending",
    "preview": { "diff": "+ src/app/login/page.tsx\n+ ...", "files_changed": 2 } }
]}
```

### 4.2 动作详情

```
GET /api/v1/actions/{action_id}
```
返回完整 `params` / `preview`，供右侧渲染。

### 4.3 确认动作（执行，流式返回结果）

```
POST /api/v1/actions/{action_id}/confirm
Accept: text/event-stream
```
请求体（可选，允许确认时微调）：
```json
{ "params_override": null, "comment": "确认提交" }
```

行为：
1. 校验动作处于 `pending` 且当前用户有权限，否则返回 `409 / 403`（非流式 JSON）。
2. 写入 `confirmed_by` / `confirmed_at`，状态 `pending → confirmed → executing`。
3. 调用对应专家 Agent 的工具（平台模块 API）执行，**以 SSE 推送执行过程与结果**，并将影响写入 `agent_action_artifact` 与 `relation`（追溯）。

流式响应示例：
```
event: action.status
data: {"action_id":8802,"status":"executing"}

event: action.status
data: {"action_id":8802,"status":"succeeded","result":{"pr_url":"https://github.com/x/y/pull/45","pr_number":45}}

event: artifact.updated
data: {"artifact_id":9002,"type":"代码","effect":"modified"}

event: message.completed
data: {"message_id":6002,"content":"已提交 PR #45，包含 2 个文件变更。"}

event: done
data: {"reason":"completed"}
```

执行失败：
```
event: action.status
data: {"action_id":8802,"status":"failed","error":"branch protected: 需至少 1 个审批"}

event: done
data: {"reason":"completed"}
```

### 4.4 拒绝动作

```
POST /api/v1/actions/{action_id}/reject
```
请求体：`{ "comment": "登录页先不做" }`
状态 `pending → rejected`（非流式，返回标准信封）。

### 4.5 取消动作

```
POST /api/v1/actions/{action_id}/cancel
```
用于 `low_write` 的撤销或 `pending/executing` 的中止；状态 → `cancelled`。可撤销与否取决于 `action_type` 是否支持补偿操作。

---

## 5. 状态码约定（动作相关）

| HTTP | 业务码 | 场景 |
|------|--------|------|
| 200 | 0 | 成功 |
| 403 | 40301 | 无权限执行该动作 |
| 404 | 40401 | 会话 / 动作不存在 |
| 409 | 40901 | 动作状态不允许该操作（如已执行的动作再次 confirm） |
| 422 | 42201 | `params` 校验失败 |

---

## 6. 关联文档

| 文档 | 路径 |
|------|------|
| 系统架构设计 | [system-architecture.md](./system-architecture.md) |
| 数据库设计 | [database-design.md](./database-design.md) |
| 项目总体规划 | [project-plan.md](./project-plan.md) |
