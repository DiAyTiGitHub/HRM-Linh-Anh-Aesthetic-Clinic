import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff-work-schedule";

export const exportActualTimesheet = (searchObject) => {
  return axios ({
    method:"post",
    url:API_PATH + "/export-actual-time-sheet",
    data:searchObject,
    responseType:"blob",
  });
}

export const getStaffWorkSchedule = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get (url);
};

export const recalculateStaffWorkTime = (id) => {
  let url = API_PATH + "/recalculate/" + id;
  return axios.get (url);
};

export const createStaffWorkScheduleList = (obj) => {
  let url = API_PATH + "/assign-shift-for-multiple-staffs";
  return axios.post (url, obj);
};

export const getInitialShiftAssignmentForm = (id) => {
  let url = API_PATH + "/initial-shift-assignment-form";
  return axios.get (url);
};


export const importStaffWorkSchedule = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);
  let url = API_PATH + "/import-excel-staff-work-schedule";
  return axios ({
    url:url,
    headers:{
      "Content-Type":"multipart/form-data",
      "Accept":"*/*"
    },
    method:"POST",
    data:formData,
  });
}


export const downloadTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH}/export-excel-template`,
    responseType:"blob",
  });
}

export const saveOneStaffWorkSchedule = (obj) => {
  let url = API_PATH + "/save-one";
  return axios.post (url, obj);
};

export const saveMultipleStaffWorkSchedule = (obj) => {
  let url = API_PATH + "/save-multiple";
  return axios.post (url, obj);
};

export const saveScheduleStatistic = (obj) => {
  let url = API_PATH + "/save-schedule-statistic";
  return axios.post (url, obj);
};

export const deleteStaffWorkSchedule = (id) => {
  let url = API_PATH + "/delete/" + id;
  return axios.delete (url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post (url, ids);
};

export const pagingStaffWorkSchedule = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post (url, searchObject);
};
export const getInitialStaffWorkScheduleFilter = () => {
  let url = API_PATH + "/initial-filter";
  return axios.get (url);
};

export const pagingWorkScheduleResult = (searchObject) => {
  var url = API_PATH + "/paging-work-schedule-result";
  return axios.post (url, searchObject);
};

export const updateScheduleOTHours = (obj) => {
  let url = API_PATH + "/update-schedule-ot-hours";
  return axios.post (url, obj);
};

export const getSchedulesInDayOfStaff = (obj) => {
  let url = API_PATH + "/get-schedules-in-day-of-staff";
  return axios.post (url, obj);
};

export const getStaffWorkScheduleSummary = (obj) => {
  let url = API_PATH + "/summary-of-staff-work-schedule";
  return axios.post (url, obj);
};

export const lockSchedulesMultiple = (ids) => {
  const url = API_PATH + "/lock-schedules";
  return axios.post (url, ids);
};

export const reStatisticSchedule = (searchObject) => {
  var url = API_PATH + "/re-statistic-schedules";
  return axios.post (url, searchObject);
};