import React, {PureComponent} from 'react';
import {Button, Input, Image, Modal, Label} from 'semantic-ui-react';
import SignInModal from './SignInModal';
import {getUser} from './Requests.jsx'

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
          About
        </div>

        <div>
          <br/>
          <Modal basic trigger={<Button>Login</Button>} size='small'>
            <Modal.Content image>
              <Image src='images/favicon.ico' size='medium' spaced='right' />
              <Modal.Description>
                <div style={{display: "inline-block"}}>
                <Input
                  placeholder='Enter Username'
                  onChange={this.handleChangeU}
                />

                <Label pointing='left'size='large'>Please enter your username</Label>
                </div>
                <div style={{display: "inline-block"}}>
                  <Input placeholder='Enter Password' onChange={this.handleChangeP} />
                  <Label pointing='left' size='large'>Please enter your password</Label>
                </div>
                <Button color='olive' size='massive' onClick={this.handleSignIn}>Login</Button>
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
