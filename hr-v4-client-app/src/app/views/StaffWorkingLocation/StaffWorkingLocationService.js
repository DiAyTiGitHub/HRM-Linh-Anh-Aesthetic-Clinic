import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/staff-working-location";

export const pagingStaffWorkingLocation = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getStaffWorkingLocationById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveStaffWorkingLocation = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteStaffWorkingLocationById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const deleteMultipleStaffWorkingLocation = (searchObject) => {
  var url = API_PATH + "/delete-multiple";
  return axios.post(url, searchObject);
};
