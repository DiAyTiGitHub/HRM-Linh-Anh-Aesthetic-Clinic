import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/education";

export const pagingEducationHistory = (dto) => {
    const url = `${API_PATH}/paging`;
    return axios.post(url, dto);
};

export const saveOrUpdateEducationHistory = (dto) => {
    const url = `${API_PATH}/create`;
    return axios.post(url, dto);
};

export const getEducationHistoryById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteEducationHistoryById = (id) => {
    const url = `${API_PATH}/delete/${id}`;
    return axios.delete(url);
};
