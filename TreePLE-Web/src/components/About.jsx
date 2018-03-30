import React, {PureComponent} from 'react';
import {Link} from 'react-router-dom';
import {Button, Input, Image, Modal, Label} from 'semantic-ui-react'

class About extends PureComponent {
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
                <Input placeholder='Enter Username' />
                
                <Label pointing = 'left'size = 'large' >Please enter your username</Label>
                </div>
                <div style={{display: "inline-block"}}>
                <Input placeholder='Enter Password' />
                
                <Label pointing = 'left' size = 'large' >Please enter your password</Label>
                </div>
                <Button color = 'olive' size = 'massive' > Login</Button>
              </Modal.Description>
            </Modal.Content>
          </Modal>
        </div>
      </div>
    );
  };
};

export default About;