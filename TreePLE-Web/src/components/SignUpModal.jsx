import React, {PureComponent} from 'react';
import {Button, Image, Modal, Form} from 'semantic-ui-react';
import {createUser} from './Requests';
import {roleSelectable} from '../constants';

class SignUpModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalOpen: false,
      inputError: false,
      username: '',
      password1: '',
      password2: '',
      role: '',
      accessKey: '',
      postalCode: '',
      errorMessage: ''
    };
  }

  handleOpen = () => this.setState({modalOpen: true});

  handleClose = () => this.setState({modalOpen: false});

  handleChangeU = (event, data) => this.setState({username: data.value});

  handleChangeP1 = (event, data) => this.setState({password1: data.value});

  handleChangeP2 = (event, data) => this.setState({password2: data.value});

  handleRoleChange = (event, data) => this.setState({role: data.value});

  handleKeyChange = (event, data) => this.setState({accessKey: data.value});

  handlePostalChange = (event, data) => this.setState({postalCode: data.value.toUpperCase()});

  handleSignUp = () => {
    if (this.state.password1 == this.state.password2) {
      const signupInfo = {
        username: this.state.username,
        password: this.state.password2,
        role: this.state.role,
        scientistKey: this.state.accessKey,
        myAddresses: this.state.postalCode
      };

      createUser(signupInfo)
        .then(response => {
          console.log(response);
          this.setState({modalOpen: false});
        })
        .catch(error => {
          console.log(error.message);
          this.setState({
            inputError: true,
            errorMessage: error.message
          });
        });
    } else {
      this.setState({
        inputError: true,
        errorMessage:"Passwords are different"
      });
    }
  }

  render() {
    return (
      <Modal
        basic
        size="small"
        open={this.state.modalOpen}
        onClose={this.handleClose}
        trigger={<Button onClick={this.handleOpen}>Sign Up</Button>}
      >
        <Modal.Content image>
          <div>
            <Image src='../images/favicon.ico' size='small' spaced='right'/>
          </div>
          <Modal.Description>
            <Form>
              <Form.Input fluid placeholder='Username' onChange={this.handleChangeU}/>
              <Form.Input fluid type='password' placeholder='Password' onChange={this.handleChangeP1}/>
              <Form.Input fluid type='password' placeholder='Confirm Password' onChange={this.handleChangeP2}/>
              <Form.Select fluid options={roleSelectable} placeholder='Role' onChange={this.handleRoleChange}/>
              <Form.Input fluid type='password' placeholder='Scientist Access Key' onChange={this.handleKeyChange}/>
              <Form.Input fluid placeholder='PostalCode' onChange={this.handlePostalChange}/>
              <Form.Button inverted color='green' size='small' onClick={this.handleSignUp}>Sign Up</Form.Button>
              <Form.Button inverted color='red' size='small' onClick={this.handleClose}>Close</Form.Button>
            </Form>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

export default SignUpModal;
