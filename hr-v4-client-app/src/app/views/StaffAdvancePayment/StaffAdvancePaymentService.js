import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/staff-advance-payment";


export const pagingStaffAdvancePayment = (searchObject) => {
  var url = API_PATH + "/paging-staff-advance-payment";
  return axios.post(url, searchObject);
};

export const getStaffAdvancePaymentById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const getInitialAdvancePaymentFilter = () => {
  let url = API_PATH + "/initial-filter";
  return axios.get(url);
};

export const saveStaffAdvancePayment = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const deleteStaffAdvancePayment = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};

export const updateStaffAdvancePaymentApprovalStatus = (payload) =>{
  const url = API_PATH + "/update-approval-status";
  return axios.post(url, payload);
}