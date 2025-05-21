import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/interview-schedule";

/**
 * Phân trang danh sách lịch phỏng vấn
 */
export const pagingInterviewSchedules = (searchParams) => {
    const url = `${API_PATH}/paging`;
    return axios.post(url, searchParams);
};

/**
 * Lấy thông tin lịch phỏng vấn theo ID
 */
export const getInterviewSchedule = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

/**
 * Tạo mới hoặc cập nhật lịch phỏng vấn
 */
export const saveInterviewSchedule = (item) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, item);
};

export const saveInterviewSchedules = (item) => {
    const url = `${API_PATH}/save-multiple`;
    return axios.post(url, item);
};

/**
 * Xóa cứng lịch phỏng vấn theo ID
 */
export const deleteInterviewSchedule = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};

/**
 * Soft delete – đánh dấu là đã xóa lịch phỏng vấn
 */
export const markDeletedInterviewSchedule = (id) => {
    const url = `${API_PATH}/mark-deleted/${id}`;
    return axios.put(url);
};

/**
 * Lấy danh sách tất cả lịch phỏng vấn (không phân trang)
 */
export const getAllInterviewSchedules = () => {
    return axios.get(API_PATH);
};
