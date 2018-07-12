import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {GoogleMap, Polygon, withScriptjs, withGoogleMap} from 'react-google-maps';
import {Button, Divider, Header, Icon, Grid, Modal} from 'semantic-ui-react';
import {getMapBounds} from './Utils';
import {gmapsKey} from '../constants';

class MunicipalityModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      update: false,
      showBorders: true,
      error: ''
    };
  }

  onToggleEdit = () => this.setState((prevState) => ({update: !prevState.update}));

  onShowBorders = () => {
    this.setState({showBorders: !this.state.showBorders});
  }

  onUpdateMunicipality = () => {
    this.setState({update: !this.state.update});
  }

  onGMapLoaded = () => {
    let viewport = getMapBounds(this.props.municipality.borders);
    this.refs.map.fitBounds(viewport);
  }

  render() {
    const {municipality} = this.props;

    return (
      <Modal open size='small' dimmer='blurring'>
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
                <Icon name='point' onClick={this.onShowBorders}/>Borders
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
            <GMap municipality={municipality}/>
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
                <Button inverted color='red' size='small' onClick={() => this.props.onClose(null)}>Close</Button>
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
)(({municipality}) => (
  <GoogleMap options={{scrollwheel: false}}>
    <Polygon key={municipality.name} paths={municipality.borders}/>
  </GoogleMap>
));

MunicipalityModal.propTypes = {
  municipality: PropTypes.object.isRequired,
  onClose: PropTypes.func.isRequired
};

export default MunicipalityModal;
