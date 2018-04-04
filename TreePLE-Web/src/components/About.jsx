import React, {PureComponent} from 'react';
import {Button, Input, Image, Modal, Label} from 'semantic-ui-react';
import SignInModal from './SignInModal';

class About extends PureComponent {
  render () {
    return (
      <div>
        <div>
          About
        </div>

        <div>
          <br/>
        <Modal trigger={<Button>Login</Button>} basic size='small'>

            <Modal.Content image>
              <Image src='images/favicon.ico' size='medium' spaced = 'right' />
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

        <div>
          <SignInModal/>
        </div>
      </div>
    );
  };
};

export default About;