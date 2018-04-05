import React, {PureComponent} from 'react';
import {Button, Input, Image, Modal, Label, Form} from 'semantic-ui-react';
import SignInModal from './SignInModal';
import SignUpModal from './SignUpModal';

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
    const options = [
      {key: 'R', text: 'Resident', value: 'resident'},
      {key: '', text: 'Scientist', value: 'scientist'},
    ];

    return (
      <div>
        <div>
          <SignInModal/>
        </div>
        <div>
          <SignUpModal/>
        </div>
      </div>
    );
  };
};

export default About;
