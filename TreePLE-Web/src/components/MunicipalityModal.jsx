import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {GoogleMap, Polygon, withScriptjs, withGoogleMap} from 'react-google-maps';
import {Button, Divider, Header, Icon, Grid, Modal} from 'semantic-ui-react';
import {getAllMunicipalities, updateMunicipality} from './Requests';
import {getLatLngBorders, getMapBounds} from './Utils';
import {gmapsKey, mtlCenter} from '../constants';

class MunicipalityModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      municipalities: [],
      borders: [],
      update: false,
      showBorders: false,
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
        })).filter((municipality) => municipality.name !== this.props.municipality.name);

        this.setState({municipalities: municipalities});
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onToggleEdit = () => this.setState((prevState) => ({update: !prevState.update}));

  onShowBorders = () => this.setState((prevState) => ({showBorders: !prevState.showBorders}));

  onEdit = (polygon) => this.setState({borders: polygon.getPaths().b[0].b});

  onUpdateMunicipality = () => {
    const municipalityParams = {
      name: this.props.municipality.name,
      totalTrees: this.props.municipality.totalTrees,
      borders: this.state.borders.map((location) => [location.lat(), location.lng()])
    };

    updateMunicipality(municipalityParams)
      .then(() => {
        this.props.onClose(null, true);
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  render() {
    const {municipality} = this.props;

    return (
      <Modal open size='large' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='map' circular/>
              <Header.Content>{!this.state.update ? 'View' : 'Update'} Municipality</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Grid textAlign='center' columns={2}>
              <Grid.Row>
                <Grid.Column>
                  <Header as='h3' content='Name'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Total Trees'/>
                </Grid.Column>
              </Grid.Row>
              <Grid.Row>
                <Grid.Column>{municipality.name}</Grid.Column>
                <Grid.Column>{municipality.totalTrees}</Grid.Column>
              </Grid.Row>
            </Grid>

            <Header as='h3' textAlign='center'>
              <Header.Content>
                <Icon name='map marker alternate' onClick={this.onShowBorders}/>Borders
              </Header.Content>
            </Header>
            {this.state.showBorders ? (
              <div>
                <Grid textAlign='center' columns={3}>
                  <Grid.Column>
                    <Header as='h4' content='Location ID'/>
                  </Grid.Column>
                  <Grid.Column>
                    <Header as='h4' content='Latitude'/>
                  </Grid.Column>
                  <Grid.Column>
                    <Header as='h4' content='Longitude'/>
                  </Grid.Column>
                </Grid>
                <Divider/>
                <Grid textAlign='center' columns={3}>
                  {municipality.borders.map(({id, lat, lng}) => (
                    <Grid.Row key={id}>
                      <Grid.Column>{id}</Grid.Column>
                      <Grid.Column>{lat}</Grid.Column>
                      <Grid.Column>{lng}</Grid.Column>
                    </Grid.Row>
                  ))}
                </Grid>
              </div>
            ) : null}

            <Divider hidden/>
            <GMap municipality={municipality} municipalities={this.state.municipalities} update={this.state.update} onEdit={this.onEdit}/>
            <Divider hidden/>

            {this.state.error ? (
              <Message error>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

            <Grid centered>
              <Grid.Row>
                {!this.state.update ? (
                  <Button inverted color='blue' size='small' disabled={!this.state.user} onClick={this.onToggleEdit}>Edit</Button>
                ) : (
                  <div>
                    <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onUpdateMunicipality}>Save</Button>
                    <Button inverted color='orange' size='small' onClick={this.onToggleEdit}>Back</Button>
                  </div>
                )}
                <Button inverted color='red' size='small' onClick={() => this.props.onClose(null, false)}>Close</Button>
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
    loadingElement: <div style={{width: '100vw', height: '60vh'}}/>,
    containerElement: <div style={{height: '60vh'}}/>,
    mapElement: <div style={{height: '60vh'}}/>
  }),
  withScriptjs,
  withGoogleMap
)(({municipality, municipalities, update, onEdit}) => {
  let gmap;
  let polygon;
  let init = true;

  const onGMapLoaded = () => {
    if (init) {
      let viewport = getMapBounds(municipality.borders);
      gmap.fitBounds(viewport);
      init = false;
    }
  };

  return update ? (
    <GoogleMap ref={(ref) => gmap = ref} zoom={13} center={mtlCenter} options={{scrollwheel: true}}>
      <Polygon
        editable
        ref={(ref) => polygon = ref}
        key={'#EDIT#' + municipality.name}
        paths={municipality.borders}
        options={{fillColor: '#8BDFB9'}}
        onMouseUp={() => onEdit(polygon)}
      />
      {municipalities.map((muni) => (
        <Polygon key={muni.name} paths={muni.borders}/>
      ))}
    </GoogleMap>
  ) : (
    <GoogleMap ref={(ref) => gmap = ref} zoom={13} center={mtlCenter} options={{scrollwheel: true}} onIdle={onGMapLoaded}>
      <Polygon key={municipality.name} paths={municipality.borders}/>
    </GoogleMap>
  );
});

MunicipalityModal.propTypes = {
  municipality: PropTypes.object.isRequired,
  onClose: PropTypes.func.isRequired
};

export default MunicipalityModal;
