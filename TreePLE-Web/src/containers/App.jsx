import React, {PureComponent} from 'react';
import {connect} from 'react-redux';
import {Link} from 'react-router-dom';
import {setMessage} from '../actions/message';
import InputPreview from '../components/InputPreview';

class App extends PureComponent {
  _onChange = (value) => {
    this.props.dispatch(setMessage(value));
  };

  render () {
    const {message} = this.props.messageReducer;

    return (
      <div>
        <InputPreview
          value={message}
          onChange={this._onChange}/>
        <Link to="/about">
          <button>Go to About</button>
        </Link>
      </div>
    );
  };
};

export default connect(state => state)(App);