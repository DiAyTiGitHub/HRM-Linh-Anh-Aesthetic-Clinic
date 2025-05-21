import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/candidate-recruitment-round";


export const pagingCandidateRecruitmentRound = (searchObject) => {
    var url = API_PATH + "/paging-candidate-recruitment-round";
    return axios.post(url, searchObject);
};

export const getById = (id) => {
    let url = API_PATH + "/" + id;
    return axios.get(url);
};

export const saveCandidateRecruitmentRound = (obj) => {
    let url = API_PATH + "/save-candidate-recruitment-round";
    return axios.post(url, obj);
};

export const deleteCandidateRecruitmentRound = (id) => {
    let url = API_PATH + "/" + id;
    return axios.delete(url);
};

export const deleteMultiple = (ids) => {
    const url = API_PATH + "/delete-multiple";
    return axios.post(url, ids);
};

// update candidate's result in recruitment round
export const updateRecruitmentRoundResult = (searchObject) => {
    const url = API_PATH + "/update-candidate-recruitment-round-result";
    return axios.post(url, searchObject);
}

// Phân bổ/Sắp xếp ứng viên cho các vòng tuyển dụng tiếp theo
export const moveToNextRecruitmentRound = (searchObject) => {
    const url = API_PATH + "/move-to-next-recruitment-round";
    return axios.post(url, searchObject);
}

// Phân bổ/Sắp xếp ứng viên cho vòng tuyển dụng đầu tiên
export const distributeCandidatesForFirstRecruitmentRound = (searchObject) => {
    const url = API_PATH + "/distribute-candidates-for-first-recruitment-round";
    return axios.post(url, searchObject);
}
export const doActionAssignment = (crrId, status) => {
    const url = API_PATH + `/do-action-assignment/${crrId}/${status}`;
    return axios.get(url);
}
export const passToNextRound = (crrId) => {
    const url = API_PATH + `/pass-to-next-round/${crrId}`;
    return axios.get(url);
}

export const passListToNextRound = (searchObject) => {
    const url = API_PATH + `/pass-list-to-next-round`;
    return axios.post(url, searchObject);
}

export const rejectCandidateRound = (crrId) => {
    const url = API_PATH + `/reject-candidate-round/${crrId}`;
    return axios.get(url);
}

export const getCandidateRoundByCandidateId = (candidateId) => {
    const url = API_PATH + "/get-round-by-candidate/" + candidateId;
    return axios.get(url);
}