import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/bank";

export const pagingBank = (dto) => {
    const url = `${API_PATH}/page`;
    return axios.post(url, dto);
};

export const saveOrUpdateBank = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, dto);
};

export const getBankById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteBankById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
export const deleteMultipleBank = (ids) => {
    const url = `${API_PATH}/deleteMultiple`;
    return axios.post(url, ids);
};