import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/person-certificate";

export const getAllPersonCertificateByPerson = (id) => {
    const url = `${API_PATH}/get-all-by-person/${id}`;
    return axios.get(url);
};

export const saveOrUpdatePersonCertificate = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, dto);
};

export const pagingPersonCertificate = (dto) => {
    const url = `${API_PATH}/search-by-page`;
    return axios.post(url, dto);
};

export const getInitialPersonCertificateFilter = (id) => {
    const url = `${API_PATH}/initial-filter`;
    return axios.get(url);
};

export const getPersonCertificateById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deletePersonCertificateById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
export const deleteMultiplePersonCertificate = (ids) => {
    const url = `${API_PATH}/deleteMultiple`;
    return axios.post(url, ids);
};