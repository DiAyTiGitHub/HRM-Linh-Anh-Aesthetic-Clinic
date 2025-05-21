import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/org-chart";

export const saveOrgChartData = (obj) => {
  const url = API_PATH + "/save-org-chart-data";
  return axios.post(url, obj);
};

export const getOrgChartData = (id) => {
  const url = API_PATH + "/get-org-chart-data/" + id;
  return axios.get(url);
};

export const refreshOrgChartData = (id) => {
  const url = API_PATH + "/refresh/" + id;
  return axios.get(url);
};

export const getPositionChart = (id) => {
  const url = API_PATH + "/sync-position";
  return axios.get(url);
};

export const pagingOrgChartData = (obj) => {
  const url = API_PATH + "/paging-org-chart-data";
  return axios.post(url, obj);
};

export const linkOrgChart = (obj) => {
  const url = API_PATH + "/link-org-chart";
  return axios.post(url, obj);
};

export const saveRelaytion = (obj) => {
  const url = API_PATH + "/save-relationship";
  return axios.post(url, obj);
};

export const deleteRelaytion = (obj) => {
  const url = API_PATH + "/delete-relationship";
  return axios.post(url, obj);
};

export const disconnectOrgChart = (id) => {
  const url = API_PATH + "/disconnect-org-chart/" + id;
  return axios.delete(url);
};

export const deleteOrganizationChart = (id) => {
  const url = API_PATH + "/delete/" + id;
  return axios.delete(url);
};

export const saveOrganizationChart = (obj) => {
  const url = API_PATH + "/save";
  return axios.post(url, obj);
};