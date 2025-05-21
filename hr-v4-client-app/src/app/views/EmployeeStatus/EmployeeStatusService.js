import axios from "axios";
import Config from "../../appConfig";

const API_PATH = Config.API_ENPOINT + "/api/employee-status";

export const pagingEmployeeStatus = (searchObject) => {
  var url = API_PATH + "/paging";
  return axios.post(url, searchObject);
};

export const getEmployeeStatus = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveEmployeeStatus = (obj) => axios.post(API_PATH, obj);

export const deleteEmployeeStatus = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCodeEmployeeStatus = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/check-code";
  return axios.get(url, config);
};