import axios from "axios";
import ConstantList from "../../appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/staff-labour-agreement";

export const pagingStaffLabourAgreement = (searchObject) => {
  const url = API_PATH + "/pagingLabourAgreement";
  return axios.post (url, searchObject);
};

export const getById = (id) => {
  const url = API_PATH + "/" + id;
  return axios.get (url);
};

export const exportHDLD = (id) => {
  const url = API_PATH + "/export/" + id;

  return axios.get (url, {responseType:'blob'})
      .then (response => {
        const blob = new Blob ([response.data], {type:'application/vnd.openxmlformats-officedocument.wordprocessingml.document'});
        let filename = "HopDongLaoDong.docx"; // Default filename
        const contentDisposition = response.headers['content-disposition'];
        if (contentDisposition) {
          const filenameMatch = contentDisposition.match (/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
          if (filenameMatch && filenameMatch[1]) {
            filename = filenameMatch[1].replace (/['"]/g, '').trim ();
          }
        }
        const link = document.createElement ('a');
        link.href = URL.createObjectURL (blob);
        link.download = filename;
        link.click ();
      })
      .catch (error => {
        console.error ("Error downloading file", error);
      });
};
export const viewerLabourAgreement = (id) => {
  const url = API_PATH + "/export/" + id;

  // Make the GET request
  return axios.get (url, {responseType:'blob'})
      .then (response => {
        // Create a blob URL for the response
        const blob = new Blob ([response.data], {type:'application/vnd.openxmlformats-officedocument.wordprocessingml.document'});
        return blob;
      })
      .catch (error => {
        console.error ("viewerLabourAgreement", error);
      });
};
export const saveStaffLabourAgreement = (obj) => {
  const url = API_PATH + "/save-labour-agreement";
  return axios.post (url, obj);
};

export const deleteMultiple = (ids) => {
  const url = API_PATH;
  return axios.post (url, ids);
};

export const deleteStaffLabourAgreement = (id) => {
  const url = API_PATH + "/" + id;
  return axios.delete (url);
};

export const getTotalHasSocialIns = (searchObject) => {
  var url = API_PATH + "/get-total-has-social-ins";
  return axios.post (url, searchObject);
};

export const exportHasInsuranceStaff = (searchObject) => {
  return axios ({
    method:"post",
    url:API_PATH + "/export-has-insurance-staff",
    data:searchObject,
    responseType:"blob",
  });
};

export const exportHICInfoToWord = (id) => {
  var url = API_PATH + `/export-hic-info-to-word/${id}`;
  return axios.get (url, {
    responseType:'blob'
  });
};

export const exportExcelStaffSIByType = (payload) => {
  return axios ({
    method:"post",
    url:API_PATH + "/export-excel-staff-social-insurance-by-type",
    data:payload,
    responseType:"blob",
  });
};

export const checkOverdueContract = (searchObject) => {
  const url = API_PATH + "/check-overdue-contract";
  return axios.post (url, searchObject);
};
export const getLastLabourAgreement = (staffId) => {
  const url = API_PATH + `/get-last-labour-agreement/${staffId}`;
  return axios.get (url);
};


export const importStaffLabourAgreement = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);

  let url = API_PATH + "/import-excel-staff-labour-agreement";
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


export const downloadStaffLabourAgreementTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH}/export-excel-staff-labour-agreement-template`,
    responseType:"blob",
  });
}

export const exportExcelStaffLabourAgreement = (searchObject) => {
  return axios ({
    method:"post",
    url:API_PATH + "/export-excel-staff-labour-agreement",
    data:searchObject,
    responseType:"blob",
  });
}