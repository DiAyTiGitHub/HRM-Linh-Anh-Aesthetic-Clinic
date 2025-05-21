import axios from "axios";
import ConstantList from "app/appConfig";
//task history
const API_PATH_TS = ConstantList.API_ENPOINT + "/api/task-history";

export const pagingHistoryOfTask = (taskId, searchObj) => {
    const url = API_PATH_TS + "/task/" + taskId;
    return axios.post(url, searchObj);
};

export const getAllHistoryOfTask = (taskId) => {
    const url = API_PATH_TS + "/task/all/" + taskId;
    return axios.get(url);
};

export const createHistoryComment = (comment) => {
    const url = API_PATH_TS + "/comment";
    return axios.post(url, comment);
};