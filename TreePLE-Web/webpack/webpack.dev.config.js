const path = require('path');
const fs = require('fs');
const webpack = require('webpack');

var parentDir = path.join(__dirname, '../');

module.exports = {
  entry: [
    path.join(parentDir, 'src/index.jsx'),
    'webpack-dev-server/client?http://127.0.0.1:8087/'
  ],
  output: {
    path: parentDir + '/dist',
    filename: 'bundle.js'
  },
  module: {
    loaders: [
      {
        test: /\.(js|jsx)?$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
        query: {
          presets: ['es2015', 'react', 'stage-0']
        }
      }, {
        test: /\.less$/,
        loaders: ['style-loader', 'css-loader', 'less-loader']
      }, {
        test: /\.css$/,
        include: /node_modules/,
        loaders: ['style-loader', 'css-loader']
      }, {
        test: /\.s[a|c]ss$/,
        loaders: ['sass-loader', 'style-loader', 'css-loader']
      }, {
        test: /\.(png|jpg|gif|svg|eot|ttf|woff|woff2)$/,
        use: {
          loader: 'url-loader',
          options: {
            limit: 100000,
          },
        }
      }
    ]
  },
  resolve: {
    extensions: ['.js', '.jsx']
  },
  devtool: 'eval-source-map',
  devServer: {
    port: 8087,
    host: '127.0.0.1',
    contentBase: parentDir,
    historyApiFallback: true,
    disableHostCheck: true,
    proxy: {
      '/api/*': {
        target: 'http://localhost:8088/',
        pathRewrite: {
          '/api': ''
        }
      }
    }
  }
};