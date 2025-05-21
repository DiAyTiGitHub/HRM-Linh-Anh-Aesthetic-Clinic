import axios from "axios";
import ConstantList from "../../appConfig";
import {getFormData} from "../../LocalFunction";

const API_PATH = ConstantList.API_ENPOINT + "/api/hr-organization";

export const pagingOrganization = (searchObject) => {
    const url = API_PATH + "/search-by-page";
    return axios.post(url, searchObject);
};

export const pagingAllOrg = (searchObject) => {
    const url = API_PATH + "/paging-organizations";
    return axios.post(url, searchObject);
};


export const getById = (id) => {
    const url = API_PATH + "/" + id;
    return axios.get(url);
};

export const saveOrganization = (obj) => {
    const url = API_PATH + "/save";
    return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
    const url = API_PATH + "/delete-multiple";
    return axios.post(url, ids);
};

export const deleteOrganization = (id) => {
    const url = API_PATH + "/" + id;
    return axios.delete(url);
};

export const getCurrentOrganizationOfCurrentUser = () => {
    const url = API_PATH + "/currentOrg";
    return axios.get(url);
};

const API_PATH_UPLOAD = ConstantList.API_ENPOINT + "/api/hr/file";
export const uploadImage = (object) => {
    var url = API_PATH_UPLOAD + "/image";
    return axios.post(url, object.formData);
};

export const importOrganization = (file) => {
    let formData = new FormData();
    formData.append("uploadfile", file); // ✅ sửa key cho trùng với backend

    let url = API_PATH + "/import-excel-organization";
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


export const downloadOrganizationTemplate = () => {
    return axios({
        method: "post",
        url: `${API_PATH}/export-excel-organization-template`,
        responseType: "blob",
    });
}

export const exportExcelOrgData = (searchObject) => {
    return axios({
      method: "post",
      url: API_PATH + "/export-excel-organization",
      data: searchObject,
      responseType: "blob",
    });
  }

export const autoGenCode = (configKey) => {
    let url = API_PATH + `/auto-gen-code/${configKey}`;
    return axios.get(url);
};