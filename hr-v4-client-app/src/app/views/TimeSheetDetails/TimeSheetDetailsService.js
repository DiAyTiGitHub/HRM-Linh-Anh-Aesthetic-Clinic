import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/timesheetdetail";
const API_PATH_TIMESHEET = ConstantList.API_ENPOINT + "/api/timesheet";
const API_TOTAL = ConstantList.API_ENPOINT + "/api/total-time-report/search-by-page";

export const pagingTimeSheetDetail = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getTimeSheetDetail = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const getTimeSheetByTime = (obj) => {
  let url = API_PATH + "/get-timesheet-by-time";
  return axios.post(url, obj);
};

export const getAllTimeSheetDetail = (id) => {
  let url = API_PATH + "/get-time-sheet/" + id;
  return axios.get(url);
};

export const getTimeSheetByStaff = (id) => {
  let url = API_PATH_TIMESHEET + "/getListTimeSheetByStaffId/" + id;
  return axios.get(url);
};

export const createTimeSheetDetail = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editTimeSheetDetail = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteTimeSheetDetail = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};

export const updateStatus = (id, workingStatusId) => {
  let url = API_PATH + "/update-status/" + id + "/" + workingStatusId;
  return axios.put(url);
};

export const totalTimeReport = (searchObject) => {
  return axios.post(API_TOTAL, searchObject);
};

export const exportToExcel = (searchObject) => {
  return axios({
    method: "post",
    url: API_PATH + "/exportExcel",
    data: searchObject,
    responseType: "blob",
  });
};

export const searchTimeSheetDate = (obj) => {
  let url = API_PATH + "/get-list-timesheet-detail";
  return axios.post(url, obj);
}

export const getAllTimesheet = (obj) => {
  let url = API_PATH + "/get-list-timesheet-detail-of-all-staff";
  return axios.post(url, obj);
}

export const saveTimeSheetDetail = (obj) => {
  if (obj?.id) {
    const url = API_PATH + "/" + obj?.id;
    return axios.put(url, obj);
  } else {
    const url = API_PATH;
    return axios.post(url, obj);
  }
}

export const autoGenerateTimeSheetDetails = (searchObject) => {
  const url = API_PATH + "/auto-generate";
  return axios.post(url, searchObject);
}