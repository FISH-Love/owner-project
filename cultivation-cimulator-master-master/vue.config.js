// vue.config.js
module.exports = {
  devServer: {
    port: 8081, // 前端端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端地址（Spring Boot端口）
        changeOrigin: true
      }
    }
  }
}
