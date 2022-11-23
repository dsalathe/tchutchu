const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '^/': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      },
      '^ws': {
        target: 'localhost:8080',
        ws: true,
        changeOrigin: true
      }
    }
  }
})
