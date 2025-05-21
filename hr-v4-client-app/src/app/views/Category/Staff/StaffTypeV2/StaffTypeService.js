import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/staff-type";

export const pagingStaffType = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  let url = API_PATH + "/get-by-id/" + id;
  return axios.get(url);
};

export const saveStaffType = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteStaffType = (id) => {
  let url = API_PATH + "/remove/" + id;
  return axios.delete(url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/remove-multiple";
  return axios.post(url, ids);
};
