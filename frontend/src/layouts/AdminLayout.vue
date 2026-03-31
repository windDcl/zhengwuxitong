<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="admin-sidebar-card">
        <p class="admin-kicker">运营中心</p>
        <h3>知识库管理</h3>
        <span>查看趋势、补录未命中问题、维护问答资产</span>
      </div>

      <nav class="admin-nav">
        <router-link to="/admin/dashboard">运营总览</router-link>
        <router-link to="/admin/categories">分类管理</router-link>
        <router-link to="/admin/faq">FAQ管理</router-link>
        <router-link to="/admin/announcements">公告管理</router-link>
        <router-link to="/admin/logs">问答日志</router-link>
        <router-link to="/admin/unmatched">未命中问题</router-link>
        <router-link to="/admin/settings">系统设置</router-link>
        <router-link to="/admin/import">Excel导入</router-link>
      </nav>
    </aside>

    <main class="admin-content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.admin-layout {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 20px;
  min-height: calc(100vh - 140px);
}

.admin-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.admin-sidebar-card {
  padding: 22px;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.96), rgba(16, 94, 84, 0.92));
  color: #f8fafc;
  box-shadow: 0 26px 50px rgba(15, 23, 42, 0.18);
}

.admin-sidebar-card h3 {
  margin: 10px 0 8px;
  font-size: 24px;
}

.admin-sidebar-card span {
  color: rgba(255, 255, 255, 0.78);
  font-size: 13px;
  line-height: 1.6;
}

.admin-kicker {
  margin: 0;
  color: #99f6e4;
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.admin-nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.76);
  border: 1px solid rgba(148, 163, 184, 0.2);
  backdrop-filter: blur(16px);
}

.admin-nav a {
  padding: 12px 14px;
  border-radius: 14px;
  color: #1e293b;
  text-decoration: none;
  font-weight: 600;
  transition: 0.2s ease;
}

.admin-nav a:hover,
.admin-nav a.router-link-active {
  background: linear-gradient(135deg, rgba(15, 118, 110, 0.14), rgba(14, 165, 233, 0.14));
  color: #0f766e;
}

.admin-content {
  min-width: 0;
}

@media (max-width: 980px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }
}
</style>
