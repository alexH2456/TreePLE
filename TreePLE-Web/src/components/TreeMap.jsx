import React, {PureComponent} from 'react';
import {Map, InfoWindow, Marker, Polygon, GoogleApiWrapper} from 'google-maps-react';
import {Dimmer, Image, Loader, Segment} from "semantic-ui-react";
import {getAllTrees, getAllMunicipalities} from './Requests';
import Logo from "../images/favicon.ico";

export class TreeMap extends PureComponent {
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

  loadTrees = () => {
    getAllTrees()
      .then(response => {
        this.setState({trees: response.data});
      })
      .catch(error => {
        console.error(error);
      });
  }

  loadMunicipalities = () => {
    getAllMunicipalities()
      .then(response => {
        let municipalities = [];

        response.data.map(municipality => {
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

  onReady = () => {
    this.loadTrees();
    this.loadMunicipalities();
  }

  moveMap = (mapProps, map) => {
    console.log(mapProps);
    console.log(map);
  }

  render() {
    const style = {
      width: '98vw',
      height: '80vh'
    };

    return (this.props.loaded && Object.keys(this.state.center).length !== 0) ? (
      <div style={style}>
        <Map google={this.props.google}
             style={style}
             zoom={this.state.zoom}
             initialCenter={this.state.center}
             onReady={this.onReady}
             onDragend={this.moveMap}>
          {this.state.municipalities.map(municipality => {
            return <Polygon key={municipality.name}
                            paths={municipality.borders}
                            strokeColor="#0000FF"
                            strokeOpacity={0.8}
                            strokeWeight={2}
                            fillColor="#0000FF"
                            fillOpacity={0.3}/>;
          })}
          {this.state.trees.map(tree => {
            return <Marker key={tree.treeId} name={tree.treeId}
                           position={{lat: tree.location.latitude, lng: tree.location.longitude}}/>;
          })}
        </Map>
      </div>
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

export default GoogleApiWrapper({
  apiKey: 'AIzaSyAyesbQMyKVVbBgKVi2g6VX7mop2z96jBo',
  version: '3.31'
})(TreeMap);