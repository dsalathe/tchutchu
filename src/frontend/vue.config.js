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
  pwa: {
    name: 'TchuTchu',
    short_name: 'TchuTchu',
    themeColor: '#DA291C',
    msTileColor: '#DA291C',
    appleMobileWebAppCapable: 'yes',
    appleMobileWebAppStatusBarStyle: 'black-translucent',
    manifestOptions: {
      name: 'TchuTchu',
      short_name: 'TchuTchu',
      description: 'A multiplayer train board game inspired by Ticket to Ride. Build routes across Switzerland and compete with friends!',
      id: '/',
      start_url: '.',
      scope: '/',
      display: 'standalone',
      orientation: 'landscape',
      theme_color: '#DA291C',
      background_color: '#000000',
      categories: ['games', 'entertainment'],
      lang: 'en',
      dir: 'ltr',
      prefer_related_applications: false,
      icons: [
        {
          src: './img/icons/android-chrome-192x192.png',
          sizes: '192x192',
          type: 'image/png'
        },
        {
          src: './img/icons/android-chrome-512x512.png',
          sizes: '512x512',
          type: 'image/png'
        },
        {
          src: './img/icons/android-chrome-maskable-192x192.png',
          sizes: '192x192',
          type: 'image/png',
          purpose: 'maskable'
        },
        {
          src: './img/icons/android-chrome-maskable-512x512.png',
          sizes: '512x512',
          type: 'image/png',
          purpose: 'maskable'
        }
      ],
      screenshots: [
        {
          src: './img/screenshots/screenshot-wide.png',
          sizes: '1920x1080',
          type: 'image/png',
          form_factor: 'wide',
          label: 'TchuTchu game board on desktop'
        },
        {
          src: './img/screenshots/screenshot-mobile.png',
          sizes: '1080x1920',
          type: 'image/png',
          form_factor: 'narrow',
          label: 'TchuTchu game board on mobile (landscape recommended)'
        }
      ]
    },
    workboxPluginMode: 'GenerateSW',
    workboxOptions: {
      skipWaiting: true,
      clientsClaim: true,
      runtimeCaching: [
        {
          urlPattern: /^https:\/\/tchutchu\.dsalathe\.dev\/.*/,
          handler: 'NetworkFirst',
          options: {
            cacheName: 'api-cache',
            networkTimeoutSeconds: 10,
            expiration: {
              maxEntries: 50,
              maxAgeSeconds: 300
            },
            cacheableResponse: {
              statuses: [0, 200]
            }
          }
        },
        {
          urlPattern: /\.(?:png|jpg|jpeg|svg|gif|webp)$/,
          handler: 'CacheFirst',
          options: {
            cacheName: 'image-cache',
            expiration: {
              maxEntries: 100,
              maxAgeSeconds: 30 * 24 * 60 * 60 // 30 days
            }
          }
        },
        {
          urlPattern: /\.(?:js|css)$/,
          handler: 'StaleWhileRevalidate',
          options: {
            cacheName: 'static-resources'
          }
        }
      ]
    }
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
