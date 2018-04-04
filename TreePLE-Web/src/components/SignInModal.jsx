import React, {PureComponent} from 'react';
import {Button, Input, Image, Modal, Label, Header} from 'semantic-ui-react';
import {getUser} from './Requests';

class SignInModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalOpen: false,
      inputError: false,
      username: '',
      password: '',
    };
  }

  handleOpen = () => this.setState({ modalOpen: true });

  handleClose = () => this.setState({ modalOpen: false });

  handleSignIn = () => {
    console.log(this.state);
    const loginInfo = {
      username: this.state.username,
      password: this.state.password
    };

    getUser(loginInfo)
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
    return (
      <Modal
        trigger={<Button onClick={this.handleOpen}>Sign In</Button>}
        open={this.state.modalOpen}
        onClose={this.handleClose}
        basic
        size='small'
      >
        <Header icon='sign in' content='Sign In to TreePLE'/>
        <Modal.Description>
          <div>
            <Input placeholder='Username' error={this.state.inputError} onChange={(e, data) => this.setState({username: data.value})}/>
          </div>
          <div>
            <Input placeholder='Password' error={this.state.inputError} onChange={(e, data) => this.setState({password: data.value})}/>
          </div>
        </Modal.Description>
        <Modal.Actions>
          <Button color='green' onClick={this.handleSignIn} inverted>
            Sign In
          </Button>
          <Button color='red' onClick={this.handleClose} inverted>
            Close
          </Button>
        </Modal.Actions>
      </Modal>
    );
  }
}

export default SignInModal;