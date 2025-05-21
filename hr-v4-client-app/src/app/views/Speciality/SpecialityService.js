import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/hrSpeciality";

export const pagingSpecialities = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getSpeciality = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createSpeciality = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editSpeciality = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteSpeciality = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};

export const autoGenCode = (configKey) => {
  let url = API_PATH + `/auto-gen-code/${configKey}`;
  return axios.get(url);
};

