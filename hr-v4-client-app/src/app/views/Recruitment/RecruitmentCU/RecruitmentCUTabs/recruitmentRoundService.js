import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/candidate-recruitment-round";

export const getByIdRecruitment = (planId, roundId) => axios.get(API_PATH + `/list-candidate/${planId}/${roundId}`);

export const getByIdRecruitmentRound = (roundId) => axios.get(API_PATH + `/get-by-round-id/${roundId}`);