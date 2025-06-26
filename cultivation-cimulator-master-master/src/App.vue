<template>
  <div id="app">
    <!-- 移除导航栏代码 -->
    
    <!-- 保留路由出口 -->
    <router-view />
  </div>
</template>

<script>
export default {
  data() {
    return {
      // 移除userInfo数据
    };
  },
  computed: {
    // 移除isLoggedIn计算属性
  },
  mounted() {
    // 移除获取用户信息的逻辑
  },
  methods: {
    // 移除logout方法
  }
};
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

#app {
  min-height: 100vh;
}
</style>
/* 移除导航栏相关的CSS样式 */