var webpack = require('webpack');
var path = require('path');

var parentDir = path.join(__dirname, '../');

module.exports = {
  entry: [
    path.join(parentDir, 'src/index.jsx')
  ],
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
        loaders: ["style-loader", "css-loader", "less-loader"]
      }
    ]
  },
  resolve: {
    extensions: ['.js', '.jsx'],
  },
  output: {
    path: parentDir + '/dist',
    filename: 'bundle.js'
  },
  devServer: {
    port: 8087,
    contentBase: parentDir,
    historyApiFallback: true
  }
};