import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/administrativeunit";
const API_PATH_2 = ConstantList.API_ENPOINT + "/api/hrAdministrativeUnit";

export const pagingAdministratives = (searchObject) => {
    var url = API_PATH_2 + "/searchByPage";
    return axios.post(url, searchObject);
};

export const getAdministrative = (id) => {
    let url = API_PATH + "/" + id;
    return axios.get(url);
};

export const createAdministrative = (obj) => {
    let url = API_PATH_2;
    return axios.post(url, obj);
};

export const editAdministrative = (obj) => {
    let url = API_PATH_2 + "/" + obj.id;
    return axios.put(url, obj);
};

export const deleteAdministrative = (id) => {
    let url = API_PATH_2 + "/" + id;
    return axios.delete(url);
};

export const checkCode = (dto) => {
    var url = API_PATH_2 + "/checkCode";
    return axios.post(url, {...dto});
};

export const getAllAdministratives = () => {
    var url = API_PATH + "/getAllAdministratives";
    return axios.get(url);
};

export const getRootUnit = (searchObject) => {
    var url = API_PATH + "/getRootUnit";
    return axios.post(url, searchObject);
};

export const getAllByLevel = (level) => {
    var url = API_PATH_2 + "/getAllByLevel/" + level;
    return axios.get(url);
};

export const getAllChildByParentId = (id) => {
    var url = API_PATH_2 + "/getAllChildByParentId/" + id;
    return axios.get(url);
};

export const pagingWards = (searchObject) => {
    var url = API_PATH_2 + "/search-ward-by-dto";
    return axios.post(url, searchObject);
};

export const importAdministrativeUnit = (file) => {
    let formData = new FormData();
    formData.append("uploadfile", file);
    let url = API_PATH_2 + "/import-excel-administrative-unit";
    return axios({
        url: url,
        headers: {
            "Content-Type": "multipart/form-data",
            "Accept": "*/*"
        },
        method: "POST",
        data: formData,
    });
}

export const downloadAdministrativeUnitTemplate = () => {
    return axios({
        method: "post",
        url: `${API_PATH_2}/export-excel-administrative-unit-template`,
        responseType: "blob",
    });
}
export const exportAdministrativeUnit = (search) => {
    return axios({
        method: "post",
        url: `${API_PATH_2}/export-excel-administrative-unit`,
        data: search, // Đảm bảo dữ liệu được gửi trong body
        responseType: "blob",
        headers: {
            "Content-Type": "application/json", // Chỉ định kiểu dữ liệu gửi lên
        },
    });
};
