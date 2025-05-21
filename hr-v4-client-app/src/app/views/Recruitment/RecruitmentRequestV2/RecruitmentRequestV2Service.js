import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/recruitment-request/";

export const pagingRecruitmentRequest = (dto) => axios.post (API_PATH + "search-by-page", dto);

export const saveRecruitmentRequest = (dto) => axios.post (API_PATH + "save-or-update", dto);

export const getById = (id) => axios.get (API_PATH + "get-by-id/" + id);

export const deleteRecruitmentRequest = (id) => axios.delete (API_PATH + "remove/" + id);

export const deleteMultiple = (ids) => axios.post (API_PATH + "remove-multiple", ids);

export const updateRequestStatus = (dto) => axios.post (API_PATH + "update-request-status", dto);

export const personInCharge = (dto) => axios.post (API_PATH + "person-in-charge", dto);

export const exportWord = async (id) => {
  const url = `${API_PATH}export/${id}`;

  try {
    const response = await axios.get (url, {responseType:"blob"});

    const blob = new Blob ([response.data], {
      type:"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    });

    const link = document.createElement ("a");
    link.href = URL.createObjectURL (blob);
    link.download = "Phiếu đề xuất tuyển dụng.docx";
    link.click ();

    return true; // Trả về true nếu thành công
  } catch (error) {
    console.error ("Export Word Error:", error);
    throw error; // Ném lỗi để nơi gọi bắt và xử lý
  }
};

export const exportExcelByFilter = async (dto) => {
  const url = `${API_PATH}export-excel`;

  try {
    const response = await axios.post (url, dto, {responseType:"blob"});

    const blob = new Blob ([response.data], {
      type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });

    const link = document.createElement ("a");
    link.href = URL.createObjectURL (blob);
    link.download = "Danh sách yêu cầu tuyển dụng.xlsx";
    link.click ();

    return true; // Trả về true nếu thành công
  } catch (error) {
    console.error ("Export Excel Error:", error);
    throw error; // Ném lỗi để nơi gọi bắt và xử lý
  }
};


export const exportRecruitmentRequestReport = (searchObject) => {
  return axios ({
    method:"post",
    url:API_PATH + "export-recruitment-request-report",
    data:searchObject,
    responseType:"blob",
  });
}

export const pagingRecruitmentRequestReport = (obj) => {
  var url = API_PATH + "paging-recruitment-request-report";
  return axios.post (url, obj);
};

export const importRecruitmentRequest = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);

  let url = API_PATH + "import-excel-recruitment-request";
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


export const downloadRecruitmentRequestTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH}export-excel-recruitment-request-template`,
    responseType:"blob",
  });
}

export const checkNumberIsWithinHeadcount = (departmentId,positionTitleId,number) => {
  var url = API_PATH + `check-number-is-within-headcount/${departmentId}/${positionTitleId}/${number}`;
  return axios.get (url);
};

export const autoGenCode = (configKey) => {
  let url = API_PATH + `auto-gen-code/${configKey}`;
  return axios.get(url);
};
export const changeListStatus = (ids,status) => {
  let url = API_PATH + `change-status/${status}`;
  return axios.post(url,ids);
};

export const getRecruitmentRequestSummaries = (payload) => {
  let url = API_PATH + `recruitment-request-summary`;
  return axios.post(url,payload);
}