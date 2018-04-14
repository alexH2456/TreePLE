import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Dimmer, Image, Loader, Segment} from 'semantic-ui-react';
import {GoogleMap, Marker, Polygon, withScriptjs, withGoogleMap} from 'react-google-maps';
import Logo from '../images/favicon.ico';
import {gmapsKey} from '../constants';
import TreeModal from './TreeModal';
import MunicipalityModal from './MunicipalityModal';
import {getAllTrees, getAllMunicipalities, getMunicipalitySustainability} from './Requests';
import {getLatLngBorders, getMapBounds} from './Utils';

export class TreeMap extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      zoom: 14,
      center: {},
      tree: null,
      municipality: null,
      trees: [],
      municipalities: [],
      treeModal: false,
      municipalityModal: false
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
          timeout: 7500,
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
        console.error(error);
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
        console.error(error);
      });
  }

  onMoveMap = (mapProps, map) => {
    console.log(mapProps);
    console.log(map);
  }

  onMunicipalityClick = (e, municipality) => {

    getMunicipalitySustainability(municipality.name)
      .then(({data}) => {
        const sustainability = {
          stormwater: data.stormwater,
          co2Reduced: data.co2Reduced,
          biodiversity: data.biodiversity,
          energyConserved: data.energyConserved
        };
        return sustainability;
      })
      .then(sustainability => {
        let viewport = getMapBounds(municipality.borders);
        this.refs.map.fitBounds(viewport);
        this.props.onSustainabilityChange(sustainability);
      })
      .catch(error => {
        console.error(error);
      });
  }

  onMunicipalityRightClick = (e, municipality) => {
    this.setState({
      municipality: municipality,
      municipalityModal: !this.state.municipalityModal
    });
  }

  onTreeRightClick = (e, tree) => {
    this.setState({
      tree: tree,
      treeModal: !this.state.treeModal
    });
  }

  render() {
    const style = {
      width: '98vw',
      height: '80vh'
    };

    return (Object.keys(this.state.center).length !== 0) ? (
      <GoogleMap
        ref='map'
        zoom={this.state.zoom}
        center={this.state.center}
        options={{scrollwheel: true}}
      >
        {this.state.municipalities.map(municipality => {
          return <Polygon key={municipality.name}
                          paths={municipality.borders}
                          onClick={e => this.onMunicipalityClick(e, municipality)}
                          onRightClick={e => this.onMunicipalityRightClick(e, municipality)}/>;
        })}
        {this.state.trees.map(tree => {
          return <Marker key={tree.treeId}
                         position={{
                           lat: tree.location.latitude,
                           lng: tree.location.longitude
                         }}
                         onRightClick={e => this.onTreeRightClick(e, tree)} />;
        })}
        {this.state.treeModal ? (
          <TreeModal tree={this.state.tree} onClose={this.onTreeRightClick}/>
        ) : null}
        {this.state.municipalityModal ? (
          <MunicipalityModal municipality={this.state.municipality} onClose={this.onMunicipalityRightClick}/>
        ) : null}
      </GoogleMap>
    ) : (
      <Segment style={{...style, display: 'table-cell', verticalAlign: 'middle'}}>
        <Dimmer active>
          <Loader size='huge'/>
        </Dimmer>
        <Image centered size='medium' src={Logo}/>
      </Segment>
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