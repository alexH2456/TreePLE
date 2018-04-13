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

export {
  getSpeciesSelectable,
  getMunicipalitySelectable
};