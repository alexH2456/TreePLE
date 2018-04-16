import React from 'react';
import ReactDOM from 'react-dom';
import NavigationBar from './components/NavigationBar';

// CSS Sheets
import 'react-day-picker/lib/style.css';

// Render Page
const base_url = window.location.origin;
const url = window.location.href;

// Main Routes
const toRender = (
  <div>
    <NavigationBar/>
  </div>
);

ReactDOM.render(toRender, document.getElementById('app'));