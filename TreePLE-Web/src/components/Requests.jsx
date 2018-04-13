import React, {PureComponent} from 'react';
import {} from "..";
import axios from 'axios';

// const backendUrl = 'http://localhost:8088/';
// const frontendUrl = 'http://localhost:8087/';
// const backendUrl = 'http://ecse321-11.ece.mcgill.ca:8080/';
// const frontendUrl = 'http://ecse321-11.ece.mcgill.ca:8087/';
const backendUrl = 'http://' + serverHost + ':' + serverPort + '/';
const frontendUrl = 'http://' +  serverHost + ':8087/';


const AXIOS = axios.create({
  baseURL: backendUrl,
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PATCH, DELETE',
    'Access-Control-Allow-Headers': 'X-Requested-With, content-type, Authorization'
  },
  timeout: 10000
});


// ==============================
// GET ALL API
// ==============================

function getAllTrees() {
  const url = `/trees/`;
  return getRequest(url);
};

function getAllUsers() {
  const url = `/users/`;
  return getRequest(url);
};

function getAllSpecies() {
  const url = `/species/`;
  return getRequest(url);
}

function getAllLocations() {
  const url = `/locations/`;
  return getRequest(url);
}

function getAllMunicipalities() {
  const url = `/municipalities/`;
  return getRequest(url);
}

function getAllForecasts() {
  const url = `/forecasts/`;
  return getRequest(url);
}

function getTreePLESustainability() {
  const url = `/sustainability/treeple/`;
  return getRequest(url);
}


// ==============================
// GET API
// ==============================

function login(jsonParams) {
  const url = `/login/`;
  return postRequestWithParams(url, jsonParams);
};

function getTree(treeId) {
  const url = `/trees/${treeId}/`;
  return getRequest(url);
};

function getUser(username) {
  const url = `/users/${username}/`;
  return getRequest(url);
};

function getMunicipalitySustainability(municipality) {
  const url = `/sustainability/${municipality}/`;
  return getRequest(url);
}


// ==============================
// POST API
// ==============================

function createTree(jsonParams) {
  const url = `/newtree/`;
  return postRequest(url);
}

function createUser(jsonParams) {
  const url = `/newuser/`;
  return postRequestWithParams(url, jsonParams);
}


// ==============================
// REQUEST API
// ==============================

function getRequest(url) {
  return AXIOS.get(url);
};

function postRequest(url) {
  return AXIOS.post(url);
};

function postRequestWithParams(url, params) {
  return AXIOS.post(url, params);
};

export {
  getAllTrees, getAllTreeLocations, createTree,
  getAllUsers, getUser, createUser,
  getAllSpecies,
  getAllLocations,
  getAllMunicipalities,
  getAllForecasts,
  getTreePLESustainability, getMunicipalitySustainability,
  login
};
