import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/hrEducationType";

export const pagingEducationTypes = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getEducationType = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createEducationType = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editEducationType = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteEducationType = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};
