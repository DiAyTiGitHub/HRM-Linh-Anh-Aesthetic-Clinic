import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/leave-type";

export const pagingLeaveType = (dto) => {
    const url = `${API_PATH}/page`;
    return axios.post(url, dto);
};

export const getLeaveTypeById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const findOneByCode = (code) => {
    const url = `${API_PATH}/find-by-code/${code}`;
    return axios.get(url);
};

export const saveLeaveType = (dto) => {
    const url = `${API_PATH}/save-or-update`;
    return axios.post(url, dto);
};
export const deleteLeaveTypeById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
export const deleteMultiple = (dto) => {
    const url = `${API_PATH}/deleteMultiple`;
    return axios.post(url, dto);
};

export const getListLeaveType = () => {
    const url = `${API_PATH}/get-list`;
    return axios.get(url);
};