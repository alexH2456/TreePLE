export const gmapsKey = 'AIzaSyDeo4TnWCcvE-yZlpmsv9FAEyYogAzzcBk';

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