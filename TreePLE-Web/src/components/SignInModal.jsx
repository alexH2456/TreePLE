import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import sha512 from 'sha512';
import {Button, Divider, Grid, Header, Icon, Modal, Form} from 'semantic-ui-react';
import {login} from './Requests';

class SignInModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      username: '',
      password: '',
      error: false,
      errorMsg: ''
    };
  }

  onUsernameChange = (e, {value}) => this.setState({username: value});
  onPasswordChange = (e, {value}) => this.setState({password: value});

  onSignIn = () => {
    const loginInfo = {
      username: this.state.username,
      password: sha512(this.state.password).toString('hex')
    };

    login(loginInfo)
      .then(({data, status}) => {
        if (status == 200) {
          localStorage.setItem('username', data.username);
          this.props.onClose();
        }
      })
      .catch(error => {
        this.setState({
          error: true,
          errorMsg: error.message
        });
      });
  }

  render() {
    return (
      <Modal open size='mini' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='user' circular/>
              <Header.Content>Sign In</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Form>
              <Form.Input fluid placeholder='Username' error={this.state.error} onChange={this.onUsernameChange}/>
              <Form.Input fluid type='password' placeholder='Password' error={this.state.error} onChange={this.onPasswordChange}/>
            </Form>
            <Divider hidden/>
            <Grid centered>
              <Grid.Row>
                <Form.Button inverted color='green' size='small' onClick={this.onSignIn}>Sign In</Form.Button>
                <Form.Button inverted color='blue' size='small' onClick={this.props.onRegister}>Sign Up</Form.Button>
                <Form.Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Form.Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

SignInModal.propTypes = {
  onClose: PropTypes.func.isRequired
}

export default SignInModal;
