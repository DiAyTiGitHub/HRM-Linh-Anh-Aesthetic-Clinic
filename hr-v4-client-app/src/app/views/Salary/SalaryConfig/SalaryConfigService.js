import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-config";

export const pagingSalaryConfig = (searchObject) => {
  const url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveSalaryConfig = (obj) => {
  const url = API_PATH;
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};

export const deleteSalaryConfig = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};