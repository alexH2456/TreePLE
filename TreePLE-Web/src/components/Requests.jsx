import React, {PureComponent} from 'react';
import {Link} from 'react-router-dom';
import axios from 'axios';

const backendUrl = 'http://localhost:8088/';
const frontendUrl = 'http://localhost:8087/';
// const backendUrl = '';
// const frontendUrl = '';

const AXIOS = axios.create({
  baseURL: backendUrl,
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PATCH, DELETE',
    'Access-Control-Allow-Headers': 'X-Requested-With, content-type, Authorization'
  },
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
function loginUser() {
    const url = '/login/';
    let username=this.state.username;
    let password=this.state.password;
      AXIOS.post(backendUrl+ url, { username, password })
            .then((response) => {

                console.log(response);
                if (response.status == 200) {
                      localStorage.setItem("username", JSON.stringify(response.data.username));
                      localStorage.setItem("userRole", JSON.stringify(response.data.role));
                      localStorage.setItem("adresses", JSON.stringify(response.data.myAddresses[0]));

                 }
      })

    }


function registerUser() {
      const url = '/newuser/';
      let username=this.state.username;
      let password=this.state.password;
      let role=this.state.role;
      let myAddresses=this.state.myAddresses;

      AXIOS.post(backendUrl+ url, { username, password, role, myAddresses })
            .then((response) => {
                console.log("got it");
                console.log(response);

           })
    }





export {getAllTrees, getAllTreeLocations,
        getAllUsers,
        getAllSpecies,
        getAllLocations,
        getAllMunicipalities,
        loginUser};
