const path = require('path')

function resolve(dir) {
  return path.join(__dirname, dir)
}

const name = '作业互评系统'

module.exports = {
  productionSourceMap: false,
  // options...
  devServer: {
    port: 8002,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },
  configureWebpack: {
    // provide the app's title in webpack's name field, so that
    // it can be accessed in index.html to inject the correct title.
    name: name,
    resolve: {
      alias: {
        '@': resolve('src')
      }
    }
  }
}
