import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/educationDegree";

export const pagingEducationDegrees = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getEducationDegree = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveEducationDegree = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editEducationDegree = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteEducationDegree = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};

export const checkName = (id, name) => {
  const config = { params: { id: id, name: name } };
  var url = API_PATH + "/checkName";
  return axios.get(url, config);
};

export const getAllEducationDegrees = () => {
  var url = API_PATH + "/getAllEducationDegrees";
  return axios.get(url);
};
