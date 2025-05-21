import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff-signature";

// Tìm kiếm danh sách StaffSignature theo trang
export const pagingStaffSignature = (searchObject) => {
    const url = `${API_PATH}/searchByPage`;
    return axios.post(url, searchObject);
};

// Lấy thông tin chữ ký nhân viên theo ID
export const getStaffSignatureById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

// Lưu hoặc cập nhật thông tin chữ ký nhân viên
export const saveOrUpdateStaffSignature = (staffSignature) => {
    const url = `${API_PATH}/save-or-update`;
    return axios.post(url, staffSignature);
};

// Xóa chữ ký nhân viên theo ID
export const deleteStaffSignature = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};

export const generateUniqueSignatureCode = () => {
    const url = `${API_PATH}/generate-staff-signature`;
    return axios.get(url);
};
