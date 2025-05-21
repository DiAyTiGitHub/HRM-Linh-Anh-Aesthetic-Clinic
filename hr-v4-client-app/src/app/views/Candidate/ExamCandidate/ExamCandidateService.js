import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/candidate";

export const pagingExamCandidates = (searchObject) => {
  const url = API_PATH + "/paging-exam-candidates";
  return axios.post(url, searchObject);
};

export const updateExamStatus = (searchObject) => {
  const url = API_PATH + "/update-exam-status";
  return axios.post(url, searchObject);
}