import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/position-role";

export const pagingPositionRole = (searchObject) => {
  const url = API_PATH + "/pagingPositionRole";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const savePositionRole = (obj) => {
  const url = API_PATH + "/savePositionRole";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH;
  return axios.post(url, ids);
};

export const deletePositionRole = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};