import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-period";

export const pagingSalaryPeriod = (searchObject) => {
    var url = API_PATH + "/search-by-page";
    return axios.post(url, searchObject);
};

export const getById = (id) => {
    let url = API_PATH + "/get-by-id/" + id;
    return axios.get(url);
};

export const saveSalaryPeriod = (obj) => {
    let url = API_PATH + "/save-or-update";
    return axios.post(url, obj);
};

export const deleteSalaryPeriod = (id) => {
    let url = API_PATH + "/remove/" + id;
    return axios.delete(url);
};

export const deleteMultiple = (ids) => {
    const url = API_PATH + "/remove-multiple";
    return axios.post(url, ids);
};
export const autoGenCode = (configKey) => {
    let url = API_PATH + `/auto-gen-code/${configKey}`;
    return axios.get(url);
};