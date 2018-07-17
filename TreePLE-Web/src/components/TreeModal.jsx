import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Button, Divider, Form, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import {GoogleMap, Marker, Polygon, withScriptjs, withGoogleMap} from 'react-google-maps';
import {getAllSpecies, getAllMunicipalities, updateTree} from './Requests';
import {getSelectable, getLatLng, getLatLngBorders, getError, getTreeMarker} from './Utils';
import {gmapsKey, landSelectable, statusSelectable, ownershipSelectable} from '../constants';

class TreeModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      updatedTree: {
        treeId: props.tree.treeId,
        height: props.tree.height,
        diameter: props.tree.diameter,
        land: props.tree.land,
        status: props.tree.status,
        ownership: props.tree.ownership,
        species: props.tree.species.name,
        municipality: props.tree.municipality.name
      },
      update: false,
      showReports: false,
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

  onToggleEdit = () => this.setState((prevState) => ({update: !prevState.update}));

  onUpdateTree = () => {
    const treeParams = {
      user: this.state.user,
      tree: this.state.updatedTree
    };

    updateTree(treeParams)
      .then(() => {
        this.props.onClose(null, true);
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onShowReports = () => this.setState((prevState) => ({showReports: !prevState.showReports}));

  onHeightChange = (e, {value}) => this.setState((prevState) => ({updatedTree: {...prevState.updatedTree, height: value}}));
  onDiameterChange = (e, {value}) => this.setState((prevState) => ({updatedTree: {...prevState.updatedTree, diameter: value}}));
  onSpeciesChange = (e, {value}) => this.setState((prevState) => ({updatedTree: {...prevState.updatedTree, species: value}}));
  onStatusChange = (e, {value}) => this.setState((prevState) => ({updatedTree: {...prevState.updatedTree, status: value}}));
  onMunicipalityChange = (e, {value}) => this.setState((prevState) => ({updatedTree: {...prevState.updatedTree, municipality: value}}));
  onOwnershipChange = (e, {value}) => this.setState((prevState) => ({updatedTree: {...prevState.updatedTree, ownership: value}}));
  onLandChange = (e, {value}) => this.setState((prevState) => ({updatedTree: {...prevState.updatedTree, land: value}}));


  render() {
    const {tree} = this.props;
    const {updatedTree} = this.state;

    const errors = getError(this.state.error);

    return (
      <Modal open size='small' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='tree' circular/>
              <Header.Content>{!this.state.update ? 'View' : 'Update'} Tree</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            {!this.state.update ? (
              <div>
                <Grid textAlign='center'>
                  <Grid.Row columns={2}>
                    <Grid.Column>
                      <Header as='h3' content='Tree ID'/>
                    </Grid.Column>
                    <Grid.Column>
                      <Header as='h3' content='Date Planted'/>
                    </Grid.Column>
                  </Grid.Row>
                  <Grid.Row columns={2}>
                    <Grid.Column>{tree.treeId}</Grid.Column>
                    <Grid.Column>{tree.datePlanted}</Grid.Column>
                  </Grid.Row>

                  <Grid.Row columns={3}>
                    <Grid.Column>
                      <Header as='h3' content='Species'/>
                    </Grid.Column>
                    <Grid.Column>
                      <Header as='h3' content='Height (cm)'/>
                    </Grid.Column>
                    <Grid.Column>
                      <Header as='h3' content='Diameter (cm)'/>
                    </Grid.Column>
                  </Grid.Row>
                  <Grid.Row columns={3}>
                    <Grid.Column>{tree.species.name}</Grid.Column>
                    <Grid.Column>{tree.height}</Grid.Column>
                    <Grid.Column>{tree.diameter}</Grid.Column>
                  </Grid.Row>

                  <Grid.Row columns={3}>
                    <Grid.Column>
                      <Header as='h3' content='Status'/>
                    </Grid.Column>
                    <Grid.Column>
                      <Header as='h3' content='Ownership'/>
                    </Grid.Column>
                    <Grid.Column>
                      <Header as='h3' content='Land'/>
                    </Grid.Column>
                  </Grid.Row>
                  <Grid.Row columns={3}>
                    <Grid.Column>{tree.status}</Grid.Column>
                    <Grid.Column>{tree.ownership}</Grid.Column>
                    <Grid.Column>{tree.land}</Grid.Column>
                  </Grid.Row>

                  <Grid.Row columns={3}>
                    <Grid.Column>
                      <Header as='h3' content='Municipality'/>
                    </Grid.Column>
                    <Grid.Column>
                      <Header as='h3' content='Latitude'/>
                    </Grid.Column>
                    <Grid.Column>
                      <Header as='h3' content='Longitude'/>
                    </Grid.Column>
                  </Grid.Row>
                  <Grid.Row columns={3}>
                    <Grid.Column>{tree.municipality.name}</Grid.Column>
                    <Grid.Column>{tree.location.latitude}</Grid.Column>
                    <Grid.Column>{tree.location.longitude}</Grid.Column>
                  </Grid.Row>
                </Grid>

                <Header as='h3' textAlign='center'>
                  <Header.Content>
                    <Icon name='wpforms' onClick={this.onShowReports}/>Reports
                  </Header.Content>
                </Header>
                {this.state.showReports ? (
                  <div>
                    <Grid textAlign='center' columns={3}>
                      <Grid.Column>
                        <Header as='h4' content='Report ID'/>
                      </Grid.Column>
                      <Grid.Column>
                        <Header as='h4' content='User'/>
                      </Grid.Column>
                      <Grid.Column>
                        <Header as='h4' content='Date Modified'/>
                      </Grid.Column>
                    </Grid>

                    <Divider/>

                    <Grid textAlign='center' columns={3}>
                      {tree.reports.map(({reportId, reportUser, reportDate}) => (
                        <Grid.Row key={reportId}>
                          <Grid.Column>{reportId}</Grid.Column>
                          <Grid.Column>{reportUser}</Grid.Column>
                          <Grid.Column>{reportDate}</Grid.Column>
                        </Grid.Row>
                      ))}
                    </Grid>
                  </div>
                ) : null}
              </div>
            ) : (
              <Form>
                <Form.Group widths='equal'>
                  <Form.Input readOnly fluid label='Tree ID' value={tree.treeId}/>
                  <Form.Input readOnly fluid label='Date Planted' value={tree.datePlanted}/>
                </Form.Group>
                <Form.Group widths='equal'>
                  <Form.Input
                    label='Height (cm)'
                    fluid placeholder='Height' type='number' min='1' value={updatedTree.height} error={errors.height} onChange={this.onHeightChange}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.height}/>Height (cm)</span>}
                  />
                  <Form.Input
                    label='Diameter (cm)'
                    fluid placeholder='Diameter' type='number' min='1' value={updatedTree.diameter} error={errors.diameter} onChange={this.onDiameterChange}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.diameter}/>Diameter (cm)</span>}
                  />
                </Form.Group>
                <Form.Group widths='equal'>
                  <Form.Select
                    label='Species'
                    fluid options={this.state.speciesSelectable} placeholder='Species' value={updatedTree.species} error={errors.species} onChange={this.onSpeciesChange}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.species}/>Species</span>}
                  />
                  <Form.Select
                    label='Status'
                    fluid options={statusSelectable} placeholder='Status' value={updatedTree.status} error={errors.status} onChange={this.onStatusChange}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.status}/>Status</span>}
                  />
                </Form.Group>
                <Form.Group widths='equal'>
                  <Form.Select
                    label='Municipality'
                    fluid options={this.state.municipalitySelectable} placeholder='Municipality' value={updatedTree.municipality} error={errors.municipality} onChange={this.onMunicipalityChange}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.municipality}/>Municipality</span>}
                  />
                  <Form.Select
                    label='Ownership'
                    fluid options={ownershipSelectable} placeholder='Ownership' value={updatedTree.ownership} error={errors.ownership} onChange={this.onOwnershipChange}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.ownership}/>Ownership</span>}
                  />
                  <Form.Select
                    label='Land'
                    fluid options={landSelectable} placeholder='Land' value={updatedTree.land} error={errors.land} onChange={this.onLandChange}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.land}/>Land</span>}
                  />
                </Form.Group>
                <Form.Group widths='equal'>
                  <Form.Input
                    label='Latitude'
                    fluid readOnly placeholder='Latitude' type='number' step='any' min='-90' max='90' value={tree.location.latitude}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.latitude}/>Latitude</span>}
                  />
                  <Form.Input
                    label='Longitude'
                    fluid readOnly placeholder='Longitude' type='number' step='any' min='-180' max='180' value={tree.location.longitude}
                    // label={<span><Popup trigger={<Icon name='help'/>} content={treeHelp.longitude}/>Longitude</span>}
                  />
                </Form.Group>
              </Form>
            )}

            <Divider hidden/>
            <GMap tree={tree}/>
            <Divider hidden/>

            {this.state.error && this.state.update ? (
              <Message error size='tiny'>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

            <Grid centered>
              <Grid.Row>
                {!this.state.update ? (
                  <Button inverted color='blue' size='small' disabled={!this.state.user} onClick={this.onToggleEdit}>Edit</Button>
                ) : (
                  <div>
                    <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onUpdateTree}>Save</Button>
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
    loadingElement: <div style={{width: '100vw', height: '40vh'}}/>,
    containerElement: <div style={{height: '40vh'}}/>,
    mapElement: <div style={{height: '40vh'}}/>
  }),
  withScriptjs,
  withGoogleMap
)(({tree}) => {
  let borders = getLatLngBorders(tree.municipality.borders);
  let location = getLatLng(tree.location);

  return (
    <GoogleMap zoom={15} center={location} options={{scrollwheel: false}}>
      <Marker
        key={tree.treeId}
        icon={{
          url: getTreeMarker(tree.status),
          anchor: new google.maps.Point(23, 45),
          scaledSize: new google.maps.Size(40, 60)
        }}
        position={location}
      />
      <Polygon key={tree.municipality.name} paths={borders} options={{fillOpacity: 0.1}}/>
    </GoogleMap>
  );
});

TreeModal.propTypes = {
  tree: PropTypes.object.isRequired,
  onClose: PropTypes.func.isRequired
};

export default TreeModal;
