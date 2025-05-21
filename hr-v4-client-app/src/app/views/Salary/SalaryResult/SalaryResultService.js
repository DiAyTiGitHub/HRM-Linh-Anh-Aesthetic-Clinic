import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/salary-result";

export const pagingSalaryResult = (searchObject) => {
    const url = API_PATH + "/paging-salary-result";
    return axios.post(url, searchObject);
};

export const getById = (id) => {
    const url = API_PATH + "/get-by-id/" + id;
    return axios.get(url);
};

export const getBasicInfoById = (id) => {
    const url = API_PATH + "/get-basic-info/" + id;
    return axios.get(url);
};

export const getSalaryResultBoard = (id) => {
    const url = API_PATH + "/get-salary-result-board/" + id;
    return axios.get(url);
};

export const getListTemplateItem = (id) => {
    const url = API_PATH + "/get-list-template-item-by-salary-result/" + id;
    return axios.get(url);
};


export const getExcelSalaryResultBoard = (id) => {
    return axios({
        method: "post",
        url: API_PATH + "/handle-excel/" + id,
        responseType: "blob",
    });
};

export const saveSalaryResult = (obj) => {
    const url = API_PATH + "/save-salary-result";
    return axios.post(url, obj);
};

export const saveBoardConfig = (obj) => {
    const url = API_PATH + "/save-board-config-of-salary-result";
    return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
    const url = API_PATH + "/remove-multiple";
    return axios.post(url, ids);
};

export const deleteSalaryResult = (id) => {
    const url = API_PATH + "/remove/" + id;
    return axios.delete(url);
};

export const lockPayroll = (id) => {
    const url = API_PATH + "/lock-payroll/" + id;
    return axios.post(url);
};

export const unlockPayroll = (id) => {
    const url = API_PATH + "/unlock-payroll/" + id;
    return axios.post(url);
};

export const createSalaryBoardByPeriodAndTemplate = (obj) => {
    const url = API_PATH + "/create-salary-board";
    return axios.post(url, obj);
};


export const updateSalaryResultStatusNotApprovedYet = (listId) => {
    const url = API_PATH + "/not-approved-yet";
    return axios.post(url, listId);
}

export const updateSalaryResultStatusApproved = (listId) => {
    const url = API_PATH + "/approved";
    return axios.post(url, listId);
}

export const updateSalaryResultStatusNotApproved = (listId) => {
    const url = API_PATH + "/not-approved";
    return axios.post(url, listId);
}

export const updateSalaryResultStatusLocked = (listId) => {
    const url = API_PATH + "/locked";
    return axios.post(url, listId);
}

export const exportFileImportSalaryValueByFilter = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/export-file-import-salary-value-by-filter",
        data: searchObject,
        responseType: "blob",
    });
}


export const importFileSalaryValueByFilter = (file, payload) => {
    let formData = new FormData();
    formData.append("uploadfile", file);
    formData.append("data", JSON.stringify(payload)); // Truyền object DTO dưới dạng JSON

    let url = API_PATH + "/import-file-salary-value-by-filter";
    return axios({
        url: url,
        method: "POST",
        headers: {
            "Content-Type": "multipart/form-data",
            "Accept": "*/*"
        },
        data: formData,
    });
}

export const searchSalaryResultBoard = (obj) => {
    const url = API_PATH + "/search-salary-result-board/" + obj?.salaryResultId;
    return axios.post(url, obj);
}

// Số lượng phiếu lương có thể thuộc bảng lương nhưng chưa được tổng hợp
export const hasAnyOrphanedPayslips = (id) => {
    const url = API_PATH + "/has-any-orphaned-payslips/" + id;
    return axios.get(url);
};

// Lấy danh sách phiếu lương có thể tổng hợp vào bảng lương
export const getAllOrphanedPayslips = (id) => {
    const url = API_PATH + "/orphaned-payslips/" + id;
    return axios.get(url);
};

export const mergeOrphansToSalaryBoard = (listId) => {
    const url = API_PATH + "/merge-orphaned-payslips";
    return axios.post(url, listId);
}

export const recalculationSalaryBoard = (id) => {
    const url = API_PATH + "/recalculate-salary-board/" + id;
    return axios.post(url);
};


// Xuất excel  bảng lương hoa hồng
export const exportExcelCommissionPayroll = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/export-excel-commission-payroll",
        data: searchObject,
        responseType: "blob",
    });
}