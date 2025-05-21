import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/candidate";

export const pagingPassedCandidates = (searchObject) => {
  const url = API_PATH + "/paging-passed-candidates";
  return axios.post(url, searchObject);
};

export const updateReceptionStatus = (searchObject) => {
  const url = API_PATH + "/update-reception-status";
  return axios.post(url, searchObject);
}

// // chuyển thành trạng thái chờ nhận việc
// export const convertToWaitingJob = (searchObject) => {
//   const url = API_PATH + "/convert-to-waiting-job";
//   return axios.post(url, searchObject);
// }

// // chuyển ứng viên thành trạng thái ĐÃ bị từ chối (sau khi pass bài thi tuyển/phỏng vấn)
// export const convertToRejectedAfterPassedTest = searchObject => {
//   const url = API_PATH + "/convert-to-rejected-after-passed-test";
//   return axios.post(url, searchObject);
// }