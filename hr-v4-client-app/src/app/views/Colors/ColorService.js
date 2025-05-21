import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/colors";

export const pagingColors = (searchObject) => {
  var url = API_PATH + "/pagingColors";
  return axios.post(url, searchObject);
};

export const getColor = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createColor = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editColor = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteColor = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};
