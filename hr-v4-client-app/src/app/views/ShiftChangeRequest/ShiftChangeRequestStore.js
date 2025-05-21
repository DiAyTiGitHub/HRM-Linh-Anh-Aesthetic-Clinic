import { makeAutoObservable } from "mobx";
import {
  pagingShiftChangeRequest,
  getById,
  saveShiftChangeRequest,
  deleteMultiple,
  deleteShiftChangeRequest,
  updateApprovalStatus,
} from "./ShiftChangeRequestService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { ShiftChangeRequest } from "./ShiftChangeRequest";
import { SalaryItem } from "../../common/Model/Salary/SalaryItem";
import { values } from "lodash";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class ShiftChangeRequestStore {
  intactSearchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    approvalStatus:0,
    chosenIds:[],
    department:null,
    organization:null,
    positionTitle:null,
    departmentId:null,
    organizationId:null,
    positionTitleId:null,
    staff:null,
    staffId:null,
    fromDate:null,
    toDate:null,
    salaryPeriod:null,
  };

  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listShiftChangeRequests = [];
  openCreateEditPopup = false;
  selectedShiftChangeRequest = new ShiftChangeRequest ();
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listChosen = [];
  // handle for update recruitment request status
  openConfirmUpdateStatusPopup = false;
  onUpdateStatus = null;

  selectDepartment = null;
  openDepartmentPopup = false;
  openViewPopup = false;

  handleOpenView = async (absenceRequest) => {
    try {
      if (absenceRequest) {
        const {data} = await getById (absenceRequest?.id);
        this.selectedShiftChangeRequest = data;
      } else {
        this.selectedShiftChangeRequest = {
          ... new ShiftChangeRequest (),
        };
      }

      this.openViewPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleSetSelectedShiftChangeRequest = (value) => {
    this.selectedShiftChangeRequest = {... value};
  }

  constructor () {
    makeAutoObservable (this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listShiftChangeRequests = [];
    this.openCreateEditPopup = false;
    this.selectedShiftChangeRequest = new ShiftChangeRequest ();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatusPopup = false;
    this.openDepartmentPopup = false;
    this.onUpdateStatus = null;
    this.openViewPopup = false;
  };

  handleChangePagingStatus = (approvalStatus) => {
    const so = {... this.searchObject, approvalStatus:approvalStatus};
    this.searchObject = so;
  };

  handleSetSearchObject = (searchObject) => {
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

    if (searchObject.staff == null) {
      searchObject.staffId = null;
    } else {
      searchObject.staffId = searchObject.staff.id;
    }

    this.searchObject = {... searchObject};
  };

  mapTabToStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab === 0) return null;
    // tab 1 => Chưa phê duyệt
    if (tab === 1) return LocalConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED_YET.value;
    // tab 2 => Đã Phê duyệt
    if (tab === 2) return LocalConstants.ShiftChangeRequestApprovalStatus.APPROVED.value;
    // tab 3 => Đã từ chối
    if (tab === 3) return LocalConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED.value;

    return null;
  };

  pagingShiftChangeRequest = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser ();
      this.handleSetSearchObject (this.searchObject);
      const payload = {
        ... this.searchObject,
        approvalStatus:this.mapTabToStatus (this.searchObject.approvalStatus),
        // organizationId:loggedInStaff?.user?.org?.id ,
      };
      console.log ("payload", payload);
      const data = await pagingShiftChangeRequest (payload);

      this.listShiftChangeRequests = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingShiftChangeRequest ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingShiftChangeRequest ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleSelectListDelete = (deleteShiftChangeRequests) => {
    this.listChosen = deleteShiftChangeRequests;
  };

  getById = async (absenceRequestId) => {
    try {
      if (!absenceRequestId) {
        this.selectedShiftChangeRequest = new ShiftChangeRequest ();
        return;
      }
      const {data} = await getById (absenceRequestId);
      this.selectedShiftChangeRequest = data;
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
    this.selectedShiftChangeRequest = {... position};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (absenceRequest) => {
    try {
      if (absenceRequest) {
        const {data} = await getById (absenceRequest?.id);
        this.selectedShiftChangeRequest = data;
      } else {
        this.selectedShiftChangeRequest = {
          ... new ShiftChangeRequest (),
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
      const {data} = await deleteShiftChangeRequest (this?.selectedShiftChangeRequest?.id);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingShiftChangeRequest ();

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

      await this.pagingShiftChangeRequest ();
      this.listChosen = [];

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  saveShiftChangeRequest = async (position) => {
    try {
      const {data} = await saveShiftChangeRequest (position);
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
    this.listChosen = this?.listChosen?.filter ((item) => item?.id !== onRemoveId);
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listChosen?.forEach (function (absenceRequest) {
      ids.push (absenceRequest?.id);
    });

    return ids;
  };

  handleOpenConfirmUpdateStatusPopup = (status) => {
    this.onUpdateStatus = status;
    this.openConfirmUpdateStatusPopup = true;
    // Filter out approved requests from listChosen
    this.listChosen = this.listChosen.filter (
        (item) => item.approvalStatus !== LocalConstants.ShiftChangeRequestApprovalStatus.APPROVED.value
    );
  };

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
      await this.pagingShiftChangeRequest ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handelOpenDepartmentPopup = (value) => {
    this.openDepartmentPopup = value;
  };
}
