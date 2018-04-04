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
    const url = '/newuser/';
    console.log(this.state.username);
    console.log(this.state.password);
    let usr=this.state.username;
    let pass=this.state.password;
      AXIOS.post(backendUrl+ url, { usr, pass })
            .then((response) => {
                //if (response.data.result_status == "success") {
                  //  localSession.setItem("role", JSON.stringify(response.data.userRole))
                        //dispatch({ type: AUTHENTICATE_USER });
                        //browserHistory.push("/home");
                    })

    }



export {getAllTrees, getAllTreeLocations,
        getAllUsers,
        getAllSpecies,
        getAllLocations,
        getAllMunicipalities,
        loginUser};
