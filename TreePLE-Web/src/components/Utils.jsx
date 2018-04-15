import {getAllSpecies, getAllMunicipalities} from "./Requests";
import {roles, lands, statuses, ownerships} from '../constants';

function getSpeciesSelectable() {
  let speciesSelectable = [];

  getAllSpecies()
    .then(({data}) => {
      data.forEach((species, idx) => {
        speciesSelectable.push({
          key: idx,
          text: species.name,
          value: species.name
        });
      });
    })
    .catch(error => {
      console.log(error);
    });

  return speciesSelectable;
}

function getMunicipalitySelectable() {
  let municipalitySelectable = [];

  getAllMunicipalities()
    .then(({data}) => {
      data.forEach((municipality, idx) => {
        municipalitySelectable.push({
          key: idx,
          text: municipality.name,
          value: municipality.name
        });
      });
    })
    .catch(error => {
      console.log(error);
    });

  return municipalitySelectable;
}

function getLatLngBorders(borders) {
  let latLngBorders = [];

  borders.map(location => {
    latLngBorders.push({
      id: location.locationId,
      lat: location.latitude,
      lng: location.longitude
    });
  });

  return latLngBorders;
}

function getMapBounds(locations) {
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
  }

  switch (tree.ownership) {
    case ownerships.public.enum:
      icons.ownership = ownerships.public.icon;
      break;
    case ownerships.private.enum:
      icons.ownership = ownerships.private.icon;
      break;
    default:
      icons.ownership = 'question';
  }

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
  }

  return icons;
}

function formatDate(date) {
  return date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate();
}

export {
  getSpeciesSelectable,
  getMunicipalitySelectable,
  getLatLngBorders,
  getMapBounds,
  getTreeIcons,
  formatDate
};