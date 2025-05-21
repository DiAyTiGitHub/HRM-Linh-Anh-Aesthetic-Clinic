import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/educationalManagementLevel";

export const pagingEducationalManagementLevels = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getEducationalManagementLevel = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createEducationalManagementLevel = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editEducationalManagementLevel = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteEducationalManagementLevel = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};
