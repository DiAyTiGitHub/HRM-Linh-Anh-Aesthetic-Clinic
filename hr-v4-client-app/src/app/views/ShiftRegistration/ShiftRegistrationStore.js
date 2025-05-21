import { makeAutoObservable } from "mobx";
import {
  pagingShiftRegistration,
  getById,
  saveShiftRegistration,
  deleteMultiple,
  deleteShiftRegistration,
  updateApprovalStatus,
  createStaffWorkSchedule,
  createStaffWorkSchedules
} from "./ShiftRegistrationService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { ShiftRegistration } from "app/common/Model/ShiftRegistration/ShiftRegistration";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class ShiftRegistrationStore {
  intactSearchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
    status: 0,
    organization: null,
    organizationId: null,
    department: null,
    departmentId: null,
    positionTitle: null,
    positionTitleId: null,
    registerStaff: null,
    shiftWork: null,
    approvalStaff: null,
    fromDate: null,
    toDate: null,
  };

  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listShiftRegistrations = [];
  openCreateEditPopup = false;
  selectedShiftRegistration = new ShiftRegistration();
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listChosen = [];
  // handle for update recruitment request status
  openConfirmUpdateStatusPopup = false;
  onUpdateStatus = null;
  openFormShiftRegristration = false;

  constructor() {
    makeAutoObservable(this);
  }

  handleResetSelectedShiftRegistration = () => {
    this.selectedShiftRegistration = new ShiftRegistration();
  }

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listShiftRegistrations = [];
    this.openCreateEditPopup = false;
    this.selectedShiftRegistration = new ShiftRegistration();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatusPopup = false;
    this.onUpdateStatus = null;
  };

  handleOpenFormShiftRegristration = () => {
    this.openFormShiftRegristration = true;
  }

  handleChangePagingStatus = (status) => {
    const so = {...this.searchObject, status: status};
    this.searchObject = so;
  };

  handleSetSearchObject = (searchObject) => {
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    }
    else {
      searchObject.departmentId = searchObject.department.id;
    }
    if (searchObject.organization == null) {
      searchObject.organizationId = null;
    }
    else {
      searchObject.organizationId = searchObject.organization.id;
    }
    if (searchObject.position == null) {
      searchObject.positionId = null;
    }
    else {
      searchObject.positionId = searchObject.position.id;
    }

    if (searchObject.recruitmentRequest == null) {
      searchObject.recruitmentRequestId = null;
    }
    else {
      searchObject.recruitmentRequestId = searchObject.recruitmentRequest.id;
    }

    this.searchObject = {...searchObject};
  };

  mapTabToStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab == 0) return null;
    // tab 1 => Chưa duyệt
    if (tab == 1) return LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.value;
    // tab 2 => Đã duyệt
    if (tab == 2) return LocalConstants.ShiftRegistrationApprovalStatus.APPROVED.value;
    // tab 3 => Không duyệt
    if (tab == 3) return LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED.value;

    return null;
  };

  pagingShiftRegistration = async () => {
    try {
      // const loggedInStaff = localStorageService.getLoginUser();
      this.handleSetSearchObject(this.searchObject);
      
      const payload = {
        ...this.searchObject,
        status: this.mapTabToStatus(this.searchObject.status),
        // organizationId: loggedInStaff?.user?.org?.id,
      };
      const data = await pagingShiftRegistration(payload);

      this.listShiftRegistrations = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingShiftRegistration();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingShiftRegistration();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteShiftRegistrations) => {
    this.listChosen = deleteShiftRegistrations;
  };

  getById = async (id) => {
    try {
      if (!id) {
        this.selectedShiftRegistration = new ShiftRegistration();
        return;
      }

      const {data} = await getById(id);
      this.selectedShiftRegistration = data;
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmUpdateStatusPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.onUpdateStatus = null;
    this.listChosen = [];
    this.openFormShiftRegristration = false;
  };
  handleCloseConfirmDeletePopup = () => {
    this.openConfirmDeletePopup = false;
  };

  handleDelete = (position) => {
    this.selectedShiftRegistration = {...position};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (shiftRegistration) => {
    try {
      if (shiftRegistration) {
        const {data} = await getById(shiftRegistration?.id);
        this.selectedShiftRegistration = data;
      } else {
        this.selectedShiftRegistration = {
          ...new ShiftRegistration(),
        };
      }

      this.openCreateEditPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteShiftRegistration(this?.selectedShiftRegistration?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingShiftRegistration();

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listChosen?.length; i++) {
        deleteData.push(this?.listChosen[i]?.id);
      }

      await deleteMultiple(deleteData);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingShiftRegistration();
      this.listChosen = [];

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveShiftRegistration = async (ShiftRegistration) => {
    try {
      const {data} = await saveShiftRegistration(ShiftRegistration);
      toast.success("Thông tin đăng kí ca làm việc đã được lưu");
      this.handleClose();
      return data;
    } catch (error) {
      throw new Error(i18n.t("toast.error"));
    }
  };

  handleCreateWorkShift = async (ShiftRegistration) => {
    try {
      const {data} = await createStaffWorkSchedule(ShiftRegistration);
      toast.success("Đã tạo ca làm việc");
      this.handleClose();
      return data;
    } catch (error) {
      throw new Error(i18n.t("toast.error"));
    }
  };

  handleRemoveActionItem = (onRemoveId) => {
    this.listChosen = this?.listChosen?.filter((item) => item?.id !== onRemoveId);
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listChosen?.forEach(function (candidate) {
      ids.push(candidate?.id);
    });

    return ids;
  };

  handleOpenConfirmUpdateStatusPopup = (status) => {
    this.onUpdateStatus = status;
    this.openConfirmUpdateStatusPopup = true;
  };

  handleCreateWorkShifts = async (listShiftRegistrations) => {
    try {
      const {data} = await createStaffWorkSchedules(listShiftRegistrations);
      toast.success("Đã tạo ca làm việc");
      // this.handleClose();
      return data;
    } catch (error) {
      throw new Error(i18n.t("toast.error"));
    }
  };

  handleConfirmUpdateStatus = async () => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error("Không có đăng kí ca làm việc nào được chọn");
        return;
      }

      if (this.onUpdateStatus == null) {
        throw new Error("On update status in invalid");
      }
      const payload = {
        chosenIds: this.getSelectedIds(),
        status: this.onUpdateStatus,
      };

      const {data} = await updateApprovalStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cập nhật trạng thái thành công!");

      this.onUpdateStatus = null;

      this.handleClose();
      await this.pagingShiftRegistration();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };
}
