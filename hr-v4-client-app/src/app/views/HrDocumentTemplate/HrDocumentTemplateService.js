import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/hr-document-template";

export const pagingHrDocumentTemplate = (dto) => {
    const url = `${API_PATH}/paging`;
    return axios.post(url, dto);
};

export const saveOrUpdate = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, dto);
};

export const getHrDocumentTemplateById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteHrDocumentTemplateById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
