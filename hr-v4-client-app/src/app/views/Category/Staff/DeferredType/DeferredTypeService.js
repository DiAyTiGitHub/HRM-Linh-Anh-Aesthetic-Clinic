import axios from "axios";
import ConstantList from "../../../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/deferred-type";

export const pagingDiscipline = (obj) =>
  axios.post(API_PATH + "/search-by-page", obj);
export const getByIdDiscipline = (id) =>
  axios.get(API_PATH + "/get-by-id/" + id);

export const createDiscipline = (obj) => axios.post(API_PATH + "/save-or-update", obj);
export const deleteDiscipline = (id) =>
  axios.delete(API_PATH + "/remove/" + id);
export const deleteMultipleDiscipline = (listId) =>
  axios.delete(API_PATH + "/remove-multiple",{
    data:listId
  });
