import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/shift-registration/";

export const pagingShiftRegistration = (dto) => axios.post(API_PATH + "search-by-page", dto)

export const saveShiftRegistration = (dto) => axios.post(API_PATH + "save-or-update", dto)

export const createStaffWorkSchedule = (dto) => axios.post(API_PATH + "create-staff-work-schedule", dto)

export const createStaffWorkSchedules = (dto) => axios.post(API_PATH + "create-staff-work-schedules", dto)

export const getById = (id) => axios.get(API_PATH + 'get-by-id/' + id)

export const deleteShiftRegistration = (id) => axios.delete(API_PATH + 'mark-delete/' + id)

export const deleteMultiple = (ids) => axios.post(API_PATH + 'remove-multiple', ids)

export const updateApprovalStatus = dto => axios.post(API_PATH + 'update-approval-status', dto)