import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/recruitment/";


export const pagingRecruitment = (dto) => axios.post(API_PATH + "search-by-page", dto); 

export const saveRecruitment = (dto) => axios.post(API_PATH + "save-or-update", dto) ;

export const getById = (id) => axios.get(API_PATH + 'get-by-id/' + id);

export const deleteRecruitment = (id) => axios.delete(API_PATH + 'remove/' + id);

export const deleteMultiple = (ids) => axios.post(API_PATH + 'remove-multiple' , ids);