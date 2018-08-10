import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Button, Divider, Form, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import {GoogleMap, Polygon, withScriptjs, withGoogleMap} from 'react-google-maps';
import {DrawingManager} from 'react-google-maps/lib/components/drawing/DrawingManager';
import {getAllMunicipalities, createMunicipality} from './Requests';
import {getLatLngBorders, getError} from './Utils';
import {gmapsKey, mtlCenter} from '../constants';


class CreateMunicipalityModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      municipalities: [],
      area: null,
      mapMunicipality: null,
      name: '',
      borders: [],
      error: ''
    };
  }

  componentWillMount() {
    getAllMunicipalities()
      .then(({data}) => {
        let municipalities = data.map((municipality) => ({
          name: municipality.name,
          totalTrees: municipality.totalTrees,
          borders: getLatLngBorders(municipality.borders)
        }));

        this.setState({municipalities: municipalities});
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onCreateMunicipality = () => {
    const municipalityParams = {
      name: this.state.name,
      totalTrees: 0,
      borders: this.state.borders
    };

    createMunicipality(municipalityParams)
      .then(() => {
        this.props.onClose(true);
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onNameChange = (e, {value}) => this.setState({name: value});

  onMunicipalityClick = (municipality) => this.setState((prevState) => ({mapMunicipality: prevState.mapMunicipality !== municipality ? municipality : null}));

  onAreaComplete = (area) => this.setState({area: area, borders: area.getPath().b.map((location) => [location.lat(), location.lng()])});

  onResetClick = () => {
    if (this.state.area) {
      this.state.area.setMap(null);
    }

    this.setState({
      area: null,
      borders: []
    });
  }

  render() {
    const errors = getError(this.state.error);

    return (
      <Modal open size='fullscreen' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='map' circular/>
              <Header.Content>Create Municipality</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Grid columns='equal'>
              <Grid.Column>
                <Form>
                  <Form.Input fluid label='Name' placeholder='Name' error={errors.municipality} onChange={this.onNameChange}/>
                </Form>
              </Grid.Column>
            </Grid>

            <Header as='h3' textAlign='center'>
              <Header.Content>
                <Icon name='tree'/>Tree Map
              </Header.Content>
            </Header>

            <Divider/>

            {this.state.mapMunicipality ? (
              <div>
                <Grid textAlign='center' columns={2}>
                  <Grid.Column>
                    <Header as='h4' content='Name'/>
                  </Grid.Column>
                  <Grid.Column>
                    <Header as='h4' content='Total Trees'/>
                  </Grid.Column>
                </Grid>
                <Divider/>
                <Grid textAlign='center' columns={2}>
                  <Grid.Row key={this.state.mapMunicipality.name}>
                    <Grid.Column>{this.state.mapMunicipality.name}</Grid.Column>
                    <Grid.Column>{this.state.mapMunicipality.totalTrees}</Grid.Column>
                  </Grid.Row>
                </Grid>
              </div>
            ) : null}

            <Divider hidden/>
            <GMap municipalities={this.state.municipalities} onMunicipalityClick={this.onMunicipalityClick} onAreaComplete={this.onAreaComplete} onResetClick={this.onResetClick}/>
            <Divider hidden/>

            {this.state.error ? (
              <Message error>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateMunicipality}>Create</Button>
                <Button inverted color='red' size='small' onClick={() => this.props.onClose(false)}>Close</Button>
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
    loadingElement: <div style={{width: '100vw', height: '80vh'}}/>,
    containerElement: <div style={{height: '80vh'}}/>,
    mapElement: <div style={{height: '80vh'}}/>
  }),
  withScriptjs,
  withGoogleMap
)(({municipalities, onMunicipalityClick, onAreaComplete, onResetClick}) => {
  const OT = google.maps.drawing.OverlayType;
  const areaOptions = {fillColor: '#8BDFB9'};

  return (
    <GoogleMap zoom={12} center={mtlCenter} options={{scrollwheel: true}} onRightClick={onResetClick}>
      <DrawingManager
        options={{
          polygonOptions: areaOptions,
          rectangleOptions: areaOptions,
          drawingControlOptions: {
            position: google.maps.ControlPosition.TOP_CENTER,
            drawingModes: [OT.POLYGON, OT.RECTANGLE]
          }
        }}
        onPolygonComplete={onAreaComplete}
        onRectangleComplete={onAreaComplete}
      />
      {municipalities.map((municipality) => (
        <Polygon
          key={municipality.name}
          paths={municipality.borders}
          onClick={() => onMunicipalityClick(municipality)}
          onRightClick={onResetClick}
        />
      ))}
    </GoogleMap>
  );
});

CreateMunicipalityModal.propTypes = {
  onClose: PropTypes.func.isRequired
};

export default CreateMunicipalityModal;
