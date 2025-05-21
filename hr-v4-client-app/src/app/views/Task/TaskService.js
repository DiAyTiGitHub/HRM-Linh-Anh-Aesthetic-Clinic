import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/hr-task";

export const pagingTask = (object) => {
  let url = API_PATH + "/paging-list-task";
  return axios.post(url, object);
};

export const getAllTask = (object) => {
  let url = API_PATH + "/search";
  return axios.post(url, object);
};

export const saveTask = (object) => {
  if (object.id) {
    const url = API_PATH + "/" + object.id;
    return axios.put(url, object);
  } 
  const url = API_PATH;
  return axios.post(url, object);
};

export const getTask = (idTask) => {
  let url = API_PATH + "/" + idTask;
  return axios.get(url);
};

export const deleteTask = (idTask) => {
  let url = API_PATH + "/" + idTask;
  return axios.delete(url);
};

export const updateStatus = (object) => {
  let url = API_PATH + "/update-status";
  return axios.put(url, object);
};

export const getListByLimitTask = (object) => { 
  let url = API_PATH + "/get-list-by-limit";
  return axios.post(url, object);
};








//export excel: task by filter
const EXCEL_PATH =
  ConstantList.API_ENPOINT + "/api/fileDownload/task-by-filter";

export const exportExcelTaskByFilter = (searchObject) => {
  return axios({
    method: "post",
    url: EXCEL_PATH,
    data: searchObject,
    responseType: "blob",
  });
}