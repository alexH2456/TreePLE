import React from 'react';
import {Flag} from 'semantic-ui-react';
import BlackTree from './images/blackTree.svg';
import BlueTree from './images/blueTree.svg';
import GreenTree from './images/greenTree.svg';
import YellowTree from './images/yellowTree.svg';
import OrangeTree from './images/orangeTree.svg';
import RedTree from './images/redTree.svg';

export const blackTree = BlackTree;
export const blueTree = BlueTree;
export const greenTree = GreenTree;
export const yellowTree = YellowTree;
export const orangeTree = OrangeTree;
export const redTree = RedTree;

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

export const iconDef = [
  {icon: 'tree', def: 'The ID given to the tree when created.'},
  {icon: 'wpforms', def: 'The number of surveys reported on the tree.'},
  {icon: 'resize vertical', def: 'The height of the tree (in cm).'},
  {icon: 'resize horizontal', def: 'The diameter of the tree (in cm).'},
  {icon: 'paw', def: 'The tree is planted on a park land.'},
  {icon: 'home', def: 'The tree is planted on a residential land.'},
  {icon: 'building', def: 'The tree is planted on an institutional land.'},
  {icon: 'map', def: 'The tree is planted on a municipal land.'},
  {icon: 'users', def: 'The tree is owned by the public.'},
  {icon: 'privacy', def: 'The tree is owned by a private user.'},
  {icon: 'user', def: 'A normal user living in Montreal just like you!'},
  {icon: 'leaf', def: 'A special user who is recognized as an arborist or a scientist.'},
];

export const colorDef = [
  {color: 'green', def: 'The tree is planted and in good health.'},
  {color: 'yellow', def: 'The tree is planted but has a disease.'},
  {color: 'orange', def: 'The tree is marked to be cutdown.'},
  {color: 'red', def: 'The tree has unfortunately been cutdown.'},
]

export const treeHelp = {
  height: 'The height of the tree (in cm).',
  diameter: 'The diameter of the tree (in cm).',
  date: 'The date the tree was or will be planted.',
  species: 'The species of the tree.',
  status: 'The health the tree is currently in.',
  municipality: 'The municipality the tree is planted in.',
  ownership: 'Who is the tree owned by?',
  land: 'The type of land the tree is planted on.',
  latitude: 'The latitude geospatial component of the tree\'s location.',
  longitude: 'The longitude geospatial component of the tree\'s location.'
};
