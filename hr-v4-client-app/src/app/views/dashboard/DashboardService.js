import axios from "axios";
import ConstantList from "../../appConfig";
const API_TOTAL =
  ConstantList.API_ENPOINT + "/api/total-time-report/search-by-page";
const API_PROJECT_TOTAL =
  ConstantList.API_ENPOINT + "/api/project-time-work/search-by-page";
const API_PROJECT_TOTAL_USER =
  ConstantList.API_ENPOINT + "/api/project-time-work/search-by-page-user";
const API_HIGHCHART =
  ConstantList.API_ENPOINT + "/api/project-time-work/get-chart-time";

export const getDashboard = (searchobject) => {
  return axios.post(ConstantList.API_ENPOINT + "/api/dashboard", searchobject);
};

export const totalTimeReport = (searchObject) => {
  return axios.post(API_TOTAL, searchObject);
};

export const totalProjectTimeReport = () => {
  return axios.get(API_PROJECT_TOTAL);
};

export const totalProjectTimeReportUser = (searchObject) => {
  return axios.post(API_PROJECT_TOTAL_USER, searchObject);
};

export const highchartReport = (searchObject) => {
  return axios.post(API_HIGHCHART, searchObject);
};

export const getListTimekeepingSummary = (searchObject) => {
  return axios.post(ConstantList.API_ENPOINT + "/api/timesheet/get-list-timekeeping-summary", searchObject);
}