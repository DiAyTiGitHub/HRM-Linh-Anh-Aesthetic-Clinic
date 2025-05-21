import { makeAutoObservable } from "mobx";
import {
  deleteLeaveRequest,
  deleteMultiple,
  downloadLeaveRequestTemplate,
  generatePaidLeaveExcel,
  generateUnpaidLeaveDocx,
  getById,
  importLeaveRequest,
  pagingLeaveRequest,
  saveLeaveRequest,
  updateApprovalStatus
} from "./LeaveRequestService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { LeaveRequest } from "./LeaveRequest";
import { getCurrentStaff } from "../profile/ProfileService";
import { saveAs } from "file-saver";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class LeaveRequestStore {
  intactSearchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    approvalStatus:0,
    chosenIds:[],
    staff:null,
    staffId:null,
    organization:null,
    organizationId:null,
    department:null,
    departmentId:null,
    positionTitle:null,
    positionTitleId:null,
    fromDate:null,
    toDate:null,
    salaryPeriod:null,
  };

  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listLeaveRequests = [];
  openCreateEditPopup = false;
  selectedLeaveRequest = new LeaveRequest ();
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listChosen = [];
  // handle for update recruitment request status
  openConfirmUpdateStatusPopup = false;
  onUpdateStatus = null;
  selectDepartment = null;
  openDepartmentPopup = false;
  openViewPopup = false;

  handleOpenView = async (leaveRequest) => {
    try {
      if (leaveRequest) {
        const {data} = await getById (leaveRequest?.id);
        this.selectedLeaveRequest = data;
      } else {
        const {data} = await getCurrentStaff ();
        this.selectedLeaveRequest = {
          ... new LeaveRequest (),
          requestStaff:data
        };
      }
      this.openViewPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  constructor () {
    makeAutoObservable (this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listLeaveRequests = [];
    this.openCreateEditPopup = false;
    this.selectedLeaveRequest = new LeaveRequest ();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatusPopup = false;
    this.openDepartmentPopup = false;
    this.onUpdateStatus = null;
  }
  handleExportRequestLeave = async (requestLeave) => {
    try {
      if (requestLeave?.id?.length > 0) {
        const totalDay = this.calculateTotalDaysByDateOnly (requestLeave?.fromDate, requestLeave?.toDate);

        const isUnpaidLeave = !requestLeave?.leaveType?.isPaid;

        if (totalDay > 14 && isUnpaidLeave) {
          await generateUnpaidLeaveDocx (requestLeave?.id);
        } else {
          await generatePaidLeaveExcel (requestLeave?.id);
        }

        toast.success ("Đã tải xuống yêu cầu nghỉ phép thành công");
      } else {
        toast.error (i18n.t ("toast.error"));
      }
    } catch (error) {
      console.error ("Error during export:", error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  calculateTotalDaysByDateOnly = (fromDateStr, toDateStr) => {
    if (!fromDateStr || !toDateStr) return 0;

    const fromDate = new Date (fromDateStr);
    const toDate = new Date (toDateStr);

    // Đặt giờ về 0 để chỉ tính ngày
    fromDate.setHours (0, 0, 0, 0);
    toDate.setHours (0, 0, 0, 0);

    const timeDiff = toDate - fromDate;
    const dayDiff = timeDiff / (1000 * 60 * 60 * 24); // tính số ngày

    return dayDiff + 1; // cộng thêm 1 để bao gồm cả ngày bắt đầu và kết thúc
  }

  handleChangePagingStatus = (approvalStatus) => {
    const so = {... this.searchObject, approvalStatus:approvalStatus};
    this.searchObject = so;
  }

  handleSetSearchObject = (searchObject) => {
    if (searchObject.staff == null) {
      searchObject.staffId = null;
    } else {
      searchObject.staffId = searchObject.staff.id;
    }
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    } else {
      searchObject.departmentId = searchObject.department.id;
    }

    if (searchObject.organization == null) {
      searchObject.organizationId = null;
    } else {
      searchObject.organizationId = searchObject.organization.id;
    }

    if (searchObject.positionTitle == null) {
      searchObject.positionTitleId = null;
    } else {
      searchObject.positionTitleId = searchObject.positionTitle.id;
    }
    this.searchObject = {... searchObject};
  }

  mapTabToStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab === 0) return null;
    // tab 1 => Chưa phê duyệt
    if (tab === 1) return LocalConstants.LeaveRequestApprovalStatus.NOT_APPROVED_YET.value;
    // tab 2 => Đã Phê duyệt
    if (tab === 2) return LocalConstants.LeaveRequestApprovalStatus.APPROVED.value;
    // tab 3 => Đã từ chối
    if (tab === 3) return LocalConstants.LeaveRequestApprovalStatus.NOT_APPROVED.value;

    return null;
  }

  pagingLeaveRequest = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser ();
      this.handleSetSearchObject (this.searchObject)
      const payload = {
        ... this.searchObject,
        approvalStatus:this.mapTabToStatus (this.searchObject.approvalStatus),
      };
      const data = await pagingLeaveRequest (payload);

      this.listLeaveRequests = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingLeaveRequest ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingLeaveRequest ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleSelectListDelete = (deleteLeaveRequests) => {
    this.listChosen = deleteLeaveRequests;
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmUpdateStatusPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.onUpdateStatus = null;
    this.listChosen = [];
    this.openViewPopup = false;
  };

  handleDelete = (leaveRequest) => {
    this.selectedLeaveRequest = {... leaveRequest};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (leaveRequest) => {
    try {
      if (leaveRequest) {
        const {data} = await getById (leaveRequest?.id);
        this.selectedLeaveRequest = data;
      } else {
        const {data} = await getCurrentStaff ();
        this.selectedLeaveRequest = {
          ... new LeaveRequest (),
          requestStaff:data
        };
      }
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteLeaveRequest (this?.selectedLeaveRequest?.id);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingLeaveRequest ();

      this.handleClose ();

    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listChosen?.length; i++) {
        deleteData.push (this?.listChosen[i]?.id);
      }

      await deleteMultiple (deleteData);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingLeaveRequest ();
      this.listChosen = [];

      this.handleClose ();


    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  saveLeaveRequest = async (position) => {
    try {
      const {data} = await saveLeaveRequest (position);
      toast.success ("Thông tin Yêu cầu đã được lưu");
      this.handleClose ();

      return data;
    } catch (error) {
      // console.error(error);
      // // toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
      // if (error.response.status == 409) {
      //     toast.error("Mã yêu cầu đã được sử dụng, vui lòng sử dụng mã yêu cầu khác", {
      //         autoClose: 5000,
      //         draggable: false,
      //         limit: 5,
      //     });
      // } else {
      //     toast.error(i18n.t("toast.error"));
      // }
      // throw new Error(i18n.t("toast.error"));
      // // return null;
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleRemoveActionItem = (onRemoveId) => {
    this.listChosen = this?.listChosen?.filter (item => item?.id !== onRemoveId);
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listChosen?.forEach (function (absenceRequest) {
      ids.push (absenceRequest?.id);
    });

    return ids;
  }

  handleOpenConfirmUpdateStatusPopup = (status) => {
    this.onUpdateStatus = status;
    this.openConfirmUpdateStatusPopup = true;
  }

  handleConfirmUpdateStatus = async () => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error ("Không có yêu cầu nào được chọn");
        return;
      }

      if (this.onUpdateStatus == null) {
        throw new Error ("On update status in invalid");
      }

      const {data} = await updateApprovalStatus (this.getSelectedIds ()[0], this.onUpdateStatus);
      if (!data) throw new Error ("");

      toast.success ("Cập nhật trạng thái thành công!");

      this.onUpdateStatus = null;

      this.handleClose ();
      await this.pagingLeaveRequest ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  }

  handelOpenDepartmentPopup = (value) => {
    this.openDepartmentPopup = value;
  }

  handleOpenConfirmDeletePopup = (value) => {
    this.openConfirmDeletePopup = value;
  };

  uploadFileExcel = async (event) => {
    const fileInput = event.target; // Lưu lại trước
    const file = fileInput.files[0];
    let message = "Nhập excel thất bại";

    try {
      await importLeaveRequest (file);
      toast.success ("Nhập excel thành công");
      this.pagingLeaveRequest ();
    } catch (error) {
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'string') {
          message = data;
        } else if (data.message) {
          message = data.message;
        }
      }
      toast.error (message);
    } finally {
      this.handleClose ();
      fileInput.value = null;
    }
  };


  handleDownloadLeaveRequestTemplate = async () => {
    try {
      const res = await downloadLeaveRequestTemplate ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu import yêu cầu nghỉ phép.xlsx");
      toast.success ("Đã tải mẫu import yêu cầu nghỉ phép thành công");
    } catch (error) {
      toast.error ("Tải mẫu import yêu cầu nghỉ phép thất bại");
      console.error (error);
    }
  };
}
