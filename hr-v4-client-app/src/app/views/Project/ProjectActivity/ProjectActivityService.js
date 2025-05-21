import axios from "axios";
import { values } from "lodash";
import ConstantList from "../../../appConfig";
const API_PROJECT_ACTIVITY =
  ConstantList.API_ENPOINT + "/api/project-activity";

export const pagingProjectActivity = (searchObject) => {
  var url = API_PROJECT_ACTIVITY + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getActivity = (id) => {
  let url = API_PROJECT_ACTIVITY + "/" + id;
  return axios.get(url);
};

export const createActivity = (obj) => {
  let url = API_PROJECT_ACTIVITY;
  return axios.post(url, obj);
};

export const getActivityByProject = (id, parentId) => {
  let url = API_PROJECT_ACTIVITY + "?projectId=" + id;
  if (parentId != null) {
    url += "&&parentId=" + parentId;
  }
  return axios.get(url);
};

export const editActivity = (obj) => {
  let url = API_PROJECT_ACTIVITY + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteActivity = (id) => {
  let url = API_PROJECT_ACTIVITY + "/" + id;
  return axios.delete(url);
};

export const checkCodeActivity = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PROJECT_ACTIVITY + "/check-code";
  return axios.get(url, config);
};

export const checkNameActivity = (id, name) => {
  const config = { params: { id: id, name: name } };
  var url = API_PROJECT_ACTIVITY + "/check-name";
  return axios.get(url, config);
};

export const getListByProjectId = (object) => {
  let url = API_PROJECT_ACTIVITY + "/getListByProjectId";
  return axios.post(url, object);
};

export const saveActivity = (value) => {
  if (value?.id) {
    let url = API_PROJECT_ACTIVITY + "/" + value.id;
    return axios.put(url, value);
  } else {
    let url = API_PROJECT_ACTIVITY;
    return axios.post(url, value);
  }
}