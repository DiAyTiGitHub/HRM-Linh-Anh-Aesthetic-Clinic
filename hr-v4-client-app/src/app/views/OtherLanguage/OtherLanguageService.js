import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/otherLanguage";

export const pagingOtherLanguages = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getOtherLanguage = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createOtherLanguage = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editOtherLanguage = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteOtherLanguage = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const getAllOtherLanguages = () => {
  var url = API_PATH + "/getAllOtherLanguages";
  return axios.get(url);
};

export const checkCode = (id, code) => {
  const param = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, param);
};
