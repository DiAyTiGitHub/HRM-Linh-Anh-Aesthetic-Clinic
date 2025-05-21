import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/allowance-policy";

export const pagingAllowancePolicy = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getAllowancePolicyById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveAllowancePolicy = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteAllowancePolicy = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};
