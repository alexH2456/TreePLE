import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Button, Divider, Dropdown, Form, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import {GoogleMap, Marker, withScriptjs, withGoogleMap} from 'react-google-maps';
import DayPickerInput from 'react-day-picker/DayPickerInput';
import {createTree, getAllSpecies, getAllMunicipalities} from './Requests';
import {getSelectable, getError, getTreeMarker, formatDate} from './Utils';
import {gmapsKey, huDates, landSelectable, statusSelectable, ownershipSelectable, flags} from '../constants';

class CreateTreeModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
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
      municipalitySelectable: [],
      error: ''
    };
  }

  componentWillMount() {
    const speciesProm = getAllSpecies().then(({data}) => data).catch(({response: {data}}) => data);
    const municipalityProm = getAllMunicipalities().then(({data}) => data).catch(({response: {data}}) => data);

    Promise.all([speciesProm, municipalityProm])
      .then(([species, municipalities]) => {
        this.setState({
          speciesSelectable: getSelectable(species),
          municipalitySelectable: getSelectable(municipalities)
        });
      })
      .catch(() => {
        this.setState({error: 'Unable to retrieve Species/Municipality list!'});
      });
  }

  onCreateTree = () => {
    const treeParams = {
      user: this.state.user,
      tree: {
        ...this.state.tree,
        datePlanted: formatDate(this.state.tree.datePlanted)
      }
    };

    createTree(treeParams)
      .then(() => {
        this.props.onClose({}, true);
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onHeightChange = (e, {value}) => this.setState((prevState) => ({tree: {...prevState.tree, height: value}}));
  onDiameterChange = (e, {value}) => this.setState((prevState) => ({tree: {...prevState.tree, diameter: value}}));
  onDateChange = (day) => this.setState((prevState) => ({tree: {...prevState.tree, datePlanted: day}}));
  onFlagChange = (e, {value}) => this.setState({language: value});
  onSpeciesChange = (e, {value}) => this.setState((prevState) => ({tree: {...prevState.tree, species: value}}));
  onStatusChange = (e, {value}) => this.setState((prevState) => ({tree: {...prevState.tree, status: value}}));
  onMunicipalityChange = (e, {value}) => this.setState((prevState) => ({tree: {...prevState.tree, municipality: value}}));
  onOwnershipChange = (e, {value}) => this.setState((prevState) => ({tree: {...prevState.tree, ownership: value}}));
  onLandChange = (e, {value}) => this.setState((prevState) => ({tree: {...prevState.tree, land: value}}));

  onTreeDrag = ({latLng}) => this.setState((prevState) => ({tree: {...prevState.tree, latitude: latLng.lat(), longitude: latLng.lng()}}));
  onTreeDragEnd = ({latLng}) => this.setState({location: latLng});

  render() {
    const {tree} = this.state;
    const errors = getError(this.state.error);

    const dayPickerProps = {
      locale: this.state.language,
      weekdaysShort: this.state.language === 'hu' ? huDates.weekShort : undefined,
      weekdaysLong: this.state.language === 'hu' ? huDates.weekLong : undefined,
      months: this.state.language === 'hu' ? huDates.months : undefined
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
                <Form.Input fluid label='Height (cm)' placeholder='Height' type='number' min='1' error={errors.height} onChange={this.onHeightChange}/>
                <Form.Input fluid label='Diameter (cm)' placeholder='Diameter' type='number' min='1' error={errors.diameter} onChange={this.onDiameterChange}/>
                <Form.Input label='Date Planted' error={errors.date}>
                  <DayPickerInput placeholder='YYYY-MM-DD' dayPickerProps={dayPickerProps} value={tree.datePlanted} onDayChange={this.onDateChange}/>
                  <Dropdown compact selection options={flags} defaultValue={flags[0].key} onChange={this.onFlagChange}/>
                </Form.Input>
              </Form.Group>
              <Form.Group widths='equal'>
                <Form.Select fluid options={this.state.speciesSelectable} label='Species' placeholder='Species' error={errors.species} onChange={this.onSpeciesChange}/>
                <Form.Select fluid options={statusSelectable} label='Status' placeholder='Status' error={errors.status} onChange={this.onStatusChange}/>
              </Form.Group>
              <Form.Group widths='equal'>
                <Form.Select fluid options={this.state.municipalitySelectable} label='Municipality' placeholder='Municipality' error={errors.municipality} onChange={this.onMunicipalityChange}/>
                <Form.Select fluid options={ownershipSelectable} label='Ownership' placeholder='Ownership' error={errors.ownership} onChange={this.onOwnershipChange}/>
                <Form.Select fluid options={landSelectable} label='Land' placeholder='Land' error={errors.land} onChange={this.onLandChange}/>
              </Form.Group>
              <Form.Group widths='equal'>
                <Form.Input fluid readOnly label='Latitude' placeholder='Latitude' type='number' step='any' min='-90' max='90' value={tree.latitude} error={errors.location}/>
                <Form.Input fluid readOnly label='Longitude' placeholder='Longitude' type='number' step='any' min='-180' max='180' value={tree.longitude} error={errors.location}/>
              </Form.Group>
            </Form>

            <Divider hidden/>
            <GMap location={this.state.location} onDrag={this.onTreeDrag} onDragEnd={this.onTreeDragEnd}/>
            <Divider hidden/>

            {this.state.error ? (
              <Message error>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateTree}>Create</Button>
                <Button inverted color='red' size='small' onClick={(e) => this.props.onClose(e, false)}>Close</Button>
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
    mapElement: <div style={{height: '40vh'}}/>
  }),
  withScriptjs,
  withGoogleMap
)(({location, onDrag, onDragEnd}) => (
  <GoogleMap zoom={15} center={location} options={{scrollwheel: false}}>
    <Marker
      draggable
      icon={{
        url: getTreeMarker(null),
        anchor: new google.maps.Point(23, 45),
        scaledSize: new google.maps.Size(40, 60)
      }}
      position={location}
      onDrag={onDrag}
      onDragEnd={onDragEnd}
    />
  </GoogleMap>
));

CreateTreeModal.propTypes = {
  location: PropTypes.object.isRequired,
  onClose: PropTypes.func.isRequired
};

export default CreateTreeModal;
