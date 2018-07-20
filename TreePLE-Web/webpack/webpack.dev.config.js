const _ = require('lodash');
const fs = require('fs');
const path = require('path');
const webpack = require('webpack');

const parentDir = path.join(__dirname, '../');

module.exports = {
  entry: [
    'babel-polyfill',
    path.join(parentDir, 'src/index.jsx')
  ],
  output: {
    path: path.join(parentDir + 'dist'),
    filename: 'bundle.js'
  },
  module: {
    loaders: [
      {
        test: /\.(js|jsx)?$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
        query: {
          presets: ['env', 'react', 'stage-0']
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
        test: /\.(html)$/,
        loader: 'html-loader'
      }, {
        test: /\.(png|jpg|gif|svg|ico|eot|ttf|woff|woff2)$/,
        include: /images/,
        use: {
          loader: 'url-loader',
          options: {
            limit: 100000,
          },
        }
      }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      serverHost: JSON.stringify('localhost'),
      serverPort: JSON.stringify('8088')
    }),
    new webpack.ProvidePlugin({
      _: 'lodash',
      C: 'constants',
      U: 'Utils'
    }),
    new webpack.optimize.UglifyJsPlugin({
      compress: false,
      mangle: false
    })
  ],
  resolve: {
    extensions: ['.js', '.jsx', '.css']
  },
  devtool: 'eval-source-map',
  devServer: {
    https: {
      key: fs.readFileSync(path.join(parentDir + 'ssl/treeple.key')),
      cert: fs.readFileSync(path.join(parentDir + 'ssl/treeple.crt'))
    },
    port: 8087,
    host: 'localhost',
    contentBase: parentDir,
    historyApiFallback: true,
    disableHostCheck: true
  }
};
