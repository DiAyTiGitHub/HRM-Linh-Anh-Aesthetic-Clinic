import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/positionTitle";

export const pagingParentPositionTitle = (searchObject) => {
    var url = API_PATH + "/parent/searchByPage";
    return axios.post(url, searchObject);
};

export const pagingPositionTitle = (searchObject) => {
    var url = API_PATH + "/searchByPage";
    return axios.post(url, searchObject);
};

export const getPosition = (id) => {
    let url = API_PATH + "/" + id;
    return axios.get(url);
};

export const createPosition = (obj) => {
    let url = API_PATH;
    return axios.post(url, obj);
};

export const editPosition = (obj) => {
    let url = API_PATH + "/" + obj.id;
    return axios.put(url, obj);
};

export const deletePosition = (id) => {
    let url = API_PATH + "/" + id;
    return axios.delete(url);
};

export const getListPositionTitle = () => {
    var url = API_PATH + "/getListPositionTitle";
    return axios.get(url);
};

export const checkCode = (id, code) => {
    const config = {params: {id: id, code: code}};
    let url = API_PATH + "/checkCode";
    return axios.get(url, config);
};

export const savePositionTitle = (obj) => {
    let url = API_PATH;
    if (obj?.id) {
        url += ("/" + obj?.id);
        return axios.put(url, obj);
    }
    return axios.post(url, obj);
};


export const importPositionTitle = (file) => {
    let formData = new FormData();
    formData.append("uploadfile", file); // ✅ sửa key cho trùng với backend

    let url = API_PATH + "/import-excel-position-title";
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


export const downloadPositionTitleTemplate = () => {
    return axios({
        method: "post",
        url: `${API_PATH}/export-excel-position-title-template`,
        responseType: "blob",
    });
}


export const importGroupPositionTitle = (file) => {
    let formData = new FormData();
    formData.append("uploadfile", file); // ✅ sửa key cho trùng với backend

    let url = API_PATH + "/import-excel-group-position-title";
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


export const downloadGroupPositionTitleTemplate = () => {
    return axios({
        method: "post",
        url: `${API_PATH}/export-excel-group-position-title-template`,
        responseType: "blob",
    });
}

export const exportExcelPositionTitleData = (searchObject) => {
    return axios({
      method: "post",
      url: API_PATH + "/export-excel-position-title",
      data: searchObject,
      responseType: "blob",
    });
  }
export const autoGenCode = (configKey) => {
    let url = API_PATH + `/auto-gen-code/${configKey}`;
    return axios.get(url);
};
