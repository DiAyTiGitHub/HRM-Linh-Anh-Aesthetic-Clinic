import axios from "axios";
import ConstantList from "../../../../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/evaluation-template";

/**
 * Phân trang danh sách tiêu chí đánh giá
 */
export const paging = (searchParams) => {
  const url = `${API_PATH}/paging`;
  return axios.post(url, searchParams);
};
/**
 * Lấy thông tin tiêu chí đánh giá theo ID
 */
export const getEvaluationTemplate = (id) => {
  const url = `${API_PATH}/find-by-id/${id}`;
  return axios.get(url);
};

/**
 * Tạo mới hoặc cập nhật tiêu chí đánh giá
 */
export const saveEvaluationTemplate = (item) => {
  const url = `${API_PATH}/save`;
  return axios.post(url, item);
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
