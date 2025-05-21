import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/hr-resource-plan";

export const pagingHrResourcePlan = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getHrResourcePlanById = (id) => {
  let url = API_PATH + "/get-by-id/" + id;
  return axios.get(url);
};

export const saveHrResourcePlan = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteHrResourcePlan = (id) => {
  let url = API_PATH + "/remove/" + id;
  return axios.delete(url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};



// Phó tổng giám đốc cập nhật trạng thái
export const updateStatusByViceGeneralDirector = (searchObject) => {
  const url = API_PATH + "/update-status-by-vice-general-director";
  return axios.post(url, searchObject);
}

// Tổng giám đốc cập nhật trạng thái
export const updateStatusByGeneralDirector = (searchObject) => {
  const url = API_PATH + "/update-status-by-general-director";
  return axios.post(url, searchObject);
}

export const updateStatus = (searchObject) => {
  const url = API_PATH + "/update-status";
  return axios.post(url, searchObject);
}

export const autoGenCode = (configKey) => {
  let url = API_PATH + `/auto-gen-code/${configKey}`;
  return axios.get(url);
};



export const getDepartmentResourcePlan = (searchObject) => {
  var url = API_PATH + "/get-department-resource-plan";
  return axios.post(url, searchObject);
};

export const getDepartmentResourcePlanTree = (searchObject) => {
  var url = API_PATH + "/get-department-resource-plan-tree";
  return axios.post(url, searchObject);
};

export const getDepartmentResourcePlanTreeBySpreadLevel = (searchObject) => {
  var url = API_PATH + "/get-department-resource-plan-tree-by-spread-level";
  return axios.post(url, searchObject);
};



