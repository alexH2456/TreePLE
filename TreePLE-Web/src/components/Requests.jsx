import axios from 'axios';

const backendUrl = 'http://' + serverHost + ':' + serverPort + '/';
const frontendUrl = 'http://' + serverHost + ':8087/';


const AXIOS = axios.create({
  baseURL: backendUrl,
  headers: {
    'Access-Control-Allow-Origin': frontendUrl,
    'Access-Control-Allow-Methods': 'GET, POST, PATCH',
    'Access-Control-Allow-Headers': 'Origin, Content-Type, Authorization'
  },
  timeout: 15000
});


// ==============================
// GET ALL API
// ==============================

function getAllTrees() {
  const url = '/trees/';
  return getRequest(url);
}

function getAllUsers() {
  const url = '/users/';
  return getRequest(url);
}

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

function getAllForecasts() {
  const url = '/forecasts/';
  return getRequest(url);
}

// ==============================
// GET API
// ==============================

function login(params) {
  const url = '/login/';
  return postRequestWithParams(url, params);
}

function getTree(treeId) {
  const url = `/trees/${treeId}/`;
  return getRequest(url);
}

function getUser(username) {
  const url = `/users/${username}/`;
  return getRequest(url);
}

function getUserTrees(username) {
  const url = `/users/${username}/trees/`;
  return getRequest(url);
}

function getUserForecasts(username) {
  const url = `/users/${username}/forecasts/`;
  return getRequest(url);
}

function getSustainability(params) {
  const url = '/sustainability/';
  return postRequestWithParams(url, params);
}

function getTreePLESustainability() {
  const url = '/sustainability/treeple/';
  return getRequest(url);
}

function getTreeSustainability(treeId) {
  const url = `/trees/${treeId}/sustainability/`;
  return getRequest(url);
}

function getMunicipalitySustainability(municipality) {
  const url = `/municipalities/${municipality}/sustainability/`;
  return getRequest(url);
}


// ==============================
// POST API
// ==============================

function createTree(params) {
  const url = '/tree/new/';
  return postRequestWithParams(url, params);
}

function createUser(params) {
  const url = '/user/new/';
  return postRequestWithParams(url, params);
}

function createForecast(params) {
  const url = '/forecast/new/';
  return postRequestWithParams(url, params);
}

function deleteForecast(params) {
  const url = '/forecast/delete/';
  return postRequestWithParams(url, params);
}


// ==============================
// PATCH API
// ==============================

function updateTree(params) {
  const url = '/tree/update/';
  return patchRequestWithParams(url, params);
}


// ==============================
// REQUEST API
// ==============================

function getRequest(url) {
  return AXIOS.get(url);
}

function postRequest(url) {
  return AXIOS.post(url);
}

function postRequestWithParams(url, params) {
  return AXIOS.post(url, params);
}

function patchRequestWithParams(url, params) {
  return AXIOS.patch(url, params);
}

export {
  getAllTrees, getTree, createTree, updateTree,
  getAllUsers, getUser, getUserTrees, getUserForecasts, createUser,
  getAllSpecies,
  getAllLocations,
  getAllMunicipalities,
  getAllForecasts, createForecast, deleteForecast,
  getSustainability, getTreePLESustainability, getTreeSustainability, getMunicipalitySustainability,
  login
};
