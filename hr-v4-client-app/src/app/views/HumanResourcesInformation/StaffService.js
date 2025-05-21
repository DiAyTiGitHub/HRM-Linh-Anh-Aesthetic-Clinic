import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff";
const API_PATH_2 = ConstantList.API_ENPOINT + "/api/timesheetdetail";
const API_PATH_STAFF_HIERARCHY = ConstantList.API_ENPOINT + "/api/staff-hierarchy";

const EXCEL_PATH =
    ConstantList.API_ENPOINT + "/api/fileDownload/exportExcel_staff";

export const getInitialStaffFilter = (id) => {
    let url = API_PATH + "/initial-filter";
    return axios.get(url);
};

export const pagingStaff = (searchObject) => {
    var url = API_PATH + "/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingStaffLabourManagement = (searchObject) => {
    var url = API_PATH + "/paging-staff-labour-management";
    return axios.post(url, searchObject);
};

export const dismissStaffPositions = (staff) => {
    const url = `${API_PATH}/dismiss-positions`;
    return axios.post(url, staff);
};

export const generateNewStaffCode = (payload) => {
    let url = API_PATH + "/generate-new-staff-code";
    return axios.post(url, payload);
};

export const getStaff = (id) => {
    let url = API_PATH + "/" + id;
    return axios.get(url);
};

export const createStaff = (obj) => {
    let url = API_PATH;
    return axios.post(url, obj);
};

export const editStaff = (obj) => {
    console.log(obj);
    let url = API_PATH + "/" + obj?.id;
    return axios.put(url, obj);
};

export const deleteStaff = (id) => {
    let url = API_PATH + "/" + id;
    return axios.delete(url);
};

export const uploadImage = (formData) => {
    var url = ConstantList.API_ENPOINT + "/api/hr/file/image";
    return axios.post(url, formData);
};

export const exportToExcel = (searchObject) => {
    return axios({
        method: "post",
        url: EXCEL_PATH,
        data: searchObject,
        responseType: "blob",
    });
}

export const exportExcelListStaff = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/export-excel-list-staff",
        data: searchObject,
        responseType: "blob",
    });
}

export const importExcelListStaff = (file) => {
    let formData = new FormData();
    formData.append("uploadfile", file);

    let url = API_PATH + "/import-excel-list-staff";
    return axios({
        url: url,
        headers: {
            "Content-Type": "multipart/form-data",
            "Accept": "*/*"
        },
        method: "POST",
        data: formData,
        responseType: "blob", // <--- Add this to handle Excel response
    });
};

export const importExcelListNewStaff = (file) => {
    let formData = new FormData();
    formData.append("uploadfile", file);

    let url = API_PATH + "/import-excel-list-new-staff";
    return axios({
        url: url,
        headers: {
            "Content-Type": "multipart/form-data",
            "Accept": "*/*"
        },
        method: "POST",
        data: formData,
        responseType: "blob", // <--- Add this to handle Excel response
    });
};




export const exportStaffLaborReportExcel = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/excel-report-on-labor-use-situation",
        data: searchObject,
        responseType: "blob",
    });
}

export const exportExcelListHrIntroduceCost = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/export-excel-introduce-cost",
        data: searchObject,
        responseType: "blob",
    });
}

export const checkIdNumber = (obj) => {
    let url = API_PATH + "/checkIdNumber";
    return axios.post(url, obj);
};

export const getAllStaff = () => {
    var url = API_PATH + "/all";
    return axios.get(url);
};
export const pagingTimeSheetDetail = (obj) => {
    let url = API_PATH_2 + "/search-by-page";
    return axios.post(url, obj);
};

export const saveStaffWithoutAccount = staff => {
    const url = API_PATH + "/save-staff-without-account";
    return axios.post(url, staff);
}

export const checkStaffTaxCode = (staff) => {
    const url = API_PATH + "/validate-tax-code";
    return axios.post(url, staff);
}

export const checkStaffSocialInsuranceNumber = (staff) => {
    const url = API_PATH + "/validate-social-insurance-number";
    return axios.post(url, staff);
}

export const checkStaffHealthInsuranceNumber = (staff) => {
    const url = API_PATH + "/validate-health-insurance-number";
    return axios.post(url, staff);
}

export const createUsersForStaff = (listStaff, allowCreate = false) => {
    const url = `${API_PATH}/create-users-for-staff?allowCreate=${allowCreate}`;
    return axios.post(url, listStaff);
};

export const findStaffsHaveBirthDayByMonth = month => {
    const url = API_PATH + "/birthDay-by-month";
    const params = {
        params: {
            month: month
        }
    }
    return axios.get(url, params);
}

export const findBySalaryTemplatePeriod = dto => {
    const url = API_PATH + "/find-by-salary-template-period";
    return axios.post(url, dto);
}

export const enableExternalIpTimekeeping = (obj) => {
    let url = API_PATH + "/enable-external-ip-timekeeping";
    return axios.post(url, obj);
};

export const disableExternalIpTimekeeping = (obj) => {
    let url = API_PATH + "/disable-external-ip-timekeeping";
    return axios.post(url, obj);
};

export const exportExcelTemplateImportStaff = () => {
    return axios({
        method: "post",
        url: `${API_PATH}/export-excel-template-import-staff`,
        responseType: "blob",
    });
}

export const pagingLowerLevelStaff = (obj) => {
    var url = API_PATH_STAFF_HIERARCHY + "/paging-lower-level-staff";
    return axios.post(url, obj);
};

export const pagingHasPermissionDepartments = (obj) => {
    var url = API_PATH_STAFF_HIERARCHY + "/paging-has-permission-departments";
    return axios.post(url, obj);
};

export const getLastLabourAgreement = (staff) => {
    let url = API_PATH + `/get-last-labour-agreement/${staff}`;
    return axios.get(url);
}


export const generateFixScheduleForChosenStaffs = (obj) => {
    var url = API_PATH + "/generate-fix-schedules";
    return axios.post(url, obj);
};

export const calculateRemaininAnnualLeave = (searchObject) => {
    var url = API_PATH + "/calculate-remainin-annual-leave";
    return axios.post(url, searchObject);
};

export const exportLaborManagementBook = (searchObject) => {
    return axios({
        method: "post",
        url: `${API_PATH}/export-labor-management-book`,
        data: searchObject,
        responseType: "blob",
    });
}

export const exportStaffLabourUtilReport = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/excel-report-on-labor-use-situation",
        data: searchObject,
        responseType: "blob",
    });
}

export const pagingStaffLabourUtilReport = (obj) => {
    var url = API_PATH + "/paging-staff-labour-util-report";
    return axios.post(url, obj);
};