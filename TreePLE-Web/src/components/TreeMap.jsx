import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from "recompose";
import {Dimmer, Image, Loader, Segment} from "semantic-ui-react";
import {GoogleMap, Marker, Polygon, withScriptjs, withGoogleMap} from 'react-google-maps';
const { DrawingManager } = require("react-google-maps/lib/components/drawing/DrawingManager");
import {getAllTrees, getAllMunicipalities, getMunicipalitySustainability} from './Requests';
import Logo from "../images/favicon.ico";

export class TreeMapp extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      zoom: 14,
      center: {},
      trees: [],
      municipalities: []
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

  getMapBounds = (locations) => {
    let lat = [];
    let lng = [];
    locations.map(location => {
      lat.push(location.lat);
      lng.push(location.lng);
    });

    const bounds = {
      south: Math.min(...lat),
      north: Math.max(...lat),
      west: Math.min(...lng),
      east: Math.max(...lng)
    }

    return bounds;
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
          let borders = [];
          municipality.borders.map(location => {
            borders.push({
              lat: location.latitude,
              lng: location.longitude
            });
          });
          borders.push(borders[0]);

          municipalities.push({
            name: municipality.name,
            borders: borders
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
    let viewport = this.getMapBounds(municipality.borders);
    let sustainability;

    new Promise(() => {
      getMunicipalitySustainability(municipality.name)
        .then(({data}) => {
          sustainability = {
            stormwater: data.stormwater,
            co2Reduced: data.co2Reduced,
            biodiversity: data.biodiversity,
            energyConserved: data.energyConserved
          };
        })
        .catch(error => {
          console.error(error);
        });
    })
    .then(() => {
      this.refs.map.fitBounds(viewport);
      this.props.onSustainabilityChange(sustainability);
    })
  }

  onTreeClick = (e, tree) => {
    console.log(tree);
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
                          onClick={e => this.onMunicipalityClick(e, municipality)}/>;
        })}
        {this.state.trees.map(tree => {
          return <Marker key={tree.treeId}
                         position={{
                           lat: tree.location.latitude,
                           lng: tree.location.longitude
                         }}
                         onClick={e => this.onTreeClick(e, tree)} />;
        })}
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
    googleMapURL: "https://maps.googleapis.com/maps/api/js?key=AIzaSyAyesbQMyKVVbBgKVi2g6VX7mop2z96jBo&v=3.exp&libraries=geometry,drawing,places",
    loadingElement: <div style={{width: '100vw', height: '100vh'}}/>,
    containerElement: <div style={{height: '80vh'}}/>,
    mapElement: <div style={{height: '80vh'}}/>,
  }),
  withScriptjs,
  withGoogleMap
)(props => <TreeMapp {...props}/>)