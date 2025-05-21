import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/workplace";

export const pagingWorkplace = (searchObject) => {
  const url = API_PATH + "/search-by-page";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveWorkplace = (obj) => {
  const url = API_PATH + "/save-or-update";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH + "/delete-multiple";
  return axios.post(url, ids);
};

export const deleteWorkplace = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};

// export const importWorkplace = (file) => {
//   let formData = new FormData();
//   formData.append("uploadfile", file);
//   let url = API_PATH + "/import-excel-department-type";
//   return axios({
//     url: url,
//     headers: {
//       "Content-Type": "multipart/form-data",
//       "Accept": "*/*"
//     },
//     method: "POST",
//     data: formData,
//   });
// }

// export const downloadWorkplaceTemplate = () => {
//   return axios({
//     method: "post",
//     url: `${API_PATH}/export-excel-workplace-template`,
//     responseType: "blob",
//   });
// }