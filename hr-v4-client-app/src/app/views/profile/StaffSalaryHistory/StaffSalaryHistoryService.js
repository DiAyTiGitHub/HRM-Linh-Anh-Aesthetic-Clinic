import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff-salary-history";

export const getAllStaffSalaryHistoryByStaffId = (id) => {
    const url = `${API_PATH}/all-staff-salary-history-by-staff/${id}`;
    return axios.get(url);
};

export const saveOrUpdateStaffSalaryHistory = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, dto);
};

export const getStaffSalaryHistoryById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

export const deleteStaffSalaryHistoryById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};