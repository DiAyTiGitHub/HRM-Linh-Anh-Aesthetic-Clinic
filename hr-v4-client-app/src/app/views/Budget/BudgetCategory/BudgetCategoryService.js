import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/budget-category";

// Lấy danh sách ngân sách theo phân trang và điều kiện tìm kiếm
export const pagingBudgetCategory = (dto) => {
    const url = `${API_PATH}/paging`;
    return axios.post(url, dto);
};

// Lấy thông tin chi tiết của một ngân sách theo ID
export const getBudgetCategoryById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

// Lưu mới hoặc cập nhật thông tin ngân sách
export const saveBudgetCategory = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, dto);
};

// Xóa một ngân sách bằng ID
export const deleteBudgetCategoryById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
export const deleteMultiple = (dto) => {
    const url = `${API_PATH}/deleteMultiple`;
    return axios.post(url, dto);
};
