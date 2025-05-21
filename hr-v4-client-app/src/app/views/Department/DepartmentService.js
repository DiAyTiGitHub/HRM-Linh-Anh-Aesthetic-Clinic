import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/hRDepartment";
const API_PATH_CORE = ConstantList.API_ENPOINT + "/api/department";

export const pagingTreeDepartments = (searchObject) => {
    var url = API_PATH + "/paging-tree-department";
    return axios.post(url, searchObject);
};

export const pagingAllDepartments = (searchObject) => {
    var url = API_PATH + "/paging-department";
    return axios.post(url, searchObject);
};

export const pagingDepartmentHierarchy = (searchObject) => {
    var url = API_PATH + "/paging-department-hierarchy";
    return axios.post(url, searchObject);
};
export const checkValidParent = (searchObject) => {
    var url = API_PATH + "/check-valid-parent";
    return axios.post(url, searchObject);
};

export const pagingDepartmentPosition = (searchObject) => {
    var url = API_PATH + "/paging-department-position";
    return axios.post(url, searchObject);
};

export const getDepartment = (id) => {
    let url = API_PATH + "/" + id;
    return axios.get(url);
};

export const saveDepartment = (obj) => {
    let url = API_PATH;
    return axios.post(url, obj);
};

export const deleteDepartment = (id) => {
    let url = API_PATH + "/" + id;
    return axios.delete(url);
};

export const deleteMultiple = (ids) => {
    const url = API_PATH + "/delete-multiple";
    return axios.post(url, ids);
};

export const checkCode = (id, code) => {
    const param = { params: { id: id, code: code } };
    var url = API_PATH + "/checkCode";
    return axios.get(url, param);
};

export const getListDepartment = () => {
    var url = API_PATH + "/getListDepartment";
    return axios.get(url);
};

export const getTreeView = () => {
    var url = API_PATH_CORE + "/tree/1/10000000";
    return axios.get(url);
};

export const importDepartment = (file) => {
    let formData = new FormData();
    formData.append("uploadfile", file); // ✅ sửa key cho trùng với backend

    let url = API_PATH + "/import-excel-department";
    return axios({
        url: url,
        headers: {
            "Content-Type": "multipart/form-data",
            Accept: "*/*",
        },
        method: "POST",
        data: formData,
    });
};

export const downloadDepartmentTemplate = () => {
    return axios({
        method: "post",
        url: `${API_PATH}/export-excel-department-template`,
        responseType: "blob",
    });
};

export const exportExcelDepartmentData = (searchObject) => {
    return axios({
        method: "post",
        url: API_PATH + "/export-excel-hr-department",
        data: searchObject,
        responseType: "blob",
    });
};
export const autoGenCode = (configKey) => {
    let url = API_PATH + `/auto-gen-code/${configKey}`;
    return axios.get(url);
};