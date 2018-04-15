import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Grid, Icon, Loader, Container} from 'semantic-ui-react';
import {GoogleMap, InfoWindow, Marker, Polygon, withScriptjs, withGoogleMap} from 'react-google-maps';
import TreeModal from './TreeModal';
import CreateTreeModal from './CreateTreeModal';
import MunicipalityModal from './MunicipalityModal';
import {getAllTrees, getAllMunicipalities, getMunicipalitySustainability, getTreeSustainability} from './Requests';
import {getLatLngBorders, getMapBounds, getTreeIcons} from './Utils';
import {gmapsKey} from '../constants';
import Logo from '../images/favicon.ico';

export class TreeMap extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      zoom: 14,
      center: {},
      trees: [],
      municipalities: [],
      treeModal: null,
      createTreeModal: null,
      treeHover: null,
      treeInfo: false,
      municipalityModal: null
    };
  }

  componentWillMount() {
    this.getUserLocation();
    this.loadMap();
  }

  getUserLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        position => {
          this.setState({
            center: {
              lat: position.coords.latitude,
              lng: position.coords.longitude
            }
          });
        },
        error => {
          this.setState({
            center: {
              lat: 45.503265,
              lng: -73.591593
            }
          });
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
      .catch(error => {
        console.log(error);
      });
  }

  loadMunicipalities = () => {
    getAllMunicipalities()
      .then(({data}) => {
        let municipalities = [];

        data.map(municipality => {
          municipalities.push({
            name: municipality.name,
            totalTrees: municipality.totalTrees,
            borders: getLatLngBorders(municipality.borders)
          });
        });

        this.setState({municipalities: municipalities});
      })
      .catch(error => {
        console.log(error);
      });
  }

  onMapClick = (e, success) => {
    if (success) {
      this.loadTrees()
    }
    this.setState({createTreeModal: !!e ? e.latLng : null});
  }

  onMunicipalityClick = (e, municipality) => {
    getMunicipalitySustainability(municipality.name)
      .then(({data}) => {
        return {
          stormwater: data.stormwater,
          co2Reduced: data.co2Reduced,
          biodiversity: data.biodiversity,
          energyConserved: data.energyConserved
        };
      })
      .then(sustainability => {
        let viewport = getMapBounds(municipality.borders);
        this.refs.map.fitBounds(viewport);
        this.props.onSustainabilityChange(sustainability);
      })
      .catch(error => {
        console.log(error);
      });
  }

  onMunicipalityDblClick = (e, municipality) => this.setState({municipalityModal: municipality});

  onTreeHover = (e, tree) => {
    if (!this.state.treeInfo) {
      this.setState({treeHover: tree});
    }
  }

  onTreeClick = (e, tree) => {
    this.setState(prevState => {
      return {
        treeHover: tree,
        treeInfo: tree == prevState.treeHover ? !prevState.treeInfo : tree !== null ? prevState.treeInfo : false
      }
    });

    if (tree !== null) {
      getTreeSustainability(tree.treeId)
        .then(({data}) => {
          return {
            stormwater: data.stormwater,
            co2Reduced: data.co2Reduced,
            biodiversity: data.biodiversity,
            energyConserved: data.energyConserved
          };
        })
        .then(sustainability => {
          this.props.onSustainabilityChange(sustainability);
        })
        .catch(error => {
          console.log(error);
        })
    }
  }

  onTreeDblClick = (e, tree) => this.setState({treeModal: tree});

  render() {
    return (Object.keys(this.state.center).length !== 0) ? (<div>
      <GoogleMap
        ref='map'
        zoom={this.state.zoom}
        center={this.state.center}
        options={{scrollwheel: true}}
        onRightClick={e => this.onMapClick(e, false)}
      >
        {this.state.municipalities.map(municipality => {
          return (
            <Polygon
              key={municipality.name}
              paths={municipality.borders}
              onClick={e => this.onMunicipalityClick(e, municipality)}
              onDblClick={e => this.onMunicipalityDblClick(e, municipality)}
              onRightClick={e => this.onMapClick(e, false)}
            />
          );
        })}
        {this.state.trees.map(tree => {
          let icons = getTreeIcons(tree);

          return (
            <Marker
              key={tree.treeId}
              position={{lat: tree.location.latitude, lng: tree.location.longitude}}
              onMouseOver={e => this.onTreeHover(e, tree)}
              onMouseOut={e => this.onTreeHover(e, null)}
              onClick={e => this.onTreeClick(e, tree)}
              onDblClick={e => this.onTreeDblClick(e, tree)}
            >
              {((!!this.state.treeHover || this.state.treeInfo) && this.state.treeHover == tree) ? (
                <InfoWindow  onCloseClick={e => this.onTreeClick(e, null)}>
                  <Container fluid style={{overflow: 'hidden'}}>
                    <Grid columns={4} divided relaxed>
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
                        <Grid.Row><Icon name={icons.land} color={icons.color}/></Grid.Row>
                        <Grid.Row><Icon name={icons.ownership} color={icons.color}/></Grid.Row>
                      </Grid.Column>
                    </Grid>
                  </Container>
                </InfoWindow>
              ) : null}
            </Marker>
          );
        })}
        {!!this.state.treeModal ? (
          <TreeModal tree={this.state.treeModal} onClose={this.onTreeDblClick}/>
        ) : null}
        {!!this.state.createTreeModal ? (
          <CreateTreeModal location={this.state.createTreeModal} onClose={this.onMapClick}/>
        ) : null}
        {!!this.state.municipalityModal ? (
          <MunicipalityModal municipality={this.state.municipalityModal} onClose={this.onMunicipalityDblClick}/>
        ) : null}
      </GoogleMap></div>
    ) : (
      <Loader size='huge'/>
    );
  }
}

export default compose(
  withProps({
    googleMapURL: `https://maps.googleapis.com/maps/api/js?key=${gmapsKey}&v=3.exp&libraries=geometry,drawing,places`,
    loadingElement: <div style={{width: '100vw', height: '100vh'}}/>,
    containerElement: <div style={{height: '80vh'}}/>,
    mapElement: <div style={{height: '80vh'}}/>,
  }),
  withScriptjs,
  withGoogleMap
)(props => <TreeMap {...props}/>)