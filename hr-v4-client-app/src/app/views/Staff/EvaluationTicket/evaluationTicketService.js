import ConstantList from "../../../appConfig";
import axios from "axios";

const API_PATH = ConstantList.API_ENPOINT + "/api/evaluation-forms";

export const saveEvaluationForms = (obj) => {
    const url = API_PATH + "/save";
    return axios.post(url, obj);
};
export const pageEvaluationForms = (searchObj) => {
    const url = API_PATH + "/paging";
    return axios.post(url, searchObj);
};
export const getEvaluationFormsById = (id) => {
    const url = API_PATH + `/${id}`;
    return axios.get(url);
};
export const deleteEvaluationForm = (id) => {
    const url = API_PATH + `/${id}`;
    return axios.delete(url)
}
export const exportEvaluationFormWord = (id) => {
    const url = `${API_PATH}/export-word/${id}`;
    return axios.get(url, {
        responseType: 'blob' // để nhận dạng file Word từ server
    })
};
export const transferEvaluationForm = (id) => {
    const url = `${API_PATH}/transfer-evaluation-form/${id}`;
    return axios.get(url)
};
export const exportContractApprovalList = (obj) => {
    const url = `${API_PATH}/export-contract-approval-list`;
    return axios.post(url, obj, {
        responseType: 'blob' // để nhận dạng file Word từ server
    })
};
