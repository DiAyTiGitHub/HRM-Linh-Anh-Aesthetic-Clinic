import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH_2 = ConstantList.API_ENPOINT + "/api/hrDiscipline";

export const pagingDiscipline = (searchObject) => { //searchByPage
  var url = API_PATH_2 + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getDiscipline = (id) => { //getOne
  var url = API_PATH_2 + "/" + id;
  return axios.get(url);
}

export const createDiscipline = (obj) => { //create
  var url = API_PATH_2;
  return axios.post(url, obj);
}

export const updateDiscipline = (obj) => { //update
  var url = API_PATH_2 + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteDiscipline = (id) => { //deleteOne   
  var url = API_PATH_2 + "/" + id;
  return axios.delete(url)
};

export const checkCode = (id, code) => { //checkCode
  const config = { params: { id: id, code: code } };
  var url = API_PATH_2 + "/checkCode";
  return axios.get(url, config);
};

export const autoGenCode = (configKey) => {
  let url = API_PATH_2 + `/auto-gen-code/${configKey}`;
  return axios.get(url);
};





