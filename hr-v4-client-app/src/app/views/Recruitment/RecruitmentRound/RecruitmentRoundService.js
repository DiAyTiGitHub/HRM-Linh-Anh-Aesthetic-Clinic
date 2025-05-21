import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/recruitment-round";


export const pagingRecruitmentRound = (dto) => axios.post(API_PATH + "/paging" , dto);
