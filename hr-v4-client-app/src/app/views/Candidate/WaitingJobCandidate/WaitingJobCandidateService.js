import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/candidate";

// danh sách ứng viên Chờ nhận việc
export const pagingWaitingJobCandidates = (searchObject) => {
  const url = API_PATH + "/paging-waiting-job-candidates";
  return axios.post(url, searchObject);
};

// Chuyển ứng viên sang Không tới nhận việc
export const convertToNotCome = (searchObject) => {
  const url = API_PATH + "/convert-to-not-come";
  return axios.post(url, searchObject);
};

// Chuyển ứng viên sang Đã nhận việc
export const convertToReceivedJob = (searchObject) => {
  const url = API_PATH + "/convert-to-received-job";
  return axios.post(url, searchObject);
};