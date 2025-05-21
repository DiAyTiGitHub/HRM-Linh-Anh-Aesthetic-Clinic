import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/politicalTheoryLevel";

export const pagingPoliticaltheoryLevels = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getPoliticaltheoryLevel = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createPoliticaltheoryLevel = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editPoliticaltheoryLevel = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deletePoliticaltheoryLevel = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};
