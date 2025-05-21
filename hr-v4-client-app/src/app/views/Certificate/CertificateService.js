import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/certificate";

export const pagingCertificates = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getCertificate = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createCertificate = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editCertificate = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteCertificate = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};

export const getAllcertificates = () => {
  var url = API_PATH + "/getAllcertificates";
  return axios.get(url);
};
