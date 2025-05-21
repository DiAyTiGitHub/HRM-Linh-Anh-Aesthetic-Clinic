import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff-annual-leave-history";

export const pagingStaffAnnualLeaveHistory = (searchObject) => {
    const url = API_PATH + "/search-by-page";
    return axios.post(url , searchObject);
};

export const getById = (id) => {
    const url = API_PATH + "/" + id;
    return axios.get(url);
};

export const saveStaffAnnualLeaveHistory = (obj) => {
    const url = API_PATH + "/save-or-update";
    return axios.post(url , obj);
};

export const deleteMultiple = (ids) => {
    const url = API_PATH + "/delete-multiple";
    return axios.post(url , ids);
};

export const deleteById = (id) => {
    const url = API_PATH + "/" + id;
    return axios.delete(url);
};

export const getInitialAnnualLeaveHistoryFilter = (id) => {
    const url = `${API_PATH}/initial-filter`;
    return axios.get(url);
};