import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';

// Main Routes
import AppRoutes from './routes';
import store from './store';

// CSS Sheets

// Render Page

ReactDOM.render(
  <Provider store={store}>
    <AppRoutes/>
  </Provider>,
  document.getElementById('app')
);