import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import sha512 from 'sha512';
import {Button, Divider, Grid, Header, Icon, Modal, Form} from 'semantic-ui-react';
import {createUser} from './Requests';
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
      error: false,
      errorMsg: ''
    };
  }

  onUsernameChange = (e, {value}) => this.setState({username: value});
  onPasswordChange1 = (e, {value}) => this.setState({password1: value});
  onPasswordChange2 = (e, {value}) => this.setState({password2: value});
  onRoleChange = (e, {value}) => this.setState({role: value});
  onKeyChange = (e, {value}) => this.setState({accessKey: value});
  onPostalChange = (e, {value}) => this.setState({postalCode: value.toUpperCase()});

  onSignUp = () => {
    if (this.state.password1 == this.state.password2) {
      const signupInfo = {
        username: this.state.username,
        password: sha512(this.state.password1).toString('hex'),
        role: this.state.role,
        scientistKey: this.state.accessKey,
        myAddresses: this.state.postalCode
      };

      createUser(signupInfo)
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
    } else {
      this.setState({
        error: true,
        errorMsg: "Passwords do not match!"
      });
    }
  }

  render() {
    console.log(sha512('guccigang').toString('hex'));

    return (
      <Modal open size="mini" dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='users' circular/>
              <Header.Content>Sign Up</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Form>
              <Form.Input fluid placeholder='Username' onChange={this.onUsernameChange}/>
              <Form.Input fluid type='password' placeholder='Password' onChange={this.onPasswordChange1}/>
              <Form.Input fluid type='password' placeholder='Confirm Password' onChange={this.onPasswordChange2}/>
              <Form.Select fluid options={roleSelectable} placeholder='Role' onChange={this.onRoleChange}/>
              {this.state.role == 'Scientist' ? (
                <Form.Input fluid type='password' placeholder='Scientist Access Key' onChange={this.onKeyChange}/>
              ) : null}
              <Form.Input fluid placeholder='Postal Code' onChange={this.onPostalChange}/>
            </Form>
            <Divider hidden/>
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
  onClose: PropTypes.func.isRequired
}

export default SignUpModal;
