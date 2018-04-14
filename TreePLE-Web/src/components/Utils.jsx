import {getAllSpecies, getAllMunicipalities} from "./Requests";

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
      console.error(error);
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
      console.error(error);
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

export {
  getSpeciesSelectable,
  getMunicipalitySelectable,
  getLatLngBorders,
  getMapBounds
};