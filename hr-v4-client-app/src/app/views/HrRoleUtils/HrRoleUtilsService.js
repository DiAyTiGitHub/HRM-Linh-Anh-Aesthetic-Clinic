import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/hr-role-utils";

export const hasShiftAssignmentPermission = () => {
    let url = API_PATH + "/has-shift-assignment-permission";
    return axios.get(url);
};

export const hasShiftAssignmentPermissionStaff = (id) => {
    let url = API_PATH + "/has-shift-assignment-permission/" + id;
    return axios.get(url);
};