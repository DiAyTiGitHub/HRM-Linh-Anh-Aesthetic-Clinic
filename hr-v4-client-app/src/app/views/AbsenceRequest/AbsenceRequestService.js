import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/absence-request/";


export const pagingAbsenceRequest = (dto) => axios.post(API_PATH + "search-by-page", dto)

export const saveAbsenceRequest = (dto) => axios.post(API_PATH + "save-or-update", dto)

export const getById = (id) => axios.get(API_PATH + 'get-by-id/' + id)

export const deleteAbsenceRequest = (id) => axios.delete(API_PATH + 'delete/' + id)

export const deleteMultiple = (ids) => axios.post(API_PATH + 'delete-multiple', ids)

export const updateApprovalStatus = dto => axios.post(API_PATH + "update-requests-approval-status", dto)