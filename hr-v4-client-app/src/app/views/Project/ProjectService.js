import axios from "axios";
import { values } from "lodash";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/project";
const API_PATH_TIMESHEET = ConstantList.API_ENPOINT + "/api/timesheetdetail";
const API_PATH_TASK = ConstantList.API_ENPOINT + "/api/hr-task";
const API_PROJECT_LIST_PATH =
  ConstantList.API_ENPOINT + "/api/project-activity";
const API_LABEL = ConstantList.API_ENPOINT + "/api/label";
const API_LABEL_PATH = ConstantList.API_ENPOINT + "/api/label/getAll";

export const pagingProject = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const pagingProjectActivity = (searchObject) => {
  var url = API_PROJECT_LIST_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getTimeSheetByActivity = (id) => {
  let url =
    API_PATH_TIMESHEET + "/getListTimeSheetDetailByProjectActivityId/" + id;
  return axios.get(url);
};

export const getTaskByActivity = (id) => {
  let url = API_PATH_TASK + "/get-by-project-activity/" + id;
  return axios.get(url);
};

export const getListById = (id) => {
  let url = API_PROJECT_LIST_PATH + "/getListById/" + id;
  return axios.get(url);
};

export const getActivity = (id) => {
  let url = API_PROJECT_LIST_PATH + "/" + id;
  return axios.get(url);
};

export const getProject = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createProject = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const createActivity = (obj) => {
  let url = API_PROJECT_LIST_PATH;
  return axios.post(url, obj);
};

export const getActivityByProject = (id, parentId) => {
  let url = API_PROJECT_LIST_PATH + "?projectId=" + id;
  if (parentId != null) {
    url += "&&parentId=" + parentId;
  }
  return axios.get(url);
};

export const editProject = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const editActivity = (obj) => {
  let url = API_PROJECT_LIST_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteProject = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const deleteActivity = (id) => {
  let url = API_PROJECT_LIST_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/check-code";
  return axios.get(url, config);
};

export const checkCodeActivity = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PROJECT_LIST_PATH + "/check-code";
  return axios.get(url, config);
};

export const checkName = (id, name) => {
  const config = { params: { id: id, name: name } };
  var url = API_PATH + "/check-name";
  return axios.get(url, config);
};

export const checkNameActivity = (id, name) => {
  const config = { params: { id: id, name: name } };
  var url = API_PROJECT_LIST_PATH + "/check-name";
  return axios.get(url, config);
};

//get tất cả label của 1 project
export const getAllLabelByIdProject = () => {
  let url = API_LABEL_PATH;
  return axios.get(url);
};

//lấy từng label theo id của label
export const getLabelByIdLabel = (id) => {
  let url = API_LABEL + "/" + id;
  return axios.get(url);
};

export const getListByProjectId = (object) => {
  let url = API_PROJECT_LIST_PATH + "/getListByProjectId";
  return axios.post(url, object);
};

export const checkCodeProject = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/check-code";
  return axios.get(url, config);
};

export const saveDataProject = (value) => {
  if (values.id) {
    let url = API_PATH + "/" + value.id;
    return axios.put(url, value);
  } else {
    let url = API_PATH;
    return axios.post(url, value);
  }
};

export const saveActivity = (value) => {
  if (value.id) {
    let url = API_PROJECT_LIST_PATH + "/" + value.id;
    return axios.put(url, value);
  } else {
    let url = API_PROJECT_LIST_PATH;
    return axios.post(url, value);
  }
};
