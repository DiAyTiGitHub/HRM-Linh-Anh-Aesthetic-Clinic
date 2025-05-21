import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/staff-allowance";

export const pagingStaffAllowance = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getStaffAllowanceById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveStaffAllowance = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteStaffAllowance = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const getListStaffAllowanceByStaffId = (staffId) => {
  const url = `${API_PATH}/staff-allowance-of-staff/${staffId}`;
  return axios.get(url);
};