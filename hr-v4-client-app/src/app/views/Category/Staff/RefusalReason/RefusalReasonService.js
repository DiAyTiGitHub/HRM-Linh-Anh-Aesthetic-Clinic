import axios from "axios";
import ConstantList from "../../../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/refusal-reason";

export const pagingRefusalReason = (obj) =>
  axios.post(API_PATH + "/search-by-page", obj);
export const getByIdRefusalReason = (id) =>
  axios.get(API_PATH + "/get-by-id/" + id);

export const createRefusalReason = (obj) => axios.post(API_PATH + "/save-or-update", obj);
export const deleteRefusalReason = (id) =>
  axios.delete(API_PATH + "/remove/" + id);
export const deleteMultipleRefusalReason = (listId) =>
  axios.delete(API_PATH + "/remove-multiple",{
    data:listId
  });
