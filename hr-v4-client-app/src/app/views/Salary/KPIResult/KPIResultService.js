import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/kpi-result";

export const pagingKPIResult = (dto) => {
    const url = `${API_PATH}/page`;
    return axios.post(url, dto);
};

export const saveOrUpdate = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, dto);
};

export const getKPIResultById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteKPIResultById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
