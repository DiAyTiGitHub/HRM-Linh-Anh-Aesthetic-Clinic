import ConstantList from "../../appConfig";
import axios from "axios";

const API_PATH = ConstantList.API_ENPOINT + "/public/candidate";
export const pagingPublicAdministratives = (searchObject) => {
    var url = API_PATH + "/hrAdministrativeUnit/searchByPage";
    return axios.post(url, searchObject);
};
export const pagingPublicCountry = (searchObject) => {
    var url = API_PATH + "/hrCountry/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPublicReligions = (searchObject) => {
    var url = API_PATH + "/hrRelegion/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPublicEthnicities = (searchObject) => {
    var url = API_PATH + "/hrEthnics/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPublicTrainingBases = (searchObject) => {
    var url = API_PATH + "/educationalInstitution/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPublicSpecialities = (searchObject) => {
    var url = API_PATH + "/hrSpeciality/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPublicEducationTypes = (searchObject) => {
    var url = API_PATH + "/hrEducationType/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPublicEducationDegrees = (searchObject) => {
    var url = API_PATH + "/educationDegree/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPublicCertificate = (searchObject) => {
    var url = API_PATH + "/certificate/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPublicPosition = (searchObject) => {
    const url = API_PATH + "/position/searchByPage";
    return axios.post(url, searchObject);
};
