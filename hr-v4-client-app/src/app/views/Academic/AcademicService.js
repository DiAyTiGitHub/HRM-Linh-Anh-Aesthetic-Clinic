import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/academicTitle";

export const pagingAcademics = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getAcademic = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createAcademic = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editAcademic = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteAcademic = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const getAllAcademics = () => {
  var url = API_PATH + "/getAllAcademics";
  return axios.get(url);
};

export const checkCode = (id,code) => {
  const param = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url,param);
};
