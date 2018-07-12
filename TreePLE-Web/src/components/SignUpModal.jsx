import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import sha512 from 'sha512';
import {Divider, Form, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import {createUser} from './Requests';
import {getError} from './Utils';
import {roleSelectable} from '../constants';

class SignUpModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      username: '',
      password1: '',
      password2: '',
      role: '',
      accessKey: '',
      postalCode: '',
      error: ''
    };
  }

  onUsernameChange = (e, {value}) => this.setState({username: value});
  onPasswordChange1 = (e, {value}) => this.setState({password1: value});
  onPasswordChange2 = (e, {value}) => this.setState({password2: value});
  onRoleChange = (e, {value}) => this.setState({role: value});
  onKeyChange = (e, {value}) => this.setState({accessKey: value});
  onPostalChange = (e, {value}) => this.setState({postalCode: value});

  onSignUp = () => {
    if (this.state.password1 === this.state.password2) {
      const signupParams = {
        username: this.state.username,
        password: sha512(this.state.password1).toString('hex'),
        role: this.state.role,
        scientistKey: this.state.accessKey,
        myAddresses: this.state.postalCode.length !== 0 ? [this.state.postalCode.toUpperCase()] : []
      };

      createUser(signupParams)
        .then(({data}) => {
          localStorage.setItem('username', data.username);
          this.props.onClose();
        })
        .catch(({response: {data}}) => {
          this.setState({error: data.message});
        });
    } else {
      this.setState({error: 'Passwords do not match!'});
    }
  }

  render() {
    const errors = getError(this.state.error);

    return (
      <Modal open size='mini' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='users' circular/>
              <Header.Content>Sign Up</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Form>
              <Form.Input fluid placeholder='Username' error={errors.username} onChange={this.onUsernameChange}/>
              <Form.Input fluid type='password' placeholder='Password' error={errors.password} onChange={this.onPasswordChange1}/>
              <Form.Input fluid type='password' placeholder='Confirm Password' error={errors.password} onChange={this.onPasswordChange2}/>
              <Form.Select fluid options={roleSelectable} placeholder='Role' error={errors.role} onChange={this.onRoleChange}/>
              {this.state.role === 'Scientist' ? (
                <Form.Input fluid type='password' placeholder='Scientist Access Key' error={errors.key} onChange={this.onKeyChange}/>
              ) : null}
              <Form.Input fluid placeholder='Postal Code' error={errors.address} onChange={this.onPostalChange}/>
            </Form>

            <Divider hidden/>

            {this.state.error ? (
              <Message error size='tiny'>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

            <Grid centered>
              <Grid.Row>
                <Form.Button inverted color='green' size='small' onClick={this.onSignUp}>Sign Up</Form.Button>
                <Form.Button inverted color='blue' size='small' onClick={this.props.onRegister}>Sign In</Form.Button>
                <Form.Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Form.Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

SignUpModal.propTypes = {
  onRegister: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired
};

export default SignUpModal;
