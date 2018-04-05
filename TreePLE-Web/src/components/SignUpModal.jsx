import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
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

  componentWillReceiveProps(nextProps) {
    if (this.props.show !== nextProps.show) {
      this.setState({modalOpen: nextProps.show});
    }
  }

  handleOpen = () => this.setState({modalOpen: true});
  handleClose = () => this.setState({modalOpen: false});

  handleChangeU = (e, data) => this.setState({username: data.value});
  handleChangeP1 = (e, data) => this.setState({password1: data.value});
  handleChangeP2 = (e, data) => this.setState({password2: data.value});
  handleRoleChange = (e, data) => this.setState({role: data.value});
  handleKeyChange = (e, data) => this.setState({accessKey: data.value});
  handlePostalChange = (e, data) => this.setState({postalCode: data.value.toUpperCase()});

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
        errorMessage: "Passwords do not match!"
      });
    }
  }

  render() {
    return (
      <Modal
        basic
        closeIcon
        size="small"
        open={this.state.modalOpen}
        // open={this.props.show}
        onClose={this.handleClose}
        // trigger={<Button onClick={this.handleOpen}>Sign Up</Button>}
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
              <Form.Input fluid placeholder='Postal Code' onChange={this.handlePostalChange}/>
              <Form.Group inline>
                <Form.Button inverted color='green' size='small' onClick={this.handleSignUp}>Sign Up</Form.Button>
                <Form.Button inverted color='red' size='small' onClick={this.handleClose}>Close</Form.Button>
              </Form.Group>
            </Form>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

SignUpModal.propTypes = {
  show: PropTypes.bool.isRequired
}

export default SignUpModal;
