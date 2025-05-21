import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/department-type";

export const pagingDepartmentType = (searchObject) => {
  const url = API_PATH + "/pagingDepartmentType";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveDepartmentType = (obj) => {
  const url = API_PATH + "/saveDepartmentType";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH;
  return axios.post(url, ids);
};

export const deleteDepartmentType = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const importDepartmentType = (file) => {
  let formData = new FormData();
  formData.append("uploadfile", file);
  let url = API_PATH + "/import-excel-department-type";
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

export const downloadDepartmentTypeTemplate = () => {
  return axios({
    method: "post",
    url: `${API_PATH}/export-excel-department-type-template`,
    responseType: "blob",
  });
}

export const autoGenCode = (configKey) => {
  let url = API_PATH + `/auto-gen-code/${configKey}`;
  return axios.get(url);
};