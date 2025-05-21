import axios from "axios";
import ConstantList from "../../../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/content-template";

/**
 * Phân trang danh sách tiêu chí đánh giá
 */
export const paging = (searchParams) => {
  const url = `${API_PATH}/search-by-page`;
  return axios.post(url, searchParams);
};
/**
 * Lấy thông tin tiêu chí đánh giá theo ID
 */
export const getById = (id) => {
  const url = `${API_PATH}/${id}`;
  return axios.get(url);
};

/**
 * Tạo mới hoặc cập nhật tiêu chí đánh giá
 */
export const save = (item) => {
  const url = `${API_PATH}/save-or-update`;
  return axios.post(url, item);
};

/**
 * Xóa cứng tiêu chí đánh giá theo ID
 */
export const deleted = (id) => {
  const url = `${API_PATH}/${id}`;
  return axios.delete(url);
};

/**
 * Soft delete – đánh dấu là đã xóa
 */
export const markDeleted = (id) => {
  const url = `${API_PATH}/mark-deleted/${id}`;
  return axios.put(url);
};

/**
 * Lấy danh sách tất cả tiêu chí đánh giá (không phân trang)
 */
export const getAll = () => {
  return axios.get(API_PATH);
};
