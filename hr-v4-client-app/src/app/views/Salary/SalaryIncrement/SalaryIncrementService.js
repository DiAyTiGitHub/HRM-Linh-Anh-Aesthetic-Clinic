import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salaryincrementtype";

export const pagingSalaryIncrement = (searchObject) => {
    var url = API_PATH + "/searchByPage";
    return axios.post(url, searchObject);
};

export const getSalaryIncrement = (id) => {
    let url = API_PATH + "/" + id;
    return axios.get(url);
};

export const saveSalaryIncrement = (obj) => {
    let url = API_PATH;
    return axios.post(url, obj);
};

export const deleteSalaryIncrement = (id) => {
    let url = API_PATH + "/" + id;
    return axios.delete(url);
};

export const checkCode = (id, code) => {
    const config = { params: { id: id, code: code } };
    let url = API_PATH + "/checkCode";
    return axios.get(url, config);
};
