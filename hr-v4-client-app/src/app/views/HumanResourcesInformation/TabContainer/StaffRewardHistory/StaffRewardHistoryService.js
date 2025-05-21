import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/rewardHistory";

export const getAllStaffRewardHistoryByStaff = (id) => {
    const url = `${API_PATH}/getAll/${id}`;
    return axios.get(url);
};

export const saveStaffRewardHistory = (dto) => {
    const url = `${API_PATH}/create`;
    return axios.post(url, dto);
};

export const updateStaffRewardHistory = (dto, id) => {
    const url = `${API_PATH}/update/${id}`;
    return axios.put(url, dto);
};


export const getStaffRewardHistoryById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteStaffRewardHistoryById = (id) => {
    const url = `${API_PATH}/delete/${id}`;
    return axios.delete(url);
};
export const deleteMultipleStaffRewardHistory = (ids) => {
    const url = `${API_PATH}/deleteLists`;
    return axios.post(url, ids);
};