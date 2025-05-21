import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/province";

export const pagingProvinces = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getProvince = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createProvince = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editProvince = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteProvince = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const getAllProvinces = () => {
  var url = API_PATH + "/getAllProvinces";
  return axios.get(url);
};

export const checkCode = (id, code) => {
  const param = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, param);
};
