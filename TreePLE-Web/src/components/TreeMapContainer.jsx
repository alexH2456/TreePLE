import React, {PureComponent} from 'react';
import GoogleMapReact, {Map, InfoWindow, Marker, GoogleApiWrapper} from 'google-maps-react';
import {getAllTrees} from './Requests';
// import TreeMap from './TreeMap';

export class TreeMapContainer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      zoom: 14,
      center: {},
      trees: []
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
          timeout: 5000,
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

  moveMap = (mapProps, map) => {
    console.log(mapProps);
    console.log(map);
  }

  render() {
    const style = {
      width: '100vw',
      height: '100vh'
    };

    var smt = this.state.trees;
    // <TreeMap google={this.props.google}/>

    return (this.props.loaded && Object.keys(this.state.center).length !== 0) ? (
      <div style={style}>
        <Map google={this.props.google}
             style={style}
             zoom={this.state.zoom}
             initialCenter={this.state.center}
             onReady={this.loadTrees}
             onDragend={this.moveMap}>
          {this.state.trees.map(tree => {
            return <Marker key={tree.treeId} name={tree.treeId}
                           position={{lat: tree.location.latitude, lng: tree.location.longitude}}/>;
          })}
        </Map>
      </div>
    ) : (
      <div>Loading Map...</div>
    );
  }
}

export default GoogleApiWrapper({
  apiKey: 'AIzaSyAyesbQMyKVVbBgKVi2g6VX7mop2z96jBo',
  version: '3.31'
})(TreeMapContainer);