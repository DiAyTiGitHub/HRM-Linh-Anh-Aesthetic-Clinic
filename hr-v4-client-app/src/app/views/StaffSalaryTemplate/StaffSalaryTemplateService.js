import axios from "axios";
import ConstantList from "../../appConfig";
import { saveAs } from "file-saver";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff-salary-template";

export const pagingStaffSalaryTemplate = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const findStaffTemplateIdByStaffIdAndTemplateId = (searchObject) => {
  var url = API_PATH + "/find-staff-template-id-by-staff-id-and-template-id";
  return axios.post(url, searchObject);
};

export const getStaffSalaryTemplateById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const findByStaffIdAndTemplateId = (searchObject) => {
  var url = API_PATH + "/find-by-staff-id-and-template-id";
  return axios.post(url, searchObject);
};

export const saveStaffSalaryTemplate = (obj) => {
  let url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteStaffSalaryTemplate = (id) => {
  let url = API_PATH + "/delete/" + id;
  return axios.delete(url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};

export const getSalaryTemplatesOfStaff = (obj) => {
  let url = API_PATH + "/salary-template-of-staff";
  return axios.post(url, obj);
};

export const downloadTemplateStaffSalaryTemplate = async (searchObject) => {
  const url = `${API_PATH}/export-import-template`;

  try {
    const response = await axios.post(url, searchObject, {
      responseType: 'blob',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return response.data;
  } catch (error) {
    console.error('Export template failed:', error);
    throw error;
  }
};

export const importFileStaffSalaryTemplate = async (file) => {
  try {
    const formData = new FormData();
    formData.append("uploadfile", file);

    const url = API_PATH + "/import-list-staff-salary-template";

    const response = await axios({
      url: url,
      method: "POST",
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
        "Accept": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      },
      responseType: "blob", // Quan trọng để nhận về dạng file
    });

    // Tạo chuỗi thời gian hiện tại: dd-MM-yyyy_HH-mm-ss
    const now = new Date();
    const pad = (n) => n.toString().padStart(2, "0");
    const timestamp = `${pad(now.getDate())}-${pad(now.getMonth() + 1)}-${now.getFullYear()}_${pad(now.getHours())}-${pad(now.getMinutes())}-${pad(now.getSeconds())}`;

    // Tên file cuối cùng
    const fileName = `ket_qua_import_mau_bang_luong_nhan_vien_${timestamp}.xlsx`;

    // Sử dụng file-saver để tải file
    saveAs(response.data, fileName);

  } catch (error) {
    console.error("Lỗi import staffSalaryTemplate:", error);
    // Xử lý lỗi phía người dùng tại đây nếu cần
  }
};