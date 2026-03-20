<template>
  <div style="display:flex;min-height:100vh;">
    <aside style="width:220px;background:#0f172a;color:#fff;padding:16px;">
      <h3>管理后台</h3>
      <nav style="display:flex;flex-direction:column;gap:8px;">
        <router-link to="/admin/dashboard">Dashboard</router-link>
        <router-link to="/admin/categories">分类管理</router-link>
        <router-link to="/admin/faq">FAQ管理</router-link>
        <router-link to="/admin/announcements">公告管理</router-link>
        <router-link to="/admin/logs">问答日志</router-link>
        <router-link to="/admin/unmatched">未命中问题</router-link>
        <router-link to="/admin/settings">系统设置</router-link>
        <router-link to="/admin/import">Excel导入</router-link>
      </nav>
    </aside>
    <main style="flex:1;padding:16px;background:#f8fafc;">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
a { color: #cbd5e1; text-decoration: none; }
a.router-link-active { color: #5eead4; }
</style>
