import React, {PureComponent} from 'react';
import GoogleMapReact from 'google-maps-react';

const AnyReactComponent = ({text}) => <div>{text}</div>;

class TreeMap extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      center: {lat: 45.503265, lng: -73.591593},
      zoom: 11
    };
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.google !== this.props.google) {
      this.loadMap();
    }
  }

  componentDidMount() {
    this.loadMap();
  }

  loadMap() {
    if (this.props && this.props.google) {
      const {google} = this.props;
      const maps = google.maps;

      const mapRef = this.refs.map;
      const node = ReactDOM.findDOMNode(mapRef);

      const center = new maps.LatLng(
        this.state.center.lat,
        this.state.center.lng
      );
      const mapConfig = Object.assign({}, {
        center: center,
        zoom: this.state.zoom
      })
      this.map = new maps.Map(node, mapConfig);
    }
  }

  render() {
    return (
      <div ref='treemap' className='google-map'>
        <GoogleMapReact
          defaultCenter={this.state.center}
          defaultZoom={this.state.zoom}>
          <AnyReactComponent
            lat={45.503265}
            lng={-73.591593}
            text={'TreePLE Map'}
          />
        </GoogleMapReact>
      </div>
    );
  }
}

export default TreeMap;