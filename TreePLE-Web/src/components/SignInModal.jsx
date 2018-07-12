import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import sha512 from 'sha512';
import {Divider, Form, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import {login} from './Requests';
import {getError} from './Utils';

class SignInModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      username: '',
      password: '',
      error: ''
    };
  }

  onUsernameChange = (e, {value}) => this.setState({username: value});
  onPasswordChange = (e, {value}) => this.setState({password: value});

  onSignIn = () => {
    const loginParams = {
      username: this.state.username,
      password: sha512(this.state.password).toString('hex')
    };

    login(loginParams)
      .then(({data}) => {
        localStorage.setItem('username', data.username);
        this.props.onClose();
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  render() {
    const errors = getError(this.state.error);

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
              <Form.Input fluid placeholder='Username' error={errors.username} onChange={this.onUsernameChange}/>
              <Form.Input fluid type='password' placeholder='Password' error={errors.password} onChange={this.onPasswordChange}/>
            </Form>

            <Divider hidden/>

            {this.state.error ? (
              <Message error size='tiny'>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

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
  onRegister: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired
};

export default SignInModal;
