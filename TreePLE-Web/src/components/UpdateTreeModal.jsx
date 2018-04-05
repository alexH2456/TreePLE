import React, {PureComponent} from 'react';
import {Button, Image, Modal, Form} from 'semantic-ui-react';


class UpdateTreeModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalOpen: false,
    }
  }

  handleOpen = () => this.setState({modalOpen: true});

  handleClose = () => this.setState({modalOpen: false});

  render() {
    const optionsLand = [
      {key: 'P', text: 'Park', value: 'park'},
      {key: 'R', text: 'Residential', value: 'residential'},
      {key: 'I', text: 'Institutional', value: 'institutional'},
      {key: 'M', text: 'Municipal', value: 'municipal'}
    ];

    const optionsStatus = [
      {key: 'P', text: 'Planted', value: 'planted'},
      {key: 'D', text: 'Diseased', value: 'diseased'},
      {key: 'M', text: 'Marked For Cutdown', value: 'markedForCutdown'},
      {key: 'C', text: 'Cutdown', value: 'cutdown'}
    ];

    const optionsOwnership = [
      {key: 'Pu', text: 'Public', value: 'public'},
      {key: 'Pr', text: 'Private', value: 'private'}
    ];

    return(
      <Modal basic size="small" open={this.state.modalOpen} onClose={this.handleClose} trigger={<Button onClick={this.handleOpen}>Update</Button>}
      >
        <Modal.Content image>
          <div>
            <Image src='images/favicon.ico' size='small' spaced='right'/>
          </div>
          <Modal.Description>
            <Form>
              <Form.Input fluid placeholder='Height'/>
              <Form.Input fluid placeholder='Diameter'/>
              <Form.Select fluid options={optionsLand} placeholder='Land'/>
              <Form.Select fluid options={optionsStatus} placeholder='Status'/>
              <Form.Select fluid options={optionsOwnership} placeholder='Ownership'/>
              <Form.Input fluid placeholder='Species'/>
              <Form.Button inverted color='green' size='small' onClick={this.handleSignUp}>Update</Form.Button>
              <Form.Button inverted color='red' size='small' onClick={this.handleClose}>Close</Form.Button>
            </Form>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

export default UpdateTreeModal;