import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-area";

export const pagingSalaryArea = (searchObject) => {
  const url = API_PATH + "/pagingSalaryArea";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveSalaryArea = (obj) => {
  const url = API_PATH + "/saveSalaryArea";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/deleteMultiple";
  return axios.post(url, ids);
};

export const deleteSalaryArea = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};