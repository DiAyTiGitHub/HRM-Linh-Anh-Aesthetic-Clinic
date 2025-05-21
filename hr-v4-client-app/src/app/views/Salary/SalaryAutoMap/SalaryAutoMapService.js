import axios from "axios";
import ConstantList from "../../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-auto-map";

export const getSalaryAutoMap = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveOrUpdateSalaryAutoMap = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const getAllSalaryAutoMap = (obj) => {
  let url = API_PATH + "/get-all";
  return axios.post(url, obj);
};
