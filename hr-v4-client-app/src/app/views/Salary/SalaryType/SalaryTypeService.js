import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-type";

export const pagingSalaryType = (searchObject) => {
  const url = API_PATH + "/pagingSalaryType";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveSalaryType = (obj) => {
  const url = API_PATH + "/saveSalaryType";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/deleteMultiple";
  return axios.post(url, ids);
};

export const deleteSalaryType = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};