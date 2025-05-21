import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/kpi";

export const pagingKPI = (dto) => {
    const url = `${API_PATH}/page`;
    return axios.post(url, dto);
};

export const saveOrUpdate = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, dto);
};

export const getKPIById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteKPIById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
