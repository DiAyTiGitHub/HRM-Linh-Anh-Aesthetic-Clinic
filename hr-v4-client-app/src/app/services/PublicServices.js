import axios from "axios";
import ConstantList from "../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/public/candidate";

export const saveCandidate = (searchObject) => {
    const url = `${API_PATH}/saveCandidate`;
    return axios.post(url, searchObject);
};
