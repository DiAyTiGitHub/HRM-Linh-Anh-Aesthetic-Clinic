import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/voucher";

// Lấy danh sách voucher theo phân trang và điều kiện tìm kiếm
export const pagingVoucher = (dto) => {
    const url = `${API_PATH}/paging`;
    return axios.post(url, dto);
};

// Lấy thông tin chi tiết của một voucher theo ID
export const getVoucherById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.get(url);
};

// Lưu mới hoặc cập nhật thông tin voucherv
export const saveVoucher = (dto) => {
    const url = `${API_PATH}/save`;
    return axios.post(url, dto);
};

// Xóa một voucher bằng ID
export const deleteVoucherById = (id) => {
    const url = `${API_PATH}/${id}`;
    return axios.delete(url);
};
export const exportVoucher = (dto) => {
    return axios({
        method: "post",
        url: API_PATH + "/export-excel",
        data: dto,
        responseType: "blob",
    });
}

export const deleteMultiple = (dto) => {
    const url = `${API_PATH}/deleteMultiple`;
    return axios.post(url, dto);
};
