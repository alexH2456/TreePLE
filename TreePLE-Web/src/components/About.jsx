import React, {PureComponent} from 'react';
import {Button, Input, Image, Modal, Label, Form} from 'semantic-ui-react';
import SignInModal from './SignInModal';
import {getUser} from './Requests.jsx'

const options = [
  {key: 'R', text: 'Resident', value: 'resident'},
  {key: '', text: 'Scientist', value: 'scientist'},
]
class About extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password: "",
    };
  }


  handleChangeU = (event) => {
    this.setState({username: event.target.value});
  }

  handleChangeP = (event) => {
    this.setState({password: event.target.value});
  }

  handleSignIn = () => {
    getUser(this.state.username)
      .then(({data, status}) => {
        if (status == 200) {
          localStorage.setItem("username", JSON.stringify(data.username));
          localStorage.setItem("role", JSON.stringify(data.role));
          localStorage.setItem("adresses", JSON.stringify(data.myAddresses[0]));
        }
      })
      .catch(error => {
        console.log(error);
      })
  }



  render () {
    return (
      <div>
        <div>
          <Modal basic trigger={<Button>Login</Button>} size='small'>
            <Modal.Content image>

            <div>
              <Image src='images/favicon.ico' size='medium' spaced='right'/>
              </div>
              <Modal.Description>
                <Form>
                  <Form.Input fluid label='Username' placeholder='Username' onChange={this.handleChangeU}/>
                  <Form.Input fluid type='password' label='Password' placeholder='Password' onChange={this.handleChangeP}/>
                  <Form.Button color='olive' size='massive' onClick={this.handleSignIn}>Sign In</Form.Button>
                </Form>
              </Modal.Description>
            </Modal.Content>
          </Modal>
        </div>

        <div>
          <Modal trigger={<Button>Register</Button>} basic size="small">
            <Modal.Content image>
              <div>
              <Image src='images/favicon.ico' size='medium' spaced='right'/>
              </div>
              <Modal.Description>
                <Form>
                  <Form.Input fluid label='Username' placeholder='Username'/>
                  <Form.Input fluid type='password' label='Password' placeholder='Password'/>
                  <Form.Input fluid type='password' label='Password2' placeholder='Confirm Password'/>
                  <Form.Select fluid label='Role' options={options} placeholder='Role'/>
                  <Form.Input fluid type='password' label='ScientistAccess' placeholder='Scientist Access'/>
                  <Form.Input fluid label='PostalCode' placeholder='PostalCode'/>
                  <Form.Button color='olive' size='massive'>Register</Form.Button>
                </Form>
              </Modal.Description>
            </Modal.Content>
          </Modal>
        </div>
        <div>
        <SignInModal/>
        </div>
      </div>
    );
  };
};

export default About;
