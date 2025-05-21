import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/staff-social-insurance";


export const pagingStaffSocialInsurance = (searchObject) => {
  var url = API_PATH + "/paging-staff-social-insurance";
  return axios.post(url, searchObject);
};

export const getInitialStaffSocialInsuranceFilter = () => {
  let url = API_PATH + "/initial-filter";
  return axios.get(url);
};

export const getStaffSocialInsuranceById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};


export const exportBHXH = (search) => {
  return axios({
    method: "post",
    url: `${API_PATH}/export`,
    data: search, // Đảm bảo dữ liệu được gửi trong body
    responseType: "blob",
    headers: {
      "Content-Type": "application/json", // Chỉ định kiểu dữ liệu gửi lên
    },
  });
};

export const saveStaffSocialInsurance = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const deleteStaffSocialInsurance = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};

export const updateStaffSocialInsurancePaidStatus = (payload) => {
  const url = API_PATH + "/update-paid-status";
  return axios.post(url, payload);
}


export const generateSocialInsuranceTicketsForStaffsBySalaryPeriod = (searchObject) => {
  var url = API_PATH + "/generate-insurance-tickets";
  return axios.post(url, searchObject);
};

export const generateSingleSocialInsuranceTicket = (searchObject) => {
  var url = API_PATH + "/generate-single-ticket";
  return axios.post(url, searchObject);
};
