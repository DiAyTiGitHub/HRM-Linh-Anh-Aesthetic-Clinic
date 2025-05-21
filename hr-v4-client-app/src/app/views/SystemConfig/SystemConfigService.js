import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/system-config";

export const pagingSystemConfig = (dto) => {
    const url = `${API_PATH}/paging-system-config`;
    return axios.post(url, dto);
};

export const saveSystemConfig = (dto) => {
    const url = `${API_PATH}/save-or-update`;
    return axios.post(url, dto);
};

export const getById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};

export const deleteMultiple = (ids) => {
    const url = `${API_PATH}/delete-multiple`;
    return axios.post(url, ids);
};

export const getByKey = (keyCode) => {
    const url = `${API_PATH}/getByKey/${keyCode}`;
    return axios.get(url);
};
