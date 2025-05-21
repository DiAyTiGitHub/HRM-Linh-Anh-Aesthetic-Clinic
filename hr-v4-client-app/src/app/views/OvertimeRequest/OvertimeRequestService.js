import axios from "axios";
import ConstantList from "app/appConfig";
import { saveAs } from "file-saver";

const API_PATH = ConstantList.API_ENPOINT + "/api/overtime-request/";


export const pagingOvertimeRequest = (dto) => axios.post(API_PATH + "search-by-page", dto)
export const getInitialOvertimeRequestFilter = () => {
  let url = API_PATH + "initial-filter";
  return axios.get(url);
};

export const saveOvertimeRequest = (dto) => axios.post(API_PATH + "save-or-update", dto)

export const getById = (id) => axios.get(API_PATH + 'get-by-id/' + id)

export const deleteOvertimeRequest = (id) => axios.delete(API_PATH + 'delete/' + id)

export const deleteMultiple = (ids) => axios.post(API_PATH + 'delete-multiple', ids)

export const updateApprovalStatus = dto => axios.post(API_PATH + "update-requests-approval-status", dto);


export const downloadOTImportTemplate = () => {
  return axios.get(`${API_PATH}download-OT-import-template`, {
    responseType: 'blob'
  });
}


export const importExcelOvertimeRequest = async (file) => {
  try {
    const formData = new FormData();
    formData.append("upload", file);

    const url = API_PATH + "import-excel-overtime-request";

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
    const fileName = `Ket_qua_nhap_DL_YC_OT_${timestamp}.xlsx`;

    // Sử dụng file-saver để tải file
    saveAs(response.data, fileName);

  } catch (error) {
    console.error("Lỗi import Ket_qua_nhap_DL_YC_OT_:", error);
    // Xử lý lỗi phía người dùng tại đây nếu cần
  }
};