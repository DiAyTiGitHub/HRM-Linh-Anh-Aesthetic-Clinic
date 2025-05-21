import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/export-candidate-report";

export const pagingExportCandidate = (searchObject) => {
  const url = API_PATH + "/paging-export-candidate";
  return axios.post(url, searchObject);
};

export const exportExcelCandidatesByFilter = (searchObject) => {
  const url = API_PATH + "/export-excel-candidate-by-filter";

  return axios({
    method: "post",
    url: url,
    data: searchObject,
    responseType: "blob",
  });
}