import axios from "axios";
import ConstantList from "../../../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/contractType";

export const pagingContractTypes = (searchObject) => {
  var url = API_PATH + "/searchByPage";
  return axios.post(url, searchObject);
};

export const getContractType = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createContractType = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editContractType = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteContractType = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};


export const deleteMultipleContractType = (listId) =>
  axios.delete(API_PATH + "/remove-multiple",{
    data:listId
  });
export const getAllContractType = () =>{
  var url = API_PATH + "/get-all";
  return axios.get(url);
}