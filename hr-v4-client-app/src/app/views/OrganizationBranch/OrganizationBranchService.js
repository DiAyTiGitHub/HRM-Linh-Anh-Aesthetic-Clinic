import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/organization-branch";

export const pagingOrganizationBranches = (searchObject) => {
  const url = API_PATH + "/pagingOrganizationBranch";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveOrganizationBranch = (obj) => {
  const url = API_PATH + "/saveOrganizationBranch";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH;
  return axios.post(url, ids);
};

export const deleteOrganizationBranch = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};