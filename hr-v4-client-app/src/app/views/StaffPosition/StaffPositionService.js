import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/position-staff";

export const pagingStaffPosition = (searchObject) => {
    var url = API_PATH + "/paging";
    return axios.post(url, searchObject);
};

export const getStaffPositionById = (id) => {
    let url = API_PATH + "/" + id;
    return axios.get(url);
};

export const saveStaffPosition = (obj) => {
    let url = API_PATH + "/save";
    return axios.post(url, obj);
};

export const deleteStaffPositionById = (id) => {
    let url = API_PATH + "/" + id;
    return axios.delete(url);
};