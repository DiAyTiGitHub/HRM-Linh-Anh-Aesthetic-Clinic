import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/candidate";

// danh sách ứng viên Đã nhận việc
export const pagingOnboardedCandidates = (searchObject) => {
  const url = API_PATH + "/paging-onboarded-candidates";
  return axios.post(url, searchObject);
};