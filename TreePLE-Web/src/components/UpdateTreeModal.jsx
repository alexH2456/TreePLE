import React, {PureComponent} from 'react';
import {Button, Image, Modal, Form} from 'semantic-ui-react';
import {landSelectable, statusSelectable, ownershipSelectable} from '../constants';


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
    return(
      <Modal
        basic
        size="small"
        open={this.state.modalOpen}
        onClose={this.handleClose}
        trigger={<Button onClick={this.handleOpen}>Update</Button>}
      >
        <Modal.Content image>
          <div>
            <Image src='../images/favicon.ico' size='small' spaced='right'/>
          </div>
          <Modal.Description>
            <Form>
              <Form.Input fluid placeholder='Height'/>
              <Form.Input fluid placeholder='Diameter'/>
              <Form.Select fluid options={landSelectable} placeholder='Land'/>
              <Form.Select fluid options={statusSelectable} placeholder='Status'/>
              <Form.Select fluid options={ownershipSelectable} placeholder='Ownership'/>
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