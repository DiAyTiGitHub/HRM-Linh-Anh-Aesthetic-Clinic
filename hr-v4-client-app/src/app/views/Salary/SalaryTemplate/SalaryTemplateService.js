import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-template";

export const pagingSalaryTemplates = (searchObject) => {
  const url = API_PATH + "/paging-salary-template";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const clonSalaryTemplate = (obj) => {
  const url = API_PATH + "/clon-salary-template";
  return axios.post(url, obj);
};

export const saveSalaryTemplate = (obj) => {
  const url = API_PATH + "/save-salary-template";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};

export const deleteSalaryTemplate = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const exportSalaryTemplate = (obj) => {
  return axios({
    url: API_PATH + "/export-excel",
    method: "POST",
    responseType: "blob",
    data: obj,
  });
}

export const downloadTemplateExcel = () => {
  return axios.post(API_PATH + "/download-template-excel", {},
    { responseType: "blob" }
  );
}
export const importExcel = (formData) => {
  return axios.post(API_PATH + "/import-excel", formData, {
    responseType: "blob"
  });
}
