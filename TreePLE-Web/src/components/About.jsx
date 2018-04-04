import React, {PureComponent} from 'react';
import {Link} from 'react-router-dom';
import {Button, Input, Image, Modal, Label} from 'semantic-ui-react'
var service = require('./Requests.jsx')

class About extends PureComponent {

  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password: "",
    };


  this.login =service.loginUser.bind(this);
  }


  handleChangeU(event) {
      this.setState({username: event.target.value});
      console.log("hey");
    }

  handleChangeP(event) {
      this.setState({password: event.target.value});
      console.log("heyP");
   }


  render () {
    return (
      <div>
        <div>
          About
          <Link to="/">
            <button>Go Home</button>
          </Link>
        </div>
        <div>
          <br/>
        <Modal trigger={<Button>Login</Button>} basic size='small'>

            <Modal.Content image>
              <Image src='images/favicon.ico' size='medium' spaced = 'true|right' />
              <Modal.Description>
                <div style={{display: "inline-block"}}>
                <Input placeholder='Enter Username'
                        onChange={this.handleChangeU.bind(this)}
                         />

                <Label pointing = 'left'size = 'large' >Please enter your username</Label>
                </div>
                <div style={{display: "inline-block"}}>
                <Input placeholder='Enter Password' onChange={this.handleChangeP.bind(this)} />

                <Label pointing = 'left' size = 'large' >Please enter your password</Label>
                </div>
                <Button color = 'olive' size = 'massive'
                        onClick={this.login}> Login</Button>
              </Modal.Description>
            </Modal.Content>
          </Modal>
        </div>
      </div>
    );
  };
};

export default About;
