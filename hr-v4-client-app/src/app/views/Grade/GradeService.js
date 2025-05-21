import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/civilServantGrade";

export const pagingGrade = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getGrade = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveGrade = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editGrade = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteGrade = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};
export const getAllGrade = () => {
  let url = API_PATH + "/getAll";
  return axios.get(url);
};
export const checkCode = (id,code) =>{
  const param = { params: { id: id, code: code } };
  let url = API_PATH + "/checkCode";
  return axios.get(url,param);
};
