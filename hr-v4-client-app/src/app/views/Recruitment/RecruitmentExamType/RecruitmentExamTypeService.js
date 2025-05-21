import axios from "axios";
import ConstantList from "../../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/recruitment-exam-type";

export const pagingRecruitmentExamType = (obj) =>
  axios.post(API_PATH + "/search-by-page", obj);

export const getByIdRecruitment = (id) =>
  axios.get(API_PATH + "/" + id);

export const createRecruitment = (obj) => axios.post(API_PATH, obj);

export const updateRecruitment = (obj, id) =>
  axios.put(API_PATH + "/" + id, obj);

export const deleteRecruitment = (id) =>
  axios.delete(API_PATH + "/" + id);

export const deleteMultipleRecruitment = (listId) =>
  axios.post(API_PATH + "/delete-multiple", listId);
