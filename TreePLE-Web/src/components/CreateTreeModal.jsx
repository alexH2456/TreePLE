import React, {PureComponent} from 'react';
import {compose, withProps} from 'recompose';
import {Button, Divider, Header, Icon, Input, Form, Grid, Modal, Flag, Segment, Dropdown} from 'semantic-ui-react';
import {GoogleMap, Marker, withScriptjs, withGoogleMap} from 'react-google-maps';
import DayPickerInput from 'react-day-picker/DayPickerInput';
import {createTree} from "./Requests";
import {getSpeciesSelectable, getMunicipalitySelectable, getLatLngBorders, formatDate} from './Utils';
import {gmapsKey, landSelectable, statusSelectable, ownershipSelectable} from '../constants';

class CreateTreeModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: '',
      tree: {
        height: null,
        diameter: null,
        datePlanted: new Date(),
        land: '',
        status: '',
        ownership: '',
        species: '',
        latitude: props.location.lat(),
        longitude: props.location.lng(),
        municipality: ''
      },
      location: props.location,
      language: 'en',
      speciesSelectable: [],
      municipalitySelectable: []
    };
  }

  componentWillMount() {
    this.setState({
      user: localStorage.getItem('username'),
      speciesSelectable: getSpeciesSelectable(),
      municipalitySelectable: getMunicipalitySelectable()
    });
  }

  onCreateTree = () => {
    const newTree = {
      user: this.state.user,
      tree: {
        ...this.state.tree,
        datePlanted: formatDate(this.state.tree.datePlanted)
      }
    };

    createTree(newTree)
      .then(({data}) => {
        this.props.onClose(null, true);
      })
      .catch(error => {
        console.log(error);
      });
  }

  onHeightChange = (e, {value}) => this.setState(prevState => ({tree: {...prevState.tree, height: value}}));
  onDiameterChange = (e, {value}) => this.setState(prevState => ({tree: {...prevState.tree, diameter: value}}));
  onDateChange = (day, {selected}) => this.setState(prevState => ({tree: {...prevState.tree, datePlanted: day}}));
  onFlagChange = (e, {value}) => this.setState({language: value});
  onSpeciesChange = (e, {value}) => this.setState(prevState => ({tree: {...prevState.tree, species: value}}));
  onStatusChange = (e, {value}) => this.setState(prevState => ({tree: {...prevState.tree, status: value}}));
  onMunicipalityChange = (e, {value}) => this.setState(prevState => ({tree: {...prevState.tree, municipality: value}}));
  onOwnershipChange = (e, {value}) => this.setState(prevState => ({tree: {...prevState.tree, ownership: value}}));
  onLandChange = (e, {value}) => this.setState(prevState => ({tree: {...prevState.tree, land: value}}));

  onTreeDrag = ({latLng}) => this.setState(prevState => ({tree: {...prevState.tree, latitude: latLng.lat(), longitude: latLng.lng()}}));
  onTreeDragEnd = ({latLng}) => this.setState({location: latLng});

  render() {
    const {tree} = this.state;

    const flags = [
      {key: 'en', value: 'en', text: <Flag name='ca'/>},
      {key: 'hu', value: 'hu', text: <Flag name='hu'/>}
    ];
    const hu = {
      weekShort: ['Vas', 'H', 'K', 'Sze', 'Csüt', 'P', 'Szo'],
      weekLong: ['Vasárnap', 'Hétfő', 'Kedd', 'Szerda', 'Csütörtök', 'Péntek', 'Szombaton'],
      months: ['Január', 'Február', 'Március', 'Április', 'Május', 'Junius', 'Julius', 'Augusztus', 'Szeptember', 'Október', 'November', 'December']
    };
    const dayPickerProps = {
      locale: this.state.language,
      weekdaysShort: this.state.language == 'hu' ? hu.weekShort : undefined,
      weekdaysLong: this.state.language == 'hu' ? hu.weekLong : undefined,
      months: this.state.language == 'hu' ? hu.months : undefined
    };

    return (
      <Modal open size='small' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='tree' circular/>
              <Header.Content>Create Tree</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Form>
              <Form.Group widths='equal'>
                <Form.Input fluid label='Height (cm)' placeholder='Height' type='number' min='1' onChange={this.onHeightChange}/>
                <Form.Input fluid label='Diameter (cm)' placeholder='Diameter' type='number' min='1' onChange={this.onDiameterChange}/>
                <Form.Input label='Date Planted'>
                  <DayPickerInput placeholder='YYYY-MM-DD' format='YYYY-M-D' value={tree.datePlanted} dayPickerProps={dayPickerProps} onDayChange={this.onDateChange}/>
                  <Dropdown compact selection options={flags} defaultValue={flags[0].key} onChange={this.onFlagChange}/>
                </Form.Input>
              </Form.Group>
              <Form.Group widths='equal'>
                <Form.Select fluid options={this.state.speciesSelectable} label='Species' placeholder='Species' onChange={this.onSpeciesChange}/>
                <Form.Select fluid options={statusSelectable} label='Status' placeholder='Status' onChange={this.onStatusChange}/>
              </Form.Group>
              <Form.Group widths='equal'>
                <Form.Select fluid options={this.state.municipalitySelectable} label='Municipality' placeholder='Municipality' onChange={this.onMunicipalityChange}/>
                <Form.Select fluid options={ownershipSelectable} label='Ownership' placeholder='Ownership' onChange={this.onOwnershipChange}/>
                <Form.Select fluid options={landSelectable} label='Land' placeholder='Land' onChange={this.onLandChange} />
              </Form.Group>
              <Form.Group widths='equal'>
                <Form.Input fluid readOnly label='Latitude' placeholder='Latitude' type='number' min='-90' max='90' value={tree.latitude}/>
                <Form.Input fluid readOnly label='Longitude' placeholder='Longitude' type='number' min='-180' max='180' value={tree.longitude}/>
              </Form.Group>
            </Form>

            <Divider hidden/>
            <GMap location={this.state.location} onDrag={this.onTreeDrag} onDragEnd={this.onTreeDragEnd}/>
            <Divider hidden/>

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateTree}>Create</Button>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

const GMap = compose(
  withProps({
    googleMapURL: `https://maps.googleapis.com/maps/api/js?key=${gmapsKey}&v=3.exp&libraries=geometry,drawing,places`,
    loadingElement: <div style={{width: '100vw', height: '40vh'}}/>,
    containerElement: <div style={{height: '40vh'}}/>,
    mapElement: <div style={{height: '40vh'}}/>,
  }),
  withScriptjs,
  withGoogleMap
)(({location, onDrag, onDragEnd}) => {
  return (
    <GoogleMap zoom={15} center={location} options={{scrollwheel: false}}>
      <Marker draggable position={location} onDrag={onDrag} onDragEnd={onDragEnd}/>
    </GoogleMap>
  );
});

export default CreateTreeModal;
