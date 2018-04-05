import React, {PureComponent} from 'react';
import {Button, Image, Modal, Form} from 'semantic-ui-react';
import {getAllSpecies} from './Requests';

class UpdateTreeModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalOpen: false,
      treeSpecies: [],
      species:'',
      ownership:'',
      land:'',
      status:'',
      height:'',
      diameter:''
    }
  }

  componentDidMount() {
    getAllSpecies()
     .then(response => {
       console.log(response.data);
       let speciesSelectable = []
       response.data.forEach((species, idx) => {
          speciesSelectable.push({
            key: idx,
            text: species.name,
            value: species.name
          });
       });
       this.setState({treeSpecies: speciesSelectable});
       console.log(speciesSelectable);
       //response.data.map

     })

  }

  handleOpen = () => this.setState({modalOpen: true});

  handleClose = () => this.setState({modalOpen: false});

  handleSpecies = (event, data) => this.setState({species: data.value});
  handleOwnership = (event, data) => this.setState({ownership: data.value});
  handleLand = (event, data) => this.setState({land: data.value});
  handleStatus = (event, data) => this.setState({status: data.value});
  handleHeight = (event, data) => this.setState({height: data.value});
  handleDiameter = (event, data) => this.setState({diameter: data.value});

  render() {
    console.log(this.state);
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
              <Form.Input fluid placeholder='Height' onChange={this.handleHeight}/>
              <Form.Input fluid placeholder='Diameter' onChange={this.handleDiameter}/>
              <Form.Select fluid options={optionsLand} placeholder='Land' onChange={this.handleLand} />
              <Form.Select fluid options={optionsStatus} placeholder='Status' onChange={this.handleStatus}/>
              <Form.Select fluid options={optionsOwnership} placeholder='Ownership' onChange={this.handleOwnership}/>
              <Form.Select fluid options={this.state.treeSpecies} placeholder='Species' onChange={this.handleSpecies}/>
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
