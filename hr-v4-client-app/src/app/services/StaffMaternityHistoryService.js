import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/maternityHistory";

// 📌 Tìm kiếm danh sách lịch sử thai sản theo trang
export const pagingStaffMaternityHistory = (searchObject) => {
    const url = `${API_PATH}/searchByPage`;
    return axios.post(url, searchObject);
};

// 📌 Lấy danh sách lịch sử thai sản của một nhân viên theo staffId
export const getAllStaffMaternityHistory = (staffId) => {
    const url = `${API_PATH}/getAll/${staffId}`;
    return axios.get(url);
};

// 📌 Lấy thông tin lịch sử thai sản theo ID
export const getStaffMaternityHistoryById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

// 📌 Lưu hoặc cập nhật thông tin lịch sử thai sản
export const saveOrUpdateStaffMaternityHistory = (maternityHistory) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, maternityHistory);
};

// 📌 Xóa lịch sử thai sản theo ID
export const deleteStaffMaternityHistory = (id) => {
    const url = `${API_PATH}/delete/${id}`;
    return axios.delete(url);
};

// 📌 Xóa nhiều bản ghi lịch sử thai sản theo danh sách ID
export const deleteMultipleStaffMaternityHistory = (ids) => {
    const url = `${API_PATH}/deleteLists`;
    return axios.delete(url, { data: ids });
};
