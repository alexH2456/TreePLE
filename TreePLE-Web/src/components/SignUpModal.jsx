import React, {PureComponent} from 'react';
import {Button, Image, Modal, Form} from 'semantic-ui-react';
import {login} from './Requests';

class SignUpModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalOpen: false,
      inputError: false,
      username: '',
      password: '',
    };
  }

  handleOpen = () => this.setState({modalOpen: true});

  handleClose = () => this.setState({modalOpen: false});

  handleSignUp = () => {
    const signupInfo = {
      username: this.state.username,
      password: this.state.password,
    };

    login(loginInfo)
      .then(response => {
        console.log(response);
        this.setState({modalOpen: false});
      })
      .catch(error => {
        console.log(error);
        this.state({inputError: true});
      })
  }

  render() {
    const options = [
      {key: 'R', text: 'Resident', value: 'resident'},
      {key: '', text: 'Scientist', value: 'scientist'},
    ];

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
            <Image src='images/favicon.ico' size='small' spaced='right'/>
          </div>
          <Modal.Description>
            <Form>
              <Form.Input fluid placeholder='Username'/>
              <Form.Input fluid type='password' placeholder='Password'/>
              <Form.Input fluid type='password' placeholder='Confirm Password'/>
              <Form.Select fluid options={options} placeholder='Role'/>
              <Form.Input fluid type='password' placeholder='Scientist Access Key'/>
              <Form.Input fluid placeholder='PostalCode'/>
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