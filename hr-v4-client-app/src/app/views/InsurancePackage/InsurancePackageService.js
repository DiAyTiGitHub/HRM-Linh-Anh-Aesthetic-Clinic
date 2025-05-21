import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/insurance-package";

export const pagingInsurancePackage = (dto) => {
    const url = `${API_PATH}/search-by-page`;
    return axios.post(url, dto);
};

export const saveOrUpdate = (dto) => {
    const url = `${API_PATH}/save-or-update`;
    return axios.post(url, dto);
};

export const getInsurancePackageById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteInsurancePackageById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
