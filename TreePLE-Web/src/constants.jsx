import React from 'react';
import {Flag} from 'semantic-ui-react';

export const gmapsKey = 'AIzaSyDeo4TnWCcvE-yZlpmsv9FAEyYogAzzcBk';

export const mtlCenter = {
  lat: 45.503265,
  lng: -73.591593
};

export const huDates = {
  weekShort: ['Vas', 'H', 'K', 'Sze', 'Csüt', 'P', 'Szo'],
  weekLong: ['Vasárnap', 'Hétfő', 'Kedd', 'Szerda', 'Csütörtök', 'Péntek', 'Szombaton'],
  months: ['Január', 'Február', 'Március', 'Április', 'Május', 'Junius', 'Julius', 'Augusztus', 'Szeptember', 'Október', 'November', 'December']
};

export const roles = {
  resident: {enum: 'Resident', icon: 'user'},
  scientist: {enum: 'Scientist', icon: 'leaf'}
};

export const lands = {
  park: {enum: 'Park', icon: 'paw'},
  residential: {enum: 'Residential', icon: 'home'},
  institutional: {enum: 'Institutional', icon: 'building'},
  municipal: {enum: 'Municipal', icon: 'map'}
};

export const statuses = {
  planted: {enum: 'Planted', color: 'green'},
  diseased: {enum: 'Diseased', color: 'yellow'},
  markedForCutdown: {enum: 'MarkedForCutdown', color: 'orange'},
  cutdown: {enum: 'Cutdown', color: 'red'}
};

export const ownerships = {
  public: {enum: 'Public', icon: 'users'},
  private: {enum: 'Private', icon: 'privacy'}
};

export const roleSelectable = [
  {key: 'R', text: 'Resident', value: 'Resident'},
  {key: 'S', text: 'Scientist', value: 'Scientist'},
];

export const landSelectable = [
  {key: 'P', text: 'Park', value: 'Park'},
  {key: 'R', text: 'Residential', value: 'Residential'},
  {key: 'I', text: 'Institutional', value: 'Institutional'},
  {key: 'M', text: 'Municipal', value: 'Municipal'}
];

export const statusSelectable = [
  {key: 'P', text: 'Planted', value: 'Planted'},
  {key: 'D', text: 'Diseased', value: 'Diseased'},
  {key: 'M', text: 'Marked For Cutdown', value: 'MarkedForCutdown'},
  {key: 'C', text: 'Cutdown', value: 'Cutdown'}
];

export const ownershipSelectable = [
  {key: 'Pu', text: 'Public', value: 'Public'},
  {key: 'Pr', text: 'Private', value: 'Private'}
];

export const flags = [
  {key: 'en', value: 'en', text: <Flag name='ca'/>},
  {key: 'hu', value: 'hu', text: <Flag name='hu'/>}
];