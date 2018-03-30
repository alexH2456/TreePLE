import React, {PureComponent} from 'react';
import {connect} from 'react-redux';
import {Link} from 'react-router-dom';
import {setMessage} from '../actions/message';
import InputPreview from './InputPreview';
import {getAllUsers} from './Requests';
import {Button, Input, Image, Modal, Label} from 'semantic-ui-react'

class App extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      users: ''
    };
  }

  _onChange = (value) => {
    this.props.dispatch(setMessage(value));
  };

  request = () => {
    getAllUsers()
      .then(response => {
        console.log(response.data);
        this.setState({users: response.data});
      })
      .catch(error => {
        console.log(error);
      });
  }

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
        <Link to="/map">
          <button>Go to maps</button>
        </Link>
        <button onClick={this.request}>Send request</button>
        {this.state.users ? (
          <div>
          {this.state.users.map(user => <p key={user.username}>{user.username}</p>)}
          </div>
        ) : (null)}

        
      </div>
    );
  };
};

export default connect(state => state)(App);