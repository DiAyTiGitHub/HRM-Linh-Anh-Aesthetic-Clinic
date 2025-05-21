import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/allowance";

export const pagingAllowance = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getAllowanceById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveAllowance = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteAllowance = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};