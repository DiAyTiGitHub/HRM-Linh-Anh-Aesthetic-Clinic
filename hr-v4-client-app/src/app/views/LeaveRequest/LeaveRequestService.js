import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/leave-request/";
const API_PATH_S = ConstantList.API_ENPOINT + "/api/staff-work-schedule/";


export const pagingLeaveRequest = (dto) => axios.post (API_PATH + "search-by-page", dto)


export const getInitialLeaveRequestFilter = () => {
  let url = API_PATH + "initial-filter";
  return axios.get (url);
};

export const saveLeaveRequest = (dto) => axios.post (API_PATH + "save-or-update", dto)

export const getById = (id) => axios.get (API_PATH + 'get-by-id/' + id)

export const deleteLeaveRequest = (id) => axios.delete (API_PATH + 'delete/' + id)

export const deleteMultiple = (ids) => axios.post (API_PATH + 'delete-multiple', ids)

export const updateApprovalStatus = (leaveRequestId, status) => axios.get (API_PATH_S + `approve-leave-request/${leaveRequestId}/${status}`)

export const generateUnpaidLeaveDocx = (id) => {
  const url = `${API_PATH}unpaid-leave/${id}`;

  return axios.get (url, {responseType:'blob'})
      .then (response => {
        const blob = new Blob ([response.data], {
          type:'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
        });

        const downloadUrl = URL.createObjectURL (blob);
        const link = document.createElement ('a');
        link.href = downloadUrl;
        link.download = "DON_XIN_NGHI_PHEP_KHONG_LUONG.docx";
        document.body.appendChild (link); // Đảm bảo tương thích trình duyệt
        link.click ();
        document.body.removeChild (link);
        URL.revokeObjectURL (downloadUrl); // Giải phóng bộ nhớ
      })
      .catch (error => {
        console.error ("Lỗi khi tải file đơn xin nghỉ phép không lương:", error);
        alert ("Không thể tải file. Vui lòng thử lại sau.");
        throw error;
      });
};

export const generatePaidLeaveExcel = (id) => {
  const url = `${API_PATH}paid-leave/${id}`;

  return axios.get (url, {responseType:'blob'})
      .then (response => {
        const blob = new Blob ([response.data], {
          type:'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        });

        const downloadUrl = URL.createObjectURL (blob);
        const link = document.createElement ('a');
        link.href = downloadUrl;
        link.download = "DON_XIN_NGHI_PHEP_CO_LUONG.xlsx";
        document.body.appendChild (link);
        link.click ();
        document.body.removeChild (link);
        URL.revokeObjectURL (downloadUrl);
      })
      .catch (error => {
        console.error ("Lỗi khi tải file Excel đơn nghỉ phép có lương:", error);
        throw error;
      });
};

export const isExistLeaveRequestInPeriod = dto => axios.post (API_PATH + "is-exist-leave-request-in-period", dto)


export const importLeaveRequest = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);

  let url = API_PATH + "import-excel-leave-request-template";
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


export const downloadLeaveRequestTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH}export-excel-leave-request-template`,
    responseType:"blob",
  });
}
