import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/hr-introduce-cost";

export const pagingHrIntroduceCost = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post (url, searchObject);
};

export const getHrIntroduceCostById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get (url);
};

export const saveHrIntroduceCost = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post (url, obj);
};

export const deleteHrIntroduceCost = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete (url);
};

export const getListHrIntroduceCostByStaffId = (staffId) => {
  const url = `${API_PATH}/staff-allowance-of-staff/${staffId}`;
  return axios.get (url);
};


export const excelIntroduceCost = (searchObject) => {
  return axios ({
    method:"post",
    url:API_PATH + "/export-excel-introduce-cost",
    data:searchObject,
    responseType:"blob",
  });
}