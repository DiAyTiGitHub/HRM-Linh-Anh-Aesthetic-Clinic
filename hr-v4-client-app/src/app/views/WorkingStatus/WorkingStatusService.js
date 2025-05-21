import axios from "axios";
import Config from "../../appConfig";

const API_PATH = Config.API_ENPOINT + "/api/working-status";

export const pagingWorkingStatus = (searchObject) => axios.post(API_PATH + "/paging", searchObject);

export const getWorkingStatus = (id) => axios.get(API_PATH + "/" + id);

export const saveWorkingStatus = (obj) => axios.post(API_PATH, obj);

export const deleteWorkingStatus = (id) => axios.delete(API_PATH + "/" + id);

export const checkCodeWorkingStatus = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/check-code";
  return axios.get(url, config);
};

export const getWorkingStatusWithTotalNOTasksByProject= (searchObj) =>{
  const url = API_PATH + "/total-nums-of-task-in-project";
  return axios.post(url, searchObj);
}

