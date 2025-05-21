import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/user-ext";
const API_PATH_UPLOAD = ConstantList.API_ENPOINT + "/api/hr/file";

export const getCurrentUser = () => {
    var url = API_PATH + "/get-current-user";
    return axios.get(url);
};

export const hasRoleManageHCNS = () => {
    var url = API_PATH + "/has-manager-hr-department";
    return axios.get(url);
};

export const getUserByStaffId = (staffId) => {
    var url = API_PATH + `/get-user-by-staff/${staffId}`;
    return axios.get(url);
};

export const getCurrentStaff = () => {
    var url = API_PATH + "/get-current-staff";
    return axios.get(url);
};

export const uploadImage = (object) => {
    var url = API_PATH_UPLOAD + "/image/" + object?.id;
    return axios.post(url, object.formData);
};
