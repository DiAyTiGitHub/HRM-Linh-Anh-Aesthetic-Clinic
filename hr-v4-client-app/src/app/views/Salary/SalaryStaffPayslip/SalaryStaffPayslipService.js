import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-result-staff";

export const pagingSalaryStaffPayslip = (searchObject) => {
    var url = API_PATH + "/paging-salary-payslip";
    return axios.post(url, searchObject);
};

export const getInitialPayslipFilter = () => {
    let url = API_PATH + "/initial-filter";
    return axios.get(url);
};

export const pagingSalaryResultStaff = (searchObject) => {
    var url = API_PATH + "/paging-salary-result-staff";
    return axios.post(url, searchObject);
};

export const getTotalSalaryResultStaff = (searchObject) => {
    var url = API_PATH + "/get-total-salary-result-staff";
    return axios.post(url, searchObject);
};

export const getById = (id) => {
    let url = API_PATH + "/get-by-id/" + id;
    return axios.get(url);
};

export const generate = (id, staffSignatureId) => {
    let url = API_PATH + `/download-payslip`;

    return axios({
        method: "post",
        url: url,
        responseType: "blob",
        data: {
            salaryResultStaffId: id,
            staffSignatureId: staffSignatureId
        },
        headers: {
            "Content-Type": "application/json"
        }
    });
};


export const saveSalaryStaffPayslip = (obj) => {
    let url = API_PATH + "/save-salary-staff-payslip";
    return axios.post(url, obj);
};

export const deleteSalaryStaffPayslip = (id) => {
    let url = API_PATH + "/delete-salary-result-staff/" + id;
    return axios.delete(url);
};

export const deleteMultiple = (ids) => {
    const url = API_PATH + "/remove-multiple";
    return axios.post(url, ids);
};

export const updateApprovalStatus = (payload) => {
    const url = API_PATH + "/update-approval-status";
    return axios.post(url, payload);
}

export const updatePaidStatus = (payload) => {
    const url = API_PATH + "/update-paid-status";
    return axios.post(url, payload);
}

export const handleCalculateSalary = (dto) => {
    const url = API_PATH + "/calculate-salary-staff";
    return axios.post(url, dto);
}

export const updateSalaryStaff = (dto) => {
    const url = API_PATH + "/update-salary-staff";
    return axios.post(url, dto);
}

