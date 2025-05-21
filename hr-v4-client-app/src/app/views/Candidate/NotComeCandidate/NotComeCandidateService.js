import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/candidate";

// danh sách ứng viên Không đến nhận việc
export const pagingNotComeCandidates = (searchObject) => {
  const url = API_PATH + "/paging-not-come-candidates";
  return axios.post(url, searchObject);
};