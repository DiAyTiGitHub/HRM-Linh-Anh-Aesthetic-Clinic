import axios from "axios";
import ConstantList from "../../../../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/evaluation-item";

/**
 * Phân trang danh sách tiêu chí đánh giá
 */
export const pagingEvaluationItems = (searchParams) => {
    const url = `${API_PATH}/paging`;
    return axios.post(url, searchParams);
};

/**
 * Lấy thông tin tiêu chí đánh giá theo ID
 */
export const getEvaluationItem = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

/**
 * Tạo mới hoặc cập nhật tiêu chí đánh giá
 */
export const saveEvaluationItem = (item) => {
  const url = `${API_PATH}/save`;
  return axios.post(url, item);
};


export const downloadTemplateFileExcel = () => {
  return axios({
      method: "post",
      url: `${API_PATH}/download-template`,
      responseType: "blob",
  });
}

export const importExcelEvaluationItem = (file) => {
    let formData = new FormData();
    formData.append("uploadfile", file);
    let url = API_PATH + "/import";
    return axios({
        url: url,
        headers: {
            "Content-Type": "multipart/form-data",
            Accept: "*/*",
        },
        method: "POST",
        data: formData,
    });
};

/**
 * Xóa cứng tiêu chí đánh giá theo ID
 */
export const deleteEvaluationItem = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};

/**
 * Soft delete – đánh dấu là đã xóa
 */
export const markDeletedEvaluationItem = (id) => {
    const url = `${API_PATH}/mark-deleted/${id}`;
    return axios.put(url);
};

/**
 * Lấy danh sách tất cả tiêu chí đánh giá (không phân trang)
 */
export const getAllEvaluationItems = () => {
    return axios.get(API_PATH);
};
/**
 * Tự động sinh mã tiêu chí đánh giá
 */
export const autoGenCode = (configKey) => {
    let url = API_PATH + `/auto-gen-code/${configKey}`;
    return axios.get(url);
};
