import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Container, Grid, Icon, Loader} from 'semantic-ui-react';
import {GoogleMap, InfoWindow, Marker, Polygon, withScriptjs, withGoogleMap} from 'react-google-maps';
import TreeModal from './TreeModal';
import CreateTreeModal from './CreateTreeModal';
import MunicipalityModal from './MunicipalityModal';
import {getAllTrees, getAllMunicipalities, getMunicipalitySustainability, getTreeSustainability} from './Requests';
import {getLatLng, getLatLngBorders, getMapBounds, getTreeIcons, getTreeMarker} from './Utils';
import {gmapsKey, mtlCenter} from '../constants';

export class TreeMap extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      center: {},
      trees: [],
      municipalities: [],
      treeModal: null,
      createTreeModal: null,
      treeHover: null,
      treeInfo: false,
      municipalityModal: null,
      error: ''
    };
  }

  componentWillMount() {
    this.getUserLocation();
    this.loadMap();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.refreshMap === true) {
      this.loadMap();
    }
  }

  getUserLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          this.setState({
            center: {
              lat: position.coords.latitude,
              lng: position.coords.longitude
            }
          });
        },
        () => {
          this.setState({center: mtlCenter});
          alert('Unable to find your location.');
        }, {
          timeout: 5000,
          enableHighAccuracy: true
        }
      );
    }
  }

  loadMap = () => {
    this.loadTrees();
    this.loadMunicipalities();
  }

  loadTrees = () => {
    getAllTrees()
      .then(({data}) => {
        this.setState({trees: data});
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  loadMunicipalities = () => {
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

  onMapClick = (e, success) => {
    if (success) {
      this.loadTrees();
    }
    this.setState({createTreeModal: 'latLng' in e ? e.latLng : null});
  }

  onMunicipalityClick = (municipality) => {
    getMunicipalitySustainability(municipality.name)
      .then(({data}) => ({
        stormwater: data.stormwater,
        co2Reduced: data.co2Reduced,
        biodiversity: data.biodiversity,
        energyConserved: data.energyConserved
      }))
      .then((sustainability) => {
        let viewport = getMapBounds(municipality.borders);
        this.refs.gmap.fitBounds(viewport);
        this.props.onSustainabilityChange(sustainability);
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onMunicipalityDblClick = (municipality, success) => {
    if (success) {
      this.loadMap();
    }
    this.setState({municipalityModal: municipality});
  }

  onTreeHover = (tree) => {
    if (!this.state.treeInfo) {
      this.setState({treeHover: tree});
    }
  }

  onTreeClick = (tree) => {
    this.setState((prevState) => ({
      treeHover: tree,
      treeInfo: tree === prevState.treeHover ? !prevState.treeInfo : tree !== null ? prevState.treeInfo : false
    }));

    if (tree !== null) {
      getTreeSustainability(tree.treeId)
        .then(({data}) => ({
          stormwater: data.stormwater,
          co2Reduced: data.co2Reduced,
          biodiversity: data.biodiversity,
          energyConserved: data.energyConserved
        }))
        .then((sustainability) => {
          this.props.onSustainabilityChange(sustainability);
        })
        .catch(({response: {data}}) => {
          this.setState({error: data.message});
        });
    }
  }

  onTreeDblClick = (tree, success) => {
    if (success) {
      this.loadTrees();
    }
    this.setState({
      treeHover: null,
      treeInfo: null,
      treeModal: tree
    });
  }

  render() {
    const TreeInfoWindow = ({tree, icons}) => (
      <InfoWindow onCloseClick={() => this.onTreeClick(null)}>
        <Container fluid style={{overflow: 'hidden'}}>
          <Grid columns={5} divided relaxed>
            <Grid.Column stretched>
              <Grid.Row><Icon name='tree' color={icons.color}/></Grid.Row>
              <Grid.Row><b>{tree.treeId}</b></Grid.Row>
            </Grid.Column>
            <Grid.Column stretched>
              <Grid.Row><Icon name='resize vertical' color={icons.color}/></Grid.Row>
              <Grid.Row><b>{tree.height}</b></Grid.Row>
            </Grid.Column>
            <Grid.Column stretched>
              <Grid.Row><Icon name='resize horizontal' color={icons.color}/></Grid.Row>
              <Grid.Row><b>{tree.diameter}</b></Grid.Row>
            </Grid.Column>
            <Grid.Column stretched>
              <Grid.Row><Icon name='wpforms' color={icons.color}/></Grid.Row>
              <Grid.Row><b>{tree.reports.length}</b></Grid.Row>
            </Grid.Column>
            <Grid.Column stretched>
              <Grid.Row><Icon name={icons.land} color={icons.color}/></Grid.Row>
              <Grid.Row><Icon name={icons.ownership} color={icons.color}/></Grid.Row>
            </Grid.Column>
          </Grid>
        </Container>
      </InfoWindow>
    );

    return Object.keys(this.state.center).length !== 0 ? (
      <GoogleMap
        ref='gmap'
        defaultZoom={14}
        defaultCenter={this.state.center}
        options={{scrollwheel: true}}
        onRightClick={(e) => this.onMapClick(e, false)}
      >
        {this.state.municipalities.map((municipality) => (
          <Polygon
            key={municipality.name}
            paths={municipality.borders}
            onClick={() => this.onMunicipalityClick(municipality)}
            onDblClick={() => this.onMunicipalityDblClick(municipality, false)}
            onRightClick={(e) => this.onMapClick(e, false)}
          />
        ))}
        {this.state.trees.map((tree) => (
          <Marker
            key={tree.treeId}
            icon={{
              url: getTreeMarker(tree.status),
              anchor: new google.maps.Point(23, 45),
              scaledSize: new google.maps.Size(40, 60)
            }}
            position={getLatLng(tree.location)}
            onMouseOver={() => this.onTreeHover(tree)}
            onMouseOut={() => this.onTreeHover(null)}
            onClick={() => this.onTreeClick(tree)}
            onDblClick={() => this.onTreeDblClick(tree, false)}
          >
            {(this.state.treeHover || this.state.treeInfo) && this.state.treeHover === tree ? (
              <TreeInfoWindow tree={tree} icons={getTreeIcons(tree)}/>
            ) : null}
          </Marker>
        ))}
        {this.state.treeModal ? (
          <TreeModal tree={this.state.treeModal} onClose={this.onTreeDblClick}/>
        ) : null}
        {this.state.createTreeModal ? (
          <CreateTreeModal location={this.state.createTreeModal} onClose={this.onMapClick}/>
        ) : null}
        {this.state.municipalityModal ? (
          <MunicipalityModal municipality={this.state.municipalityModal} onClose={this.onMunicipalityDblClick}/>
        ) : null}
      </GoogleMap>
    ) : (
      <Loader size='huge'/>
    );
  }
}

TreeMap.propTypes = {
  refreshMap: PropTypes.bool.isRequired,
  onSustainabilityChange: PropTypes.func.isRequired
};

export default compose(
  withProps({
    googleMapURL: `https://maps.googleapis.com/maps/api/js?key=${gmapsKey}&v=3.exp&libraries=geometry,drawing,places`,
    loadingElement: <div style={{width: '100vw', height: '100vh'}}/>,
    containerElement: <div style={{height: '80vh'}}/>,
    mapElement: <div style={{height: '80vh'}}/>
  }),
  withScriptjs,
  withGoogleMap
)((props) => <TreeMap {...props}/>);
