import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/relationship";

export const getAllStaffFamilyRelationshipByStaffId = (id) => {
    const url = `${API_PATH}/getall/${id}`;
    return axios.get(url);
};

export const saveStaffFamilyRelationship = (dto) => {
    const url = `${API_PATH}/create`;
    return axios.post(url, dto);
};

export const updateStaffFamilyRelationship = (dto, id) => {
    const url = `${API_PATH}/update/${id}`;
    return axios.put(url, dto);
};

export const getStaffFamilyRelationshipByStaffId = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteStaffFamilyRelationshipByStaffId = (id) => {
    const url = `${API_PATH}/delete/${id}`;
    return axios.delete(url);
};
export const deleteMultipleStaffFamilyRelationship = (ids) => {
    const url = `${API_PATH}/deleteLists`;
    return axios.post(url, ids);
};