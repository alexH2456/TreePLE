import React, {PureComponent} from 'react';
import {Button, Image, Modal, Form} from 'semantic-ui-react';
import {createUser} from './Requests';

class SignUpModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalOpen: false,
      inputError: false,
      username: '',
      password1: '',
      password2: '',
      role:'',
      accessKey:'',
      postalCode:''
    };

    this.handlePostalChange = this.handlePostalChange.bind(this);
    this.handleKeyChange = this.handleKeyChange.bind(this);
    this.handleRoleChange =  this.handleRoleChange.bind(this);
    this.handleChangeU =  this.handleChangeU.bind(this);
    this.handleChangeP1 = this.handleChangeP1.bind(this);
    this.handleChangeP2 = this.handleChangeP2.bind(this);



  }

  handleOpen = () => this.setState({modalOpen: true});

  handleClose = () => this.setState({modalOpen: false});



  handleSignUp = () => {
    console.log(this.state);
    if(this.state.password1==this.state.password2){

      const signupInfo = {
        username: this.state.username,
        password: this.state.password2,
        role: this.state.role,
        scientistKey: this.state.accessKey,
        myAddresses: this.state.postalCode
      };
      console.log(signupInfo);
      createUser(signupInfo)
        .then(response => {
          console.log(response);
          this.setState({modalOpen: false});
        })
        .catch(error => {
          console.log(error);
          this.setState({inputError: true});
        })

    }
  }



  handlePostalChange(event, data) {
    this.setState({postalCode: data.value.toUpperCase()})
  }
  handleKeyChange(event, data) {
    this.setState({accessKey: data.value})
  }
  handleRoleChange(event, data){
    console.log(event.target);
    console.log(data);
    this.setState({role: data.value})
  }
  handleChangeU(event, data){
    this.setState({username: data.value});
  }
  handleChangeP1(event, data){
    this.setState({password1: data.value});
  }
  handleChangeP2(event, data){
    this.setState({password2: data.value});
  }


  render() {
    console.log(this.state);
    const options = [
      {key: 'R', text: 'Resident', value: 'Resident'},
      {key: 'S', text: 'Scientist', value: 'Scientist'},
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
              <Form.Input fluid placeholder='Username' onChange={this.handleChangeU}/>
              <Form.Input fluid type='password' placeholder='Password' onChange={this.handleChangeP1}/>
              <Form.Input fluid type='password' placeholder='Confirm Password' onChange={this.handleChangeP2}/>
              <Form.Select fluid options={options} placeholder='Role' onChange={this.handleRoleChange}/>
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
