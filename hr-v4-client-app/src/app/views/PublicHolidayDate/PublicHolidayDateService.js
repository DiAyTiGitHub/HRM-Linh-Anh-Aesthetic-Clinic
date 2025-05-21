import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/public-holiday-date";

export const pagingPublicHolidayDate = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getPublicHolidayDateById = (id) => {
  let url = API_PATH + "/get-by-id/" + id;
  return axios.get(url);
};

export const saveOrUpdatePublicHolidayDate = (obj) => {
  const url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deletePublicHolidayDate = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const createPublicHolidayDateAutomatic = (obj) => {
  const url = API_PATH + "/create-automatic";
  return axios.post(url, obj);
};


export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};