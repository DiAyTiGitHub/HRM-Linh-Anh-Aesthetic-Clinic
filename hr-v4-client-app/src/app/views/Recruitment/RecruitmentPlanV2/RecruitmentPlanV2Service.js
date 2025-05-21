import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/recruitment-plan/";
const API_PATH_RECRUITMENT_ROUND = ConstantList.API_ENPOINT + "/api/recruitment-round/";

export const pagingRecruitmentPlan = (dto) => axios.post (API_PATH + "search-by-page", dto)

export const saveRecruitmentPlan = (dto) => axios.post (API_PATH + "save-or-update", dto)

export const getById = (id) => axios.get (API_PATH + 'get-by-id/' + id)

export const deleteRecruitmentPlan = (id) => axios.delete (API_PATH + 'remove/' + id)

export const deleteMultiple = (ids) => axios.post (API_PATH + 'remove-multiple', ids)

export const updatePlanStatus = dto => axios.post (API_PATH + 'update-plan-status', dto)


export const importRecruitmentPlan = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);

  let url = API_PATH + "import-excel-recruitment-plan-template";
  return axios ({
    url:url,
    headers:{
      "Content-Type":"multipart/form-data",
      "Accept":"*/*"
    },
    method:"POST",
    data:formData,
  });
}


export const downloadRecruitmentPlanTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH}export-excel-recruitment-plan-template`,
    responseType:"blob",
  });
}

export const importRecruitmentRound = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);

  let url = API_PATH_RECRUITMENT_ROUND + "import-excel-recruitment-round-template";
  return axios ({
    url:url,
    headers:{
      "Content-Type":"multipart/form-data",
      "Accept":"*/*"
    },
    method:"POST",
    data:formData,
  });
}


export const downloadRecruitmentRoundTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH_RECRUITMENT_ROUND}export-excel-recruitment-round-template`,
    responseType:"blob",
  });
}
export const autoGenCode = (configKey) => {
  let url = API_PATH + `auto-gen-code/${configKey}`;
  return axios.get(url);
};