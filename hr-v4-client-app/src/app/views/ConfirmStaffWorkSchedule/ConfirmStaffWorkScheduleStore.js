import { makeAutoObservable } from "mobx";
import {
  pagingOvertimeRequest,
  saveOvertimeRequest,
  deleteMultiple,
  deleteOvertimeRequest,
  updateApprovalStatus
} from "./ConfirmStaffWorkScheduleService";

import {
  getStaffWorkSchedule,
} from "app/views/StaffWorkScheduleV2/StaffWorkScheduleService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { getCurrentStaff } from "../profile/ProfileService";
import { StaffWorkSchedule } from "app/common/Model/Timekeeping/StaffWorkSchedule";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class ConfirmStaffWorkScheduleStore {
  intactSearchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    approvalStatus:0,
    staffId:null,
    staff:null,
    department:null,
    departmentId:null,
    organization:null,
    organizationId:null,
    fromDate:new Date (),
    toDate:new Date (),
    salaryPeriod:null,
    positionTitle:null,
    positionTitleId:null,
  };

  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listConfirmStaffWorkSchedule = [];
  openCreateEditPopup = false;
  selectedOvertimeRequest = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listChosen = [];
  // handle for update recruitment request status
  openConfirmUpdateStatusPopup = false;
  onUpdateStatus = null;

  selectDepartment = null;
  openDepartmentPopup = false;
  openViewPopup = false;

  handleOpenView = async (obj) => {
    try {
      if (obj) {
        const {data} = await getStaffWorkSchedule (obj?.id);
        this.selectedOvertimeRequest = data;
      } else {
        let {data} = await getCurrentStaff ();
        this.selectedOvertimeRequest = {
          ... new StaffWorkSchedule (),
          staff:data
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
    this.listConfirmStaffWorkSchedule = [];
    this.openCreateEditPopup = false;
    this.selectedOvertimeRequest = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatusPopup = false;
    this.openDepartmentPopup = false;
    this.onUpdateStatus = null;
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

    this.searchObject = {... searchObject};
  }

  mapTabToStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab == 0) return null;
    // tab 1 => Chưa phê duyệt
    if (tab == 1) return LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED_YET.value;
    // tab 2 => Đã Phê duyệt
    if (tab == 2) return LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value;
    // tab 3 => Đã từ chối
    if (tab == 3) return LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED.value;

    return null;
  }

  pagingOvertimeRequest = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser ();
      const payload = {
        ... this.searchObject,
        approvalStatus:this.mapTabToStatus (this.searchObject.approvalStatus),
        // organizationId: loggedInStaff?.user?.org?.id
      };
      const data = await pagingOvertimeRequest (payload);

      this.listConfirmStaffWorkSchedule = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingOvertimeRequest ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingOvertimeRequest ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleSelectListDelete = (deleteOvertimeRequests) => {
    this.listChosen = deleteOvertimeRequests;
  };

  getById = async (overtimeRequestId) => {
    try {
      if (!overtimeRequestId) {
        this.selectedOvertimeRequest = new StaffWorkSchedule ();

        return;
      }
      const {data} = await getStaffWorkSchedule (overtimeRequestId);
      this.selectedOvertimeRequest = data;
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
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


  handleDelete = (position) => {
    this.selectedOvertimeRequest = {... position};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (obj) => {
    try {
      if (obj) {
        const {data} = await getStaffWorkSchedule (obj?.id);
        this.selectedOvertimeRequest = data;
      } else {
        let {data} = await getCurrentStaff ();
        this.selectedOvertimeRequest = {
          ... new StaffWorkSchedule (),
          staff:data
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
      const {data} = await deleteOvertimeRequest (this?.selectedOvertimeRequest?.id);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingOvertimeRequest ();

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

      await this.pagingOvertimeRequest ();
      this.listChosen = [];

      this.handleClose ();


    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  saveOvertimeRequest = async (position) => {
    try {
      const {data} = await saveOvertimeRequest (position);
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
    this?.listChosen?.forEach (function (objobj) {
      ids.push (objobj?.id);
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
      const payload = {
        chosenIds:this.getSelectedIds (),
        approvalStatus:this.onUpdateStatus,
      };

      const {data} = await updateApprovalStatus (payload);
      if (!data) throw new Error ("");

      toast.success ("Cập nhật trạng thái thành công!");

      this.onUpdateStatus = null;

      this.handleClose ();
      await this.pagingOvertimeRequest ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  }

  handelOpenDepartmentPopup = (value) => {
    this.openDepartmentPopup = value;
  }

  selectedStaff = null;

  handleSelectStaff (timesheetStaff) {
    this.selectedStaff = timesheetStaff;
  }

  getCurrentStaff = async () => {
    try {
      let data = await getCurrentStaff ();

      this.handleSelectStaff (data?.data);

      this.searchObject = {
        ... this.searchObject,
        staff:data.data
      }

    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.get_fail"));
      this.handleSelectStaff (null);
    }
  };
}
