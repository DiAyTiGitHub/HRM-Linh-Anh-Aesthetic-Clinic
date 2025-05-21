import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff-working-history";

export const pagingStaffWorkingHistory = (searchObject) => {
  const url = API_PATH + "/paging";
  return axios.post(url, searchObject);
};

export const getStaffWorkingHistoryById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveStaffWorkingHistory = (obj) => {
  const url = API_PATH + "/save";
  return axios.post(url, obj);
};

export const deleteStaffWorkingHistory = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const getRecentStaffWorkingHistory = (staffId) => {
  const url = API_PATH + "/recent/" + staffId;
  return axios.get(url);
};


