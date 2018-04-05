import React, {PureComponent} from 'react';
import {Button, Input, Image, Modal, Label, Form} from 'semantic-ui-react';
import SignInModal from './SignInModal';
import SignUpModal from './SignUpModal';
import UpdateTreeModal from './UpdateTreeModal';

class About extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password: "",
      modalOpen: false,
      inputError: false
    };
  }

  render () {
    

    return (
      <div>
        <div>
          <SignInModal/>
        </div>
        <div>
          <SignUpModal/>
        </div>
        <div>
          <UpdateTreeModal/>
        </div>
      </div>
    );
  };
};

export default About;
