import React, {PureComponent} from 'react';
import GoogleMapReact from 'google-maps-react';

class TreeMap extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      zoom: 14,
      center: {},
      trees: [],
      municipalities: []
    };
  }
}

export default TreeMap;