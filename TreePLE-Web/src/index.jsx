import React from 'react';
import ReactDOM from 'react-dom';
import NavigationBar from './components/NavigationBar';

// CSS Sheets
import 'react-day-picker/lib/style.css';

const toRender = (
  <div>
    <NavigationBar/>
  </div>
);

ReactDOM.render(toRender, document.getElementById('app'));
