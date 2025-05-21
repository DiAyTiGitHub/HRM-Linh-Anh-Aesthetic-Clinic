import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-unit";

export const pagingSalaryUnit = (searchObject) => {
  const url = API_PATH + "/pagingSalaryUnit";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveSalaryUnit = (obj) => {
  const url = API_PATH + "/saveSalaryUnit";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/deleteMultiple";
  return axios.post(url, ids);
};

export const deleteSalaryUnit = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};