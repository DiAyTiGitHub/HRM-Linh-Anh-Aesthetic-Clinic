import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/salary-result-staff";

export const saveSalaryResultStaff = (obj) => {
    const url = API_PATH + "/save-salary-result-staff";
    return axios.post(url, obj);
};

export const exportPdf = (payload) => {
    const url = API_PATH + "/export-pdf";
    return axios({
        method: "post",
        url: url,
        data: payload,
        responseType: "blob",
    });
};

export const exportCalculateSalaryStaffsToExcel = (payload) => {
    const url = API_PATH + "/export-calculate-salary-by-staffs";
    return axios({
        method: "post",
        url: url,
        data: payload,
        responseType: "blob",
    });
};

export const deleteSalaryResultStaff = (resultStaffId) => {
    const url = API_PATH + "/delete-salary-result-staff/" + resultStaffId;
    return axios.delete(url);
};

export const reCalculateRowByChangingCellValue = (obj) => {
    const url = API_PATH + "/recalculate-payslip-row";
    return axios.post(url, obj);
};


export const viewSalaryResult = (obj) => {
    const url = API_PATH + "/view-salary-staff";
    return axios.post(url, obj);
};   

export const calculateSalaryStaffs = (obj) => {
    const url = API_PATH + "/calculate-salary-staffs";
    return axios.post(url, obj);
};

export const calculateSalaryByStaffs = (obj) => {
    const url = API_PATH + "/calculate-salary-by-staffs";
    return axios.post(url, obj);
};


export const downloadSalaryResultStaffItemImportTemplate = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/download-salary-result-staff-item-import-template",
        data: searchObject,
        responseType: "blob",
    });
}

export const importSalaryResultStaffItemTemplate = (file) => {
    const url = API_PATH + "/import-salary-result-staff-item-template";
    let formData = new FormData();
    formData.append("uploadfile", file);
    const config = {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    };

    return axios.post(url, formData, config);
}


export const updateSalaryPayslipsPaidStatus = (searchObject) => {
    const url = API_PATH + "/update-paid-status";
    return axios.post(url, searchObject);
}