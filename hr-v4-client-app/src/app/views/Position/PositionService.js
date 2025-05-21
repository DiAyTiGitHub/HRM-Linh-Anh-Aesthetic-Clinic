import ConstantList from "app/appConfig";
import axios from "axios";
const API_PATH = ConstantList.API_ENPOINT + "/api/position";

export const pagingPosition = (searchObject) => {
  const url = API_PATH + "/pagingPosition";
  return axios.post(url, searchObject);
};

export const pagingPositionByCurrentUser = (searchObject) => {
  const url = API_PATH + "/pagingPosition";
  return axios.post(url, searchObject);
};

export const countNumberOfPositionInDepartmentWithPositionTitle = (dto) => {
  const url = API_PATH + "/count-position-with-position-title";
  return axios.post(url, dto);
};

export const getResourceItemById = (id) => {
  const url = API_PATH + "/get-resource-plan-item/" + id;
  return axios.get(url);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get(url);
};

export const getByStaffId = (id) => {
  const url = API_PATH + "/get-by-staff/" + id;
  return axios.get(url);
};


export const savePosition = (obj) => {
  const url = API_PATH + "/savePosition";
  return axios.post(url, obj);
};

export const fetchTransferPosition = (obj) => {
  const url = API_PATH + "/transfer-position";
  return axios.post(url, obj);
};

export const fetchTransferStaff = (obj) => {
  const url = API_PATH + "/transfer-staff";
  return axios.post(url, obj);
};




export const deleteMultiple = (ids) => {
  const url = API_PATH + "/deleteMultiple";
  return axios.post(url, ids);
};

export const deletePosition = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete(url);
};


export const importPosition = (file) => {
  let formData = new FormData();
  formData.append("uploadfile", file); // ✅ sửa key cho trùng với backend

  let url = API_PATH + "/import-excel-position";
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
export const importPositionRelationShip = (file) => {
  let formData = new FormData();
  formData.append("uploadfile", file); // ✅ sửa key cho trùng với backend

  let url = API_PATH + "/import-relationship";
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
export const downloadPositionTemplate = () => {
  return axios({
    method: "post",
    url: `${API_PATH}/export-excel-position-template`,
    responseType: "blob",
  });
}
export const downloadPositionRelationshipTemplate = () => {
  return axios({
    method: "post",
    url: `${API_PATH}/export-excel-position-relationship-template`,
    responseType: "blob",
  });
}


export const removeStaffFromPosition = (id) => {
  const url = API_PATH + "/remove-staff-from-position/" + id;
  return axios.delete(url);
};

export const assignPositionsForStaff = (searchObject) => {
  const url = API_PATH + "/assign-positions-for-staff";
  return axios.post(url, searchObject);
};

export const exportExcelPositionData = (searchObject) => {
  return axios({
    method: "post",
    url: API_PATH + "/export-excel-position",
    data: searchObject,
    responseType: "blob",
  });
}

export const autoGenCode = (configKey) => {
  const url = API_PATH + `/auto-gen-code/${configKey}`;
  return axios.get(url);
}
