import axios from "axios";
import ConstantList from "../../appConfig";
import { getFormData } from "app/LocalFunction";
import { saveAs } from "file-saver";

const API_PATH = ConstantList.API_ENPOINT + "/api/timesheetdetail";

// Lấy chi tiết Timesheet theo ID
export const getTimeSheetDetailById = (id) => {
  return axios.get(`${API_PATH}/${id}`);
};

// Lưu chi tiết Timesheet
export const saveTimeSheetDetail = (obj) => {
  return axios.post(API_PATH, obj);
};

// Lưu chi tiết TimesheetDetail
export const saveOrUpdateTimeSheetDetail = (obj) => {
  return axios.post(`${API_PATH}/save-or-update`, obj);
};

// Xoá chi tiết Timesheet theo danh sách
export const deleteTimeSheetDetails = (list) => {
  return axios.delete(API_PATH, { data: list });
};

// Cập nhật chi tiết Timesheet
export const updateTimeSheetDetail = (id, obj) => {
  return axios.put(`${API_PATH}/${id}`, obj);
};

// Lấy danh sách Timesheet theo ID của Timesheet
export const getTimeSheetByTime = (id) => {
  return axios.get(`${API_PATH}/get-time-sheet/${id}`);
};

// Tìm kiếm Timesheet có phân trang
export const pagingTimeSheetDetail = (searchObject) => {
  return axios.post(`${API_PATH}/search-by-page`, searchObject);
};

// Lấy danh sách TimesheetDetail theo Project Activity ID
export const getListByProjectActivityId = (id) => {
  return axios.get(`${API_PATH}/getListTimeSheetDetailByProjectActivityId/${id}`);
};

// Lấy danh sách TimesheetDetail theo Project ID
export const getListByProjectId = (id) => {
  return axios.get(`${API_PATH}/getListTimeSheetDetailByProjectId/${id}`);
};

// Export Timesheet ra Excel
export const exportTimeSheetsToExcel = (searchObject) => {
  return axios.post(`${API_PATH}/exportExcel`, searchObject, { responseType: 'blob' });
};

// Cập nhật trạng thái Timesheet
export const updateTimeSheetStatus = (id, workingStatusId) => {
  return axios.put(`${API_PATH}/update-status/${id}/${workingStatusId}`);
};

// Xoá chi tiết Timesheet theo ID
export const deleteTimeSheetDetail = (id) => {
  return axios.delete(`${API_PATH}/${id}`);
};

// Cập nhật Timesheet Detail
export const updateDetail = () => {
  return axios.get(`${API_PATH}/updateDetail`);
};

// Lấy danh sách TimesheetDetail
export const getTimesheetDetail = (searchObject) => {
  return axios.post(`${API_PATH}/get-list-timesheet-detail`, searchObject);
};

// Lấy danh sách TimesheetDetail của tất cả nhân viên
export const getListTimesheetDetailOfAllStaff = (searchObject) => {
  return axios.post(`${API_PATH}/get-list-timesheet-detail-of-all-staff`, searchObject);
};

// Tự động sinh TimesheetDetail
export const autoGenerateTimeSheetDetails = (searchObject) => {
  return axios.post(`${API_PATH}/auto-generate`, searchObject);
};

export const importTimeSheetDetail = async (file) => {
  try {
    const formData = new FormData();
    formData.append("upload", file);

    const url = API_PATH + "/import-timeSheet-detail";

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
    const fileName = `ket_qua_import_cham_cong_${timestamp}.xlsx`;

    // Sử dụng file-saver để tải file
    saveAs(response.data, fileName);

  } catch (error) {
    console.error("Lỗi import timesheet:", error);
    // Xử lý lỗi phía người dùng tại đây nếu cần
  }
};

export const importDataWithSystemTemplate = async (file) => {
  try {
    const formData = new FormData();
    formData.append("uploadfile", file);

    const url = API_PATH + "/import-data-with-system-template";

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
    const fileName = `ket_qua_import_cham_cong_${timestamp}.xlsx`;

    // Sử dụng file-saver để tải file
    saveAs(response.data, fileName);

  } catch (error) {
    console.error("Lỗi import timesheet:", error);
    // Xử lý lỗi phía người dùng tại đây nếu cần
  }
};

export const downloadTimesheetDetailTemplate = () => {
  return axios.get(`${API_PATH}/download-timeSheet-detail-template`, {
    responseType: 'blob'
  });
}

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};


// Xuất dữ liệu chấm công theo mẫu của hệ thống
export const exportDataWithSystemTemplate = (searchObject) => {
  return axios({
    method: "post",
    url: API_PATH + "/export-with-system-template",
    data: searchObject,
    responseType: "blob",
  });
}


export const getInitialTimesheetDetailFilter = () => {
  let url = API_PATH + "/initial-filter";
  return axios.get(url);
};

// Xuất dữ liệu chấm công LINH ANH
export const exportExcelLATimekeepingData = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/export-excel-LA-timekeeping-data",
        data: searchObject,
        responseType: "blob",
    });
}