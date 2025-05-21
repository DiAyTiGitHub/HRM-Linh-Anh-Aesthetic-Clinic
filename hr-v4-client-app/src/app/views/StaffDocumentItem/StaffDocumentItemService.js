import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/staff-document-item";

export const pagingStaffDocumentItem = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getStaffDocumentItemByTemplateAndStaff = (searchObject) => {
  var url = API_PATH + "/get-item-by-template-staff";
  return axios.post(url, searchObject);
};

export const saveTemplateAndStaff = (searchObject) => {
  var url = API_PATH + "/save-template-staff";
  return axios.post(url, searchObject);
};

export const getStaffDocumentItemById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveStaffDocumentItem = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteStaffDocumentItemById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const deleteMultipleStaffDocumentItem = (searchObject) => {
  var url = API_PATH + "/delete-multiple";
  return axios.post(url, searchObject);
};

export async function uploadFile(file) {
  const url = API_PATH + "/upload";
  let formData = new FormData();
  formData.append('uploadfile', file);//Lưu ý tên 'uploadfile' phải trùng với tham số bên Server side
  const config = {
      headers: {
          'Content-Type': 'multipart/form-data'
      }
  }
  return axios.post(url, formData, config);
}
