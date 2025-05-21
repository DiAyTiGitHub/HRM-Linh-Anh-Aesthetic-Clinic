import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/timesheet";
const API_PATH_DETAIL = ConstantList.API_ENPOINT + "/api/timesheetdetail";
const API_TOTAL =
  ConstantList.API_ENPOINT + "/api/total-time-report/search-by-page";
const API_LABEL_PATH = ConstantList.API_ENPOINT + "/api/label/getAll";
const API_LABEL = ConstantList.API_ENPOINT + "/api/label";
const API_PA_PATH = ConstantList.API_ENPOINT + "/api/project-activity";

export const pagingTimeSheet = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

/* TimeSheet */
export const getTimeSheet = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createTimeSheet = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editTimeSheet = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteTimeSheet = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

/* TimeKeeping */
export const createTimeKeeping = (obj) => {
  let url = API_PATH + "/do-timekeeping";
  return axios.post(url, obj);
};

/* get TimeSheet by Time */
export const getTimeSheetByTime = (obj) => {
  let url = API_PATH + "/get-timesheet-by-time";
  return axios.post(url, obj);
};

/* get TimeSheet by Time */
export const checkTimeKeeping = (obj) => {
  let url = API_PATH + "/check-time-keeping";
  return axios.post(url, obj);
};

/* get TimeSheet by Time */
export const checkTimeSheetDetail = (obj) => {
  let url = API_PATH + "/check-detail";
  return axios.post(url, obj);
};

/* TimeSheetDetail */
export const getAllTimeSheetDetail = (id) => {
  let url = API_PATH_DETAIL + "/get-time-sheet/" + id;
  return axios.get(url);
};

/* Other */

export const checkCode = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/checkCode";
  return axios.get(url, config);
};

export const updateStatus = (id, workingStatusId) => {
  let url = API_PATH + "/update-status/" + id + "/" + workingStatusId;
  return axios.put(url);
};

export const exportToExcel = (searchObject) => {
  return axios({
    method: "post",
    url: API_PATH + "/exportExcel",
    data: searchObject,
    responseType: "blob",
  });
};

export const exportToExcelStaffNotTimeKeeping = (searchObject) => {
  return axios({
    method: "post",
    url: API_PATH + "/exportExcel-staff-not-timekeeping",
    data: searchObject,
    responseType: "blob",
  });
};
export const exportToExcelStaffNotTimeSheet = (searchObject) => {
  return axios({
    method: "post",
    url: API_PATH + "/exportExcel-staff-not-timesheet",
    data: searchObject,
    responseType: "blob",
  });
};

export const totalTimeReport = (searchObject) => {
  return axios.post(API_TOTAL, searchObject);
};

export const getAllLabelByIdProject = (id) => {
  let url = API_LABEL_PATH + "/" + id;
  return axios.get(url);
};

//them label ở phần thêm task
export const addLabelTask = (searchObject) => {
  var url = API_LABEL;
  return axios.post(url, searchObject);
};

export const editLabelTask = (obj) => {
  var url = API_LABEL + "/" + obj.id;
  return axios.put(url, obj);
};

export const deleteLabelTask = (obj) => {
  var url = API_LABEL + "/" + obj.id;
  return axios.delete(url, obj);
};

export const searchToList = (searchObject) => {
  var url = API_PA_PATH + "/search-to-list";
  return axios.post(url, searchObject);
};

export const searchToListPaging = (searchObject) => {
  var url = API_PA_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getTimeKeepingByMonth = (searchObject) => {
  var url = API_PATH + "/get-time-keeping-by-month";
  return axios.post(url, searchObject);
};

/* TimeSheet */
export const getTimeSheetByDate = (obj) => {
  let url = API_PATH + "/get-by-date";
  return axios.post(url, obj);
};

export const checkTimeSheet = (obj) => {
  let url = API_PATH + "/check-time-sheet";
  return axios.post(url, obj);
};

export const saveTimeSheet = (obj) => {
  let url = API_PATH + "/save-timekeeping";
  return axios.post(url, obj);
};

export const getCurrentTimekeepingData = (obj) => {
  let url = API_PATH + "/get-current-timekeeping";
  return axios.get(url);
};
