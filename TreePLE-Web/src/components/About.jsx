import React, {PureComponent} from 'react';
import {Button, Input, Image, Modal, Label, Form} from 'semantic-ui-react';
import DayPickerInput from 'react-day-picker/DayPickerInput';

class About extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      date: new Date()
    }
  }

  onChange =(e, d) => {
  console.log(e,d);
  }

  render () {
    return (
      <div>
        <div>
          <DayPickerInput/>
        </div>
      </div>
    );
  };
};

export default About;
