import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff-salary-item-value";

export const pagingStaffSalaryItemValue = (searchObject) => {
    const url = API_PATH + "/paging";
    return axios.post(url, searchObject);
};

export const getById = (id) => {
    const url = API_PATH + "/" + id;
    return axios.get(url);
};

export const getListByStaffId = (id) => {
    const url = API_PATH + "/staff/" + id;
    return axios.get(url);
};

export const getTaxBHXHByStaffId = (id) => {
    const url = API_PATH + "/tax-bhxh/" + id;
    return axios.get(url);
};

export const updateStaffSalaryItemValue = (obj) => {
    const url = API_PATH + "/update";
    return axios.post(url, obj);
};

export const saveStaffSalaryItemValue = (obj) => {
    const url = API_PATH + "/save";
    return axios.post(url, obj);
};

export const saveStaffSalaryItemValueList = (obj) => {
    const url = API_PATH + "/save-list-staff-salary-item-value";
    return axios.post(url, obj);
};
// export const deleteMultiple = (ids) => {
//   const url = API_PATH + "/deleteMultiple";
//   return axios.post(url, ids);
// };

export const deleteStaffSalaryItemValue = (id) => {
    const url = API_PATH + "/" + id;
    return axios.delete(url);
};

export const getListByStaffAndTemplateItem = (obj) => {
    const url = API_PATH + "/get-by-staff-template-item";
    return axios.post(url, obj);
};

export const getSalaryValueHistories = (id) => {
    const url = API_PATH + "/salary-value-histories/" + id;
    return axios.get(url);
};