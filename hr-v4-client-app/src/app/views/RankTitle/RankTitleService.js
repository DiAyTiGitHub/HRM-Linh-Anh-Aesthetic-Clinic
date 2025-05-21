import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/rank-title";

export const pagingRankTitle = (searchObject) => {
  const url = API_PATH + "/pagingRankTitle";
  return axios.post(url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const saveRankTitle = (obj) => {
  const url = API_PATH + "/saveRankTitle";
  return axios.post(url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH;
  return axios.post(url, ids);
};

export const deleteRankTitle = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};

export const importRankTitle = (file) => {
  let formData = new FormData();
  formData.append("uploadfile", file); // ✅ sửa key cho trùng với backend

  let url = API_PATH + "/import-excel-rank-title";
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


export const downloadRankTitleTemplate = () => {
  return axios({
    method: "post",
    url: `${API_PATH}/export-excel-rank-title-template`,
    responseType: "blob",
  });
}
export const autoGenCode = (configKey) => {
  let url = API_PATH + `/auto-gen-code/${configKey}`;
  return axios.get(url);
};