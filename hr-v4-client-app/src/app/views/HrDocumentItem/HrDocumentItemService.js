import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/hr-document-item";

export const pagingHrDocumentItem = (dto) => {
    const url = `${API_PATH}/paging`;
    return axios.post(url, dto);
};

export const saveOrUpdateHrDocumentItem = (dto) => {
    const url = `${API_PATH}/save-or-update`;
    return axios.post(url, dto);
};

export const getHrDocumentItemById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteHrDocumentItemById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};

export const deleteMultipleHrDocumentItems = (ids) => {
    const url = `${API_PATH}/delete-multiple`;
    return axios.post(url, ids);
};
