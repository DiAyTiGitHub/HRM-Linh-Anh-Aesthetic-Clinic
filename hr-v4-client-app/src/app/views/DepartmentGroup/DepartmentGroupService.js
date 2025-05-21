import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/department-group";

export const pagingDepartmentGroup = (searchObject) => {
  const url = API_PATH + "/pagingDepartmentGroup";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveDepartmentGroup = (obj) => {
  const url = API_PATH + "/saveDepartmentGroup";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH;
  return axios.post(url, ids);
};

export const deleteDepartmentGroup = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};