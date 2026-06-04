"use client";

import {
  Activity,
  Bot,
  Check,
  ChevronRight,
  CircleUserRound,
  ClipboardList,
  Command,
  DatabaseZap,
  FileKey2,
  Gauge,
  Layers3,
  LockKeyhole,
  Menu,
  Plus,
  Search,
  ShieldCheck,
  SlidersHorizontal,
  UsersRound,
} from "lucide-react";
import { useMemo, useState } from "react";

type SectionKey = "overview" | "providers" | "users" | "roles" | "menus";

const navItems: Array<{
  key: SectionKey;
  label: string;
  description: string;
  icon: React.ComponentType<{ className?: string }>;
}> = [
  { key: "overview", label: "管理概览", description: "系统配置总览", icon: Gauge },
  { key: "providers", label: "模型服务商", description: "密钥、端点、模型", icon: Bot },
  { key: "users", label: "用户注册", description: "账号与组织", icon: UsersRound },
  { key: "roles", label: "角色权限", description: "权限策略", icon: ShieldCheck },
  { key: "menus", label: "菜单分配", description: "导航与入口", icon: Layers3 },
];

const providers = [
  {
    name: "OpenAI",
    type: "商业 API",
    endpoint: "https://api.openai.com/v1",
    status: "已启用",
    models: ["gpt-4.1", "o4-mini", "text-embedding-3-large"],
    owner: "平台管理员",
    quota: "78%",
  },
  {
    name: "DashScope",
    type: "国内模型平台",
    endpoint: "https://dashscope.aliyuncs.com",
    status: "待验证",
    models: ["qwen-max", "qwen-plus", "qwen-embedding"],
    owner: "AI 中台",
    quota: "42%",
  },
  {
    name: "Local vLLM",
    type: "私有化部署",
    endpoint: "http://10.12.8.21:8000/v1",
    status: "维护中",
    models: ["Qwen3-32B", "DeepSeek-R1-Distill"],
    owner: "基础设施",
    quota: "15%",
  },
];

const users = [
  { name: "陈景行", account: "chen.jx", role: "系统管理员", org: "平台部", status: "已激活" },
  { name: "林文澈", account: "lin.wc", role: "模型运营", org: "AI 中台", status: "待审核" },
  { name: "周若安", account: "zhou.ra", role: "普通用户", org: "研发效能", status: "已激活" },
  { name: "赵亦宁", account: "zhao.yn", role: "审计员", org: "安全合规", status: "冻结" },
];

const permissions = [
  { module: "模型服务商", admin: true, operator: true, auditor: true, user: false },
  { module: "用户注册", admin: true, operator: false, auditor: true, user: false },
  { module: "角色权限", admin: true, operator: false, auditor: true, user: false },
  { module: "菜单分配", admin: true, operator: true, auditor: true, user: false },
  { module: "调用审计", admin: true, operator: true, auditor: true, user: false },
];

const menuTree = [
  {
    title: "系统管理",
    children: ["模型服务商", "用户注册", "角色权限", "菜单分配"],
  },
  {
    title: "AI 工作台",
    children: ["对话工作区", "提示词模板", "知识库", "调用审计"],
  },
  {
    title: "运维监控",
    children: ["模型健康检查", "配额看板", "异常告警"],
  },
];

const statusTone: Record<string, string> = {
  已启用: "bg-emerald-50 text-emerald-700 ring-emerald-200",
  待验证: "bg-amber-50 text-amber-700 ring-amber-200",
  维护中: "bg-rose-50 text-rose-700 ring-rose-200",
  已激活: "bg-emerald-50 text-emerald-700 ring-emerald-200",
  待审核: "bg-amber-50 text-amber-700 ring-amber-200",
  冻结: "bg-zinc-100 text-zinc-600 ring-zinc-200",
};

function classNames(...classes: Array<string | false | undefined>) {
  return classes.filter(Boolean).join(" ");
}

function StatusPill({ status }: { status: string }) {
  return (
    <span
      className={classNames(
        "inline-flex h-7 items-center rounded-full px-2.5 text-xs font-medium ring-1",
        statusTone[status] ?? "bg-zinc-100 text-zinc-700 ring-zinc-200",
      )}
    >
      {status}
    </span>
  );
}

function Panel({
  title,
  eyebrow,
  action,
  children,
}: {
  title: string;
  eyebrow?: string;
  action?: React.ReactNode;
  children: React.ReactNode;
}) {
  return (
    <section className="rounded-lg border border-zinc-200 bg-white shadow-sm">
      <div className="flex flex-col gap-3 border-b border-zinc-200 px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          {eyebrow ? <p className="text-xs font-semibold uppercase tracking-[0.16em] text-teal-700">{eyebrow}</p> : null}
          <h2 className="mt-1 text-lg font-semibold text-zinc-950">{title}</h2>
        </div>
        {action}
      </div>
      <div className="p-5">{children}</div>
    </section>
  );
}

function IconButton({
  label,
  children,
  active = false,
}: {
  label: string;
  children: React.ReactNode;
  active?: boolean;
}) {
  return (
    <button
      type="button"
      aria-label={label}
      title={label}
      className={classNames(
        "grid size-10 place-items-center rounded-md border transition",
        active
          ? "border-zinc-950 bg-zinc-950 text-white"
          : "border-zinc-200 bg-white text-zinc-700 hover:border-zinc-400 hover:text-zinc-950",
      )}
    >
      {children}
    </button>
  );
}

export default function Home() {
  const [active, setActive] = useState<SectionKey>("overview");
  const activeItem = useMemo(() => navItems.find((item) => item.key === active) ?? navItems[0], [active]);

  return (
    <main className="min-h-screen bg-[#f6f5f1] text-zinc-950">
      <div className="flex min-h-screen">
        <aside className="hidden w-72 shrink-0 border-r border-zinc-200 bg-[#101312] text-white lg:block">
          <div className="flex h-full flex-col">
            <div className="border-b border-white/10 px-6 py-6">
              <div className="flex items-center gap-3">
                <div className="grid size-11 place-items-center rounded-lg bg-teal-400 text-zinc-950">
                  <Command className="size-5" />
                </div>
                <div>
                  <p className="text-sm font-semibold text-white">AI Workbench</p>
                  <p className="text-xs text-zinc-400">Admin Console</p>
                </div>
              </div>
            </div>

            <nav className="flex-1 space-y-1 px-3 py-5">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive = active === item.key;
                return (
                  <button
                    key={item.key}
                    type="button"
                    onClick={() => setActive(item.key)}
                    className={classNames(
                      "flex w-full items-center gap-3 rounded-md px-3 py-3 text-left transition",
                      isActive ? "bg-white text-zinc-950" : "text-zinc-300 hover:bg-white/10 hover:text-white",
                    )}
                  >
                    <Icon className="size-5 shrink-0" />
                    <span className="min-w-0 flex-1">
                      <span className="block text-sm font-semibold">{item.label}</span>
                      <span className={classNames("block truncate text-xs", isActive ? "text-zinc-500" : "text-zinc-500")}>
                        {item.description}
                      </span>
                    </span>
                    {isActive ? <ChevronRight className="size-4" /> : null}
                  </button>
                );
              })}
            </nav>

            <div className="border-t border-white/10 p-4">
              <div className="rounded-lg bg-white/7 p-4">
                <div className="flex items-center gap-3">
                  <CircleUserRound className="size-8 text-teal-300" />
                  <div>
                    <p className="text-sm font-medium">平台管理员</p>
                    <p className="text-xs text-zinc-400">admin@ai-workbench</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </aside>

        <section className="min-w-0 flex-1">
          <header className="sticky top-0 z-10 border-b border-zinc-200 bg-[#f6f5f1]/90 px-4 py-3 backdrop-blur md:px-8">
            <div className="flex flex-col gap-3 xl:flex-row xl:items-center xl:justify-between">
              <div className="flex items-center gap-3">
                <IconButton label="打开菜单">
                  <Menu className="size-4" />
                </IconButton>
                <div>
                  <p className="text-xs font-semibold uppercase tracking-[0.18em] text-zinc-500">System Admin</p>
                  <h1 className="text-2xl font-semibold tracking-tight text-zinc-950">{activeItem.label}</h1>
                </div>
              </div>
              <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
                <label className="relative block min-w-0 sm:w-80">
                  <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-zinc-400" />
                  <input
                    className="h-10 w-full rounded-md border border-zinc-200 bg-white pl-9 pr-3 text-sm text-zinc-900 outline-none transition placeholder:text-zinc-400 focus:border-teal-500 focus:ring-2 focus:ring-teal-100"
                    placeholder="搜索用户、模型、菜单"
                  />
                </label>
                <div className="flex gap-2">
                  <IconButton label="筛选">
                    <SlidersHorizontal className="size-4" />
                  </IconButton>
                  <button
                    type="button"
                    className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-teal-600 px-4 text-sm font-semibold text-white transition hover:bg-teal-700"
                  >
                    <Plus className="size-4" />
                    新增配置
                  </button>
                </div>
              </div>
            </div>

            <div className="mt-3 flex gap-2 overflow-x-auto pb-1 lg:hidden">
              {navItems.map((item) => (
                <button
                  key={item.key}
                  type="button"
                  onClick={() => setActive(item.key)}
                  className={classNames(
                    "h-9 shrink-0 rounded-md px-3 text-sm font-medium",
                    active === item.key ? "bg-zinc-950 text-white" : "bg-white text-zinc-700 ring-1 ring-zinc-200",
                  )}
                >
                  {item.label}
                </button>
              ))}
            </div>
          </header>

          <div className="mx-auto grid max-w-7xl gap-6 px-4 py-6 md:px-8">
            <Overview active={active} setActive={setActive} />
            {active === "providers" ? <Providers /> : null}
            {active === "users" ? <Users /> : null}
            {active === "roles" ? <Roles /> : null}
            {active === "menus" ? <Menus /> : null}
          </div>
        </section>
      </div>
    </main>
  );
}

function Overview({
  active,
  setActive,
}: {
  active: SectionKey;
  setActive: (section: SectionKey) => void;
}) {
  const metrics = [
    { label: "服务商", value: "3", note: "1 个待验证", icon: DatabaseZap },
    { label: "注册用户", value: "128", note: "12 个待审核", icon: UsersRound },
    { label: "角色策略", value: "4", note: "覆盖 5 个模块", icon: FileKey2 },
    { label: "菜单入口", value: "11", note: "3 个一级目录", icon: Menu },
  ];

  return (
    <>
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {metrics.map((metric) => {
          const Icon = metric.icon;
          return (
            <button
              type="button"
              key={metric.label}
              onClick={() =>
                setActive(
                  metric.label === "服务商"
                    ? "providers"
                    : metric.label === "注册用户"
                      ? "users"
                      : metric.label === "角色策略"
                        ? "roles"
                        : "menus",
                )
              }
              className="rounded-lg border border-zinc-200 bg-white p-4 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-teal-300 hover:shadow-md"
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm text-zinc-500">{metric.label}</p>
                  <p className="mt-2 text-3xl font-semibold tracking-tight text-zinc-950">{metric.value}</p>
                </div>
                <span className="grid size-10 place-items-center rounded-md bg-[#f0ead8] text-zinc-900">
                  <Icon className="size-5" />
                </span>
              </div>
              <p className="mt-3 text-sm text-zinc-600">{metric.note}</p>
            </button>
          );
        })}
      </div>

      {active === "overview" ? (
        <div className="grid gap-6 xl:grid-cols-[1.45fr_0.9fr]">
          <Panel
            eyebrow="Foundation"
            title="管理端基础能力"
            action={
              <button className="inline-flex h-9 items-center gap-2 rounded-md border border-zinc-200 bg-white px-3 text-sm font-medium text-zinc-700 hover:border-zinc-400">
                <ClipboardList className="size-4" />
                配置清单
              </button>
            }
          >
            <div className="grid gap-3 md:grid-cols-2">
              {navItems.slice(1).map((item) => {
                const Icon = item.icon;
                return (
                  <button
                    key={item.key}
                    type="button"
                    onClick={() => setActive(item.key)}
                    className="flex min-h-28 items-start gap-4 rounded-lg border border-zinc-200 p-4 text-left transition hover:border-teal-300 hover:bg-teal-50/50"
                  >
                    <span className="grid size-11 shrink-0 place-items-center rounded-md bg-zinc-950 text-white">
                      <Icon className="size-5" />
                    </span>
                    <span>
                      <span className="block font-semibold text-zinc-950">{item.label}</span>
                      <span className="mt-1 block text-sm leading-6 text-zinc-600">{item.description}</span>
                      <span className="mt-3 inline-flex items-center gap-1 text-sm font-medium text-teal-700">
                        进入配置 <ChevronRight className="size-4" />
                      </span>
                    </span>
                  </button>
                );
              })}
            </div>
          </Panel>

          <Panel eyebrow="Runtime" title="系统健康">
            <div className="space-y-4">
              {["认证服务", "权限缓存", "模型网关", "审计日志"].map((item, index) => (
                <div key={item} className="flex items-center justify-between gap-3">
                  <div className="flex items-center gap-3">
                    <span className="grid size-9 place-items-center rounded-md bg-emerald-50 text-emerald-700">
                      <Activity className="size-4" />
                    </span>
                    <div>
                      <p className="text-sm font-medium text-zinc-900">{item}</p>
                      <p className="text-xs text-zinc-500">{index === 2 ? "P95 320ms" : "运行正常"}</p>
                    </div>
                  </div>
                  <StatusPill status="已启用" />
                </div>
              ))}
            </div>
          </Panel>
        </div>
      ) : null}
    </>
  );
}

function Providers() {
  return (
    <Panel
      eyebrow="Model Gateway"
      title="模型服务商配置"
      action={
        <button className="inline-flex h-9 items-center gap-2 rounded-md bg-zinc-950 px-3 text-sm font-semibold text-white">
          <Plus className="size-4" />
          新增服务商
        </button>
      }
    >
      <div className="overflow-x-auto">
        <table className="w-full min-w-[920px] border-separate border-spacing-0 text-left text-sm">
          <thead>
            <tr className="text-xs uppercase tracking-[0.12em] text-zinc-500">
              <th className="border-b border-zinc-200 pb-3 font-semibold">服务商</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">接入类型</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">端点</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">模型</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">配额</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">状态</th>
            </tr>
          </thead>
          <tbody>
            {providers.map((provider) => (
              <tr key={provider.name} className="align-top">
                <td className="border-b border-zinc-100 py-4 pr-4">
                  <p className="font-semibold text-zinc-950">{provider.name}</p>
                  <p className="mt-1 text-xs text-zinc-500">负责人：{provider.owner}</p>
                </td>
                <td className="border-b border-zinc-100 py-4 pr-4 text-zinc-700">{provider.type}</td>
                <td className="border-b border-zinc-100 py-4 pr-4 font-mono text-xs text-zinc-600">{provider.endpoint}</td>
                <td className="border-b border-zinc-100 py-4 pr-4">
                  <div className="flex flex-wrap gap-1.5">
                    {provider.models.map((model) => (
                      <span key={model} className="rounded-md bg-zinc-100 px-2 py-1 text-xs text-zinc-700">
                        {model}
                      </span>
                    ))}
                  </div>
                </td>
                <td className="border-b border-zinc-100 py-4 pr-4">
                  <div className="h-2 w-24 rounded-full bg-zinc-100">
                    <div className="h-2 rounded-full bg-teal-600" style={{ width: provider.quota }} />
                  </div>
                  <p className="mt-1 text-xs text-zinc-500">{provider.quota}</p>
                </td>
                <td className="border-b border-zinc-100 py-4">
                  <StatusPill status={provider.status} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Panel>
  );
}

function Users() {
  return (
    <div className="grid gap-6 xl:grid-cols-[0.82fr_1.18fr]">
      <Panel eyebrow="Registration" title="注册入口配置">
        <form className="grid gap-4">
          {["默认组织", "默认角色", "审核方式", "邮箱域名白名单"].map((label) => (
            <label key={label} className="grid gap-2 text-sm font-medium text-zinc-700">
              {label}
              <input
                className="h-10 rounded-md border border-zinc-200 bg-white px-3 text-sm outline-none focus:border-teal-500 focus:ring-2 focus:ring-teal-100"
                defaultValue={
                  label === "默认组织"
                    ? "AI Workbench"
                    : label === "默认角色"
                      ? "普通用户"
                      : label === "审核方式"
                        ? "管理员审核"
                        : "@company.com"
                }
              />
            </label>
          ))}
          <button className="mt-2 inline-flex h-10 items-center justify-center gap-2 rounded-md bg-teal-600 px-4 text-sm font-semibold text-white hover:bg-teal-700">
            <Check className="size-4" />
            保存注册策略
          </button>
        </form>
      </Panel>

      <Panel eyebrow="Accounts" title="用户列表">
        <div className="space-y-3">
          {users.map((user) => (
            <div key={user.account} className="grid gap-3 rounded-lg border border-zinc-200 p-4 sm:grid-cols-[1fr_auto] sm:items-center">
              <div className="flex items-center gap-3">
                <span className="grid size-10 shrink-0 place-items-center rounded-md bg-[#f0ead8] font-semibold text-zinc-900">
                  {user.name.slice(0, 1)}
                </span>
                <div className="min-w-0">
                  <p className="font-semibold text-zinc-950">{user.name}</p>
                  <p className="truncate text-sm text-zinc-500">
                    {user.account} · {user.org} · {user.role}
                  </p>
                </div>
              </div>
              <StatusPill status={user.status} />
            </div>
          ))}
        </div>
      </Panel>
    </div>
  );
}

function Roles() {
  return (
    <Panel eyebrow="RBAC" title="角色权限矩阵">
      <div className="overflow-x-auto">
        <table className="w-full min-w-[760px] border-separate border-spacing-0 text-sm">
          <thead>
            <tr className="text-left text-xs uppercase tracking-[0.12em] text-zinc-500">
              <th className="border-b border-zinc-200 pb-3 font-semibold">模块</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">系统管理员</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">模型运营</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">审计员</th>
              <th className="border-b border-zinc-200 pb-3 font-semibold">普通用户</th>
            </tr>
          </thead>
          <tbody>
            {permissions.map((row) => (
              <tr key={row.module}>
                <td className="border-b border-zinc-100 py-4 font-medium text-zinc-950">{row.module}</td>
                {(["admin", "operator", "auditor", "user"] as const).map((role) => (
                  <td key={role} className="border-b border-zinc-100 py-4">
                    <span
                      className={classNames(
                        "inline-grid size-7 place-items-center rounded-md",
                        row[role] ? "bg-emerald-50 text-emerald-700" : "bg-zinc-100 text-zinc-300",
                      )}
                    >
                      <Check className="size-4" />
                    </span>
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Panel>
  );
}

function Menus() {
  return (
    <div className="grid gap-6 xl:grid-cols-[1fr_1fr]">
      <Panel eyebrow="Navigation" title="菜单树">
        <div className="space-y-3">
          {menuTree.map((group) => (
            <div key={group.title} className="rounded-lg border border-zinc-200 p-4">
              <div className="flex items-center gap-3">
                <span className="grid size-9 place-items-center rounded-md bg-zinc-950 text-white">
                  <Menu className="size-4" />
                </span>
                <p className="font-semibold text-zinc-950">{group.title}</p>
              </div>
              <div className="mt-3 grid gap-2 pl-12">
                {group.children.map((child) => (
                  <label key={child} className="flex items-center justify-between gap-3 rounded-md bg-zinc-50 px-3 py-2 text-sm text-zinc-700">
                    {child}
                    <input type="checkbox" defaultChecked className="size-4 accent-teal-600" />
                  </label>
                ))}
              </div>
            </div>
          ))}
        </div>
      </Panel>

      <Panel eyebrow="Assignment" title="按角色分配菜单">
        <div className="grid gap-4">
          {["系统管理员", "模型运营", "审计员", "普通用户"].map((role, index) => (
            <div key={role} className="rounded-lg border border-zinc-200 p-4">
              <div className="flex items-center justify-between gap-3">
                <div className="flex items-center gap-3">
                  <LockKeyhole className="size-5 text-teal-700" />
                  <p className="font-semibold text-zinc-950">{role}</p>
                </div>
                <span className="text-sm text-zinc-500">{index === 3 ? "3" : "11"} 个入口</span>
              </div>
              <div className="mt-3 flex flex-wrap gap-2">
                {(index === 3 ? ["对话工作区", "提示词模板", "知识库"] : ["系统管理", "AI 工作台", "运维监控"]).map((item) => (
                  <span key={item} className="rounded-md bg-[#f0ead8] px-2.5 py-1 text-xs font-medium text-zinc-800">
                    {item}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      </Panel>
    </div>
  );
}
