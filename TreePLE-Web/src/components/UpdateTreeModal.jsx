import React, {PureComponent} from 'react';
import {Button, Image, Modal, Form} from 'semantic-ui-react';
import {landSelectable, statusSelectable, ownershipSelectable} from '../constants';
import {getAllSpecies, getAllMunicipalities} from './Requests';

class UpdateTreeModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalOpen: false,
      species: [],
      municipalities: [],
      species: '',
      ownership: '',
      land: '',
      status: '',
      height: '',
      diameter: '',
      municipality: '',
      treeId: ''
    }
  }

  componentDidMount() {
    getAllSpecies()
      .then(response => {
        let speciesSelectable = [];

        response.data.forEach((species, idx) => {
          speciesSelectable.push({
            key: idx,
            text: species.name,
            value: species.name
          });
        });
        this.setState({species: speciesSelectable});
      });

    getAllMunicipalities()
      .then(response => {
          let municipalitySelectable = [];

          response.data.forEach((municipality, idx) => {
            municipalitySelectable.push({
              key: idx,
              text: municipality.name,
              value: municipality.name
            });
          });
        this.setState({municipalities: municipalitySelectable});
      });
  }

  handleOpen = () => this.setState({modalOpen: true});
  handleClose = () => this.setState({modalOpen: false});

  handleSpecies = (e, data) => this.setState({species: data.value});
  handleOwnership = (e, data) => this.setState({ownership: data.value});
  handleLand = (e, data) => this.setState({land: data.value});
  handleStatus = (e, data) => this.setState({status: data.value});
  handleHeight = (e, data) => this.setState({height: data.value});
  handleDiameter = (e, data) => this.setState({diameter: data.value});
  handleMunicipality = (e, data) => this.setState({municipality: data.value});

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
              <Form.Input fluid placeholder='Height' onChange={this.handleHeight}/>
              <Form.Input fluid placeholder='Diameter' onChange={this.handleDiameter}/>
              <Form.Select fluid options={landSelectable} placeholder='Land' onChange={this.handleLand} />
              <Form.Select fluid options={statusSelectable} placeholder='Status' onChange={this.handleStatus}/>
              <Form.Select fluid options={ownershipSelectable} placeholder='Ownership' onChange={this.handleOwnership}/>
              <Form.Select fluid options={this.state.species} placeholder='Species' onChange={this.handleSpecies}/>
              <Form.Select fluid options={this.state.municipalities} placeholder='Municipalities' onChange={this.handleMunicipality}/>
              <Form.Button inverted color='green' size='small' onClick={this.handleSignUp}>Update Tree</Form.Button>
              <Form.Button inverted color='red' size='small' onClick={this.handleClose}>Close</Form.Button>
            </Form>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

export default UpdateTreeModal;