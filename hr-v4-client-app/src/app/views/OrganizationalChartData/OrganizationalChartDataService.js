import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/org-chart";

export const saveOrgChartData = (obj) => {
  const url = API_PATH + "/save-org-chart-data";
  return axios.post(url, obj);
};

export const getOrgChartDataById = (id) => {
  const url = API_PATH + "/get-org-chart-data/" + id;
  return axios.get(url);
};

export const pagingOrgChartData = (obj) => {
  const url = API_PATH + "/paging-org-chart-data";
  return axios.post(url, obj);
};
export const getChartByPositionId = (obj) => {
  const url = API_PATH + "/position";
  return axios.post(url, obj);
};
export const getChartByDepartmentId = (obj) => {
  const url = API_PATH + "/from-department";
  return axios.post(url, obj);
};

export const deleteOrgChartData = (id) => {
  const url = API_PATH + "/delete-org-chart-data/" + id;
  return axios.delete(url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple-org-chart-data";
  return axios.post(url, ids);
};