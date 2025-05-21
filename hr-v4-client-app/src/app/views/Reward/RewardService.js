import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/reward-form";

export const pagingRewards = (searchObject) => axios.post(API_PATH + "/paging", searchObject);

export const getReward = (id) => axios.get(API_PATH + "/" + id);

export const saveReward = (obj) => axios.post(API_PATH, obj);

export const deleteReward = (id) => axios.delete(API_PATH + "/" + id);

export const deleteList = (ids) => axios.delete(API_PATH ,{
  data:ids
});

export const checkCodeReward = (id, code) => {
  const config = { params: { id: id, code: code } };
  var url = API_PATH + "/check-code";
  return axios.get(url, config);
};
