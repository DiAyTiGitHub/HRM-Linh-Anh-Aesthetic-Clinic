import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/salary-item";

export const pagingSalaryItem = (searchObject) => {
  var url = API_PATH + "/paging-salary-item";
  return axios.post (url, searchObject);
};

export const getById = (id) => {
  let url = API_PATH + "/" + id;
  return axios.get (url);
};

export const getByStaffId = (staffId) => {
  let url = API_PATH + "/staff/" + staffId;
  return axios.get (url);
};

export const saveSalaryItem = (obj) => {
  let url = API_PATH;
  return axios.post (url, obj);
};

export const deleteSalaryItem = (id) => {
  let url = API_PATH + "/" + id;
  return axios.delete (url);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post (url, ids);
};

export const importSalaryItem = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);

  let url = API_PATH + "/import-excel-salary-item-template";
  return axios ({
    url:url,
    headers:{
      "Content-Type":"multipart/form-data",
      "Accept":"*/*"
    },
    method:"POST",
    data:formData,
  });
}

export const downloadSalaryItemTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH}/export-excel-salary-item-template`,
    responseType:"blob",
  });
}
