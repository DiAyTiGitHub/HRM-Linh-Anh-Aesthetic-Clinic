import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/candidate";

export const pagingCandidates = (searchObject) => {
  const url = API_PATH + "/pagingCandidates";
  return axios.post (url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get (url);
};

export const saveCandidate = (obj) => {
  const url = API_PATH + "/saveCandidate";
  return axios.post (url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH;
  return axios.post (url, ids);
};

export const resignMultiple = (ids) => {
  const url = API_PATH + "/resign-multiple";
  return axios.put (url, ids);
};

export const exportCandidateHDLD = (id) => {
  const url = API_PATH + "/export/" + id;

  // Make the GET request
  return axios.get (url, {responseType:'blob'})
      .then (response => {
        // Create a blob URL for the response
        const blob = new Blob ([response.data], {type:'application/vnd.openxmlformats-officedocument.wordprocessingml.document'});
        const link = document.createElement ('a');
        link.href = URL.createObjectURL (blob);
        link.download = "HopDongLaoDongThuViec.docx"; // You can customize the filename if needed
        link.click (); // Simulate a click to trigger the download
      })
      .catch (error => {
        console.error ("Error downloading file", error);
      });
};

export const deleteCandidate = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete (url);
};

export const updateApprovalStatus = (payload) => {
  const url = API_PATH + "/updateApprovalStatus";
  return axios.post (url, payload);
}

export const updateStatus = (payload) => {
  const url = API_PATH + "/update-status";
  return axios.post (url, payload);
}

export const checkDuplicateCandidate = (payload) => {
  const url = API_PATH + "/existing-candidates";
  return axios.post (url, payload);
}

export const getExistCandidateProfileOfStaff = (staffId) => {
  const url = API_PATH + "/exist-candidate-profile-of-staff/" + staffId;
  return axios.get (url);
}

export const importCandidate = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);

  let url = API_PATH + "/import-excel-candidate";
  return axios ({
    url:url,
    headers:{
      "Content-Type":"multipart/form-data",
      "Accept":"*/*"
    },
    method:"POST",
    data:formData,
    responseType:"blob",
  });
}


export const downloadCandidateTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH}/export-excel-candidate-template`,
    responseType:"blob",
  });
}

export const sendMail = (payload) => {
  const url = API_PATH + "/send-mail";
  return axios.post (url, payload);
}
export const sendMailEdit = (payload) => {
  const url = API_PATH + "/send-mail-edit";
  const formData = new FormData();

  console.log(payload);
  // Gửi JSON payload không có file (chỉ dữ liệu)
  formData.append('dto', new Blob([JSON.stringify({
    templateId: payload.templateId,
    candidate: payload.candidate
  })], { type: 'application/json' }));
  if (payload?.attachedFileTemplate?.length) {
    for (const file of payload?.attachedFileTemplate) {
      formData.append("attachedFileTemplate", file);
    }
  }

  // Gửi file đính kèm từng candidate, key theo index
  payload.candidate.forEach((candidate, i) => {
    (candidate.attachedFile || []).forEach((file) => {
      formData.append(i, file);
    });
  });

  return axios.post(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};



export const getPreviewMail = (payload) => {
  const url = API_PATH + "/get-preview-mail";
  return axios.post (url, payload);
}

export const approveCV = (payload) => {
  const url = API_PATH + "/approve-cv";
  return axios.post (url, payload);
}

export const exportExcelRecruitmentReports = async (searchObject) => {
  const url = `${API_PATH}/export-excel-recruitment-reports`;

  try {
    const response = await axios.post (url, searchObject, {
      responseType:'blob',
      headers:{
        'Content-Type':'application/json',
      },
    });

    return response.data;
  } catch (error) {
    console.error ('Export template failed:', error);
    throw error;
  }
};

export const autoGenCode = (configKey) => {
  let url = API_PATH + `/auto-gen-code/${configKey}`;
  return axios.get (url);
};