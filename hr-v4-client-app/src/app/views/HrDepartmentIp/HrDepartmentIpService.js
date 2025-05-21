import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/hr-department-ip";

export const pagingHrDepartmentIp = (searchObject) => {
  const url = API_PATH + "/pagingHrDepartmentIp";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveDepartmentIp = (obj) => {
  const url = API_PATH + "/saveDepartmentIp";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH;
  return axios.post(url, ids);
};

export const deleteHrDepartmentIp = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};