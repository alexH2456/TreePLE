import {lands, statuses, ownerships, blackTree, blueTree, greenTree, yellowTree, orangeTree, redTree} from '../constants';

function getSelectable(data) {
  return data.map((item, idx) => ({
    key: idx,
    text: item.name,
    value: item.name
  }));
};

function getLatLng(location) {
  return {
    id: location.locationId,
    lat: location.latitude,
    lng: location.longitude
  };
};

function getLatLngBorders(borders) {
  return borders.map((location) => getLatLng(location));
};

function getMapBounds(locations) {
  let lat = [];
  let lng = [];

  locations.forEach((location) => {
    lat.push(location.lat);
    lng.push(location.lng);
  });

  const bounds = {
    south: Math.min(...lat),
    north: Math.max(...lat),
    west: Math.min(...lng),
    east: Math.max(...lng)
  };

  return bounds;
};

function getTreeIcons(tree) {
  let icons = {};

  switch (tree.land) {
    case lands.park.enum:
      icons.land = lands.park.icon;
      break;
    case lands.residential.enum:
      icons.land = lands.residential.icon;
      break;
    case lands.institutional.enum:
      icons.land = lands.institutional.icon;
      break;
    case lands.municipal.enum:
      icons.land = lands.municipal.icon;
      break;
    default:
      icons.land = 'question';
  };

  switch (tree.ownership) {
    case ownerships.public.enum:
      icons.ownership = ownerships.public.icon;
      break;
    case ownerships.private.enum:
      icons.ownership = ownerships.private.icon;
      break;
    default:
      icons.ownership = 'question';
  };

  switch (tree.status) {
    case statuses.planted.enum:
      icons.color = statuses.planted.color;
      break;
    case statuses.diseased.enum:
      icons.color = statuses.diseased.color;
      break;
    case statuses.markedForCutdown.enum:
      icons.color = statuses.markedForCutdown.color;
      break;
    case statuses.cutdown.enum:
      icons.color = statuses.cutdown.color;
      break;
    default:
      icons.color = 'black';
  };

  return icons;
};

function getTreeMarker(status) {
  switch (status) {
    case statuses.planted.enum:
      return greenTree;
    case statuses.diseased.enum:
      return yellowTree;
    case statuses.markedForCutdown.enum:
      return orangeTree;
    case statuses.cutdown.enum:
      return redTree;
    case 'selected':
      return blueTree;
    default:
      return blackTree;
  }
};

function getError(error) {
  error = error.toLowerCase();

  const errorList = {
    height: error.includes('height'),
    diameter: error.includes('diameter'),
    address: error.includes('address'),
    date: error.includes('date'),
    land: error.includes('land'),
    status: error.includes('status'),
    ownership: error.includes('ownership'),
    species: error.includes('species'),
    municipality: error.includes('municipality'),
    report: error.includes('report'),
    username: error.includes('username'),
    password: error.includes('password'),
    role: error.includes('role'),
    location: error.includes('location'),
    borders: error.includes('borders'),
    key: error.includes('key')
  };

  return errorList;
};

function getTreeAge(tree) {
  return 6 * tree.diameter / 2.54;
};

function formatDate(date) {
  return date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate();
};

export {
  getSelectable,
  getLatLng,
  getLatLngBorders,
  getMapBounds,
  getTreeIcons,
  getTreeMarker,
  getError,
  getTreeAge,
  formatDate
};
