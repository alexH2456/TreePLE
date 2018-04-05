import React from 'react';
import ReactDOM from 'react-dom';
import About from './components/About';
import IconMenu from './components/IconMenu';
import NavigationBar from './components/NavigationBar';
import {TreeMapContainer} from './components/TreeMapContainer';

// Main Routes

// CSS Sheets

// Render Page

const base_url = window.location.origin;
const url = window.location.href;

console.log(url);

let component;

if (url.includes('/about')) {
  component = <About/>
} else if (url.includes('/menu')) {
  component = <IconMenu/>;
} else if (url.includes('/bar')) {
  component = <NavigationBar/>;
} else if (url.includes('/map')) {
    component = <TreeMapContainer/>;
} else {
  component = (
    <div>
      <NavigationBar icon={<IconMenu/>}/>
      <TreeMapContainer/>;
    </div>
  );
}

const toRender = (
  <div>
    {component}
  </div>
);

ReactDOM.render(toRender, document.getElementById('app'));

// ReactDOM.render(
//   <Provider store={store}>
//     <AppRoutes/>
//   </Provider>,
//   document.getElementById('app')
// );