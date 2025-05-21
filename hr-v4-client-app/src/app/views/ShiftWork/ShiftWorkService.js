import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/shiftwork";
const API_PATH_2 = ConstantList.API_ENPOINT + "/api/timePeriod";

export const pagingShiftWork = (searchObject) => {
  var url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getShiftWork = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get(url);
};

export const createShiftWork = (obj) => {
  let url = API_PATH;
  return axios.post(url, obj);
};

export const editShiftWork = (obj) => {
  let url = API_PATH + "/" + obj.id;
  return axios.put(url, obj);
};

export const saveShiftWork = (obj) => {
  if (obj.id) {
    return axios.put(API_PATH + "/" + obj.id, obj);
  }

  return axios.post(API_PATH, obj);
}

export const deleteShiftWork = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const checkCode = (id, code) => {
  const config = {params: {id: id, code: code}};
  var url = API_PATH + "/check-code";
  return axios.get(url, config);
};

export const checkCodeShiftWork = (id, code) => {
  const config = {params: {id: id, code: code}};
  var url = API_PATH + "/check-code";
  return axios.get(url, config);
};

export const pagingTimePeriod = (searchObject) => {
  var url = API_PATH_2 + "/getPage";
  return axios.post(url, searchObject);
};


export const importShiftWork = (file) => {
  let formData = new FormData();
  formData.append("uploadfile", file);

  let url = API_PATH + "/import-excel-shift-work";
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


export const downloadShiftWorkTemplate = () => {
  return axios({
    method: "post",
    url: `${API_PATH}/export-excel-shift-work-template`,
    responseType: "blob",
  });
}
