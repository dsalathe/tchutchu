const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: ['keycloak-js'],
  chainWebpack: config => {
    config.module
      .rule('vue')
      .use('vue-loader')
      .tap(options => ({
        ...options,
        compilerOptions: {
          isCustomElement: tag => tag.startsWith('dsalathe-')
        }
      }))
  },
  devServer: {
    port: 8082,
    proxy: {
      '^/game-ws': {
        target: 'http://localhost:8081',
        ws: true,
        changeOrigin: true
      },
      '^/app': {
        target: 'http://localhost:8081',
        ws: true,
        changeOrigin: true
      }
    }
  }
})
