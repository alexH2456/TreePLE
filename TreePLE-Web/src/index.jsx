import React from 'react';
import ReactDOM from 'react-dom';
import IconMenu from './components/IconMenu';
import NavigationBar from './components/NavigationBar';
import TreeMap from './components/TreeMap';

// CSS Sheets

// Render Page

const base_url = window.location.origin;
const url = window.location.href;

// Main Routes

let component;

if (url.includes('/about')) {
  component = <About/>
} else if (url.includes('/menu')) {
  component = <IconMenu/>;
} else if (url.includes('/map')) {
    component = <TreeMap/>;
} else {
  component = <NavigationBar/>;
}

const toRender = (
  <div>
    {component}
  </div>
);

ReactDOM.render(toRender, document.getElementById('app'));