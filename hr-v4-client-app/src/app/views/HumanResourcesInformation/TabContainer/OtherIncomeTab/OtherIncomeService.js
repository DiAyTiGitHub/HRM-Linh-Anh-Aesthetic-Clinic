import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/other-income";


export const saveOrUpdateOtherIncome = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url , dto);
};

export const pagingOtherIncome = (dto) => {
    const url = `${API_PATH}/paging`;
    return axios.post(url , dto);
};


export const getOtherIncomeById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteOtherIncomeById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
export const deleteMultipleOtherIncome = (ids) => {
    const url = `${API_PATH}/deleteLists`;
    return axios.post(url , ids);
};