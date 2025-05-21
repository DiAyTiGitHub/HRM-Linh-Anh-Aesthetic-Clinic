import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/work-schedule-calendar";

export const getWorkingScheduleByFilter = (payload) => {
  let url = API_PATH + "/get-working-schedule-by-filter";
  return axios.post(url, payload);
};

export const getWorkCalendarOfStaff = (payload) => {
  let url = API_PATH + "/work-schedule-calendar-of-staff";
  return axios.post(url, payload);
};

export const getInitialTimekeepingReportFilter = () => {
  let url = API_PATH + "/initial-timekeeping-filter";
  return axios.get(url);
};

export const getTimekeepingReportByFitler = payload => {
  let url = API_PATH + "/get-timekeeping-report-by-filter";
  return axios.post(url, payload);
}

export const exportTimekeepingReportByFitler = (dto) => {
  return axios({
    method: "post",
    url: API_PATH + "/export-excel",
    data: dto,
    responseType: "blob",
  });
}

