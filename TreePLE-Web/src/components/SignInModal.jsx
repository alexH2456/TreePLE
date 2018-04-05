import React, {PureComponent} from 'react';
import {Button, Image, Modal, Form} from 'semantic-ui-react';
import {login} from './Requests';

class SignInModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalOpen: false,
      inputError: false,
      username: '',
      password: '',
      errorMessage: ''
    };
  }

  handleOpen = () => this.setState({modalOpen: true});

  handleClose = () => this.setState({modalOpen: false});

  handleChangeU = (event) => {
    this.setState({username: event.target.value});
  }

  handleChangeP = (event) => {
    this.setState({password: event.target.value});
  }

  handleSignIn = () => {
    const loginInfo = {
      username: this.state.username,
      password: this.state.password,
    };

    login(loginInfo)
      .then(({data, status}) => {
        if (status == 200) {
            localStorage.setItem("username", JSON.stringify(data.username));
            localStorage.setItem("role", JSON.stringify(data.role));
            localStorage.setItem("adresses", JSON.stringify(data.myAddresses[0]));
            this.setState({modalOpen: false});
        }
      })
      .catch(error => {
        console.log(error.message);
        this.setState({inputError: true,
                       errorMessage:error.message
                     });
      });
  }

  render() {
    return (
      <Modal
        basic
        size='small'
        open={this.state.modalOpen}
        onClose={this.handleClose}
        trigger={<Button onClick={this.handleOpen}>Sign In</Button>}
      >
        <Modal.Content image>
          <div>
            <Image src='images/favicon.ico' size='small' spaced='right'/>
          </div>
          <Modal.Description>
            <Form>
              <Form.Input fluid placeholder='Username' error={this.state.error} onChange={this.handleChangeU}/>
              <Form.Input fluid type='password' placeholder='Password' error={this.state.error} onChange={this.handleChangeP}/>
              <Form.Button inverted color='green' size='small' onClick={this.handleSignIn}>Sign In</Form.Button>
              <Form.Button inverted color='red' size='small' onClick={this.handleClose}>Close</Form.Button>
            </Form>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

export default SignInModal;
