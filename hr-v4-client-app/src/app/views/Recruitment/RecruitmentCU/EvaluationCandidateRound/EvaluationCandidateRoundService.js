import ConstantList from "app/appConfig";
import axios from "axios";

const API_PATH = ConstantList.API_ENPOINT + "/api/evaluation-candidate-round";

export const getByCandidateRoundId = (id) => {
    var url = API_PATH + `/get-by-round/${id}`;
    return axios.get(url);
};
export const saveEvaluationCandidate = (values) => {
    var url = API_PATH + `/save`;
    return axios.post(url, values);
};
