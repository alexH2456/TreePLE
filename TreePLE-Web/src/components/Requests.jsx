import React, {PureComponent} from 'react';
import {Link} from 'react-router-dom';
import axios from 'axios';

// const backendUrl = '';
// const frontendUrl = '';
const backendUrl = 'http://localhost:8088/';
const frontendUrl = 'http://localhost:8087/';

const AXIOS = axios.create({
  baseURL: backendUrl,
  headers: {'Access-Control-Allow-Origin': frontendUrl},
  timeout: 5000
});

// ==============================
// GET API
// ==============================

function getAllTrees() {
  const url = '/trees/';
  return getRequest(url);
};

function getAllTreeLocations() {
  const url = '/trees/?query=locations';
  return getRequest(url);
}

function getAllUsers() {
  const url = '/users/';
  return getRequest(url);
};

function getAllSpecies() {
  const url = '/species/';
  return getRequest(url);
}

function getAllLocations() {
  const url = '/locations/';
  return getRequest(url);
}

function getAllMunicipalities() {
  const url = '/municipalities/';
  return getRequest(url);
}


// ==============================
// REQUEST API
// ==============================

function getRequest(url) {
  return AXIOS.get(url);
};

export {getAllTrees, getAllTreeLocations,
        getAllUsers,
        getAllSpecies,
        getAllLocations,
        getAllMunicipalities};