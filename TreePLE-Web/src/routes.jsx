import React from 'react';
import {BrowserRouter, Route, Switch, Link} from 'react-router-dom';
import App from './components/App';
import About from './components/About';
import TreeMapContainer from './components/TreeMapContainer';
import NavigationBar from './components/NavigationBar';

export default () => {
  return (
    <BrowserRouter>
      <Switch>
        <Route exact path='/' component={NavigationBar}/>
        <Route path='/about' component={About}/>
        <Route path='/map' component={TreeMapContainer}/>
      </Switch>
    </BrowserRouter>
  );
};