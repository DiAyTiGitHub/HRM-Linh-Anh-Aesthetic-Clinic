import { makeAutoObservable } from "mobx";
import {
  pagingStaffAllowance,
  getStaffAllowanceById,
  saveStaffAllowance,
  deleteStaffAllowance,
  getListStaffAllowanceByStaffId
} from "./StaffAllowanceService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class StaffAllowanceStore {
  intactSearchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
    allowance: null,
    allowanceId: null,
    staff: null,
    staffId: null
  };
  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  initialStaffAllowance = {
    id: null,
    name: null,
    code: null,
    description: null,
    staff: null,
    allowance: null,
    allowancePolicy: null,
    usingFormula: null,
    startDate: null,
    endDate: null
  };

  staffAllowanceList = [];
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  keyword = "";
  loadingInitial = false;

  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openCreateEditPopup = false;
  listOnDelete = [];
  isAdmin = false;
  selectedStaffAllowance = null;
  selectedStaffAllowanceList = [];
  listStaffAllowance = [];

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.staffAllowanceList = [];
    this.openCreateEditPopup = false;
    this.selectedStaffAllowance = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    this.listStaffAllowance = [];
  };

  pagingStaffAllowance = async () => {
    try {
      const payload = {
        ...this.searchObject
      };
      const data = await pagingStaffAllowance(payload);
      this.staffAllowanceList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;
    await this.pagingStaffAllowance();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    await this.pagingStaffAllowance();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleOpenCreateEdit = async (staffAllowanceId) => {
    try {
      if (staffAllowanceId) {
        const { data } = await getStaffAllowanceById(staffAllowanceId);
        this.selectedStaffAllowance = data;
      } else {
        this.selectedStaffAllowance = {
          ...this.initialStaffAllowance,
        };
      }
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;
  };

  handleDelete = (staffAllowance) => {
    this.selectedStaffAllowance = { ...staffAllowance };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteStaffAllowance(this.selectedStaffAllowance.id);
      toast.success(i18n.t("toast.delete_success"));
      await this.pagingStaffAllowance();
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  // handleConfirmDeleteList = async () => {
  //   try {
  //     const deleteData = [];

  //     for (let i = 0; i < this?.listOnDelete?.length; i++) {
  //       deleteData.push(this?.listOnDelete[i]?.id);
  //     }
  //     await deleteMultiple(deleteData);
  //     toast.success(i18n.t("toast.delete_success"));

  //     await this.pagingStaffAllowance();
  //     this.listOnDelete = [];

  //     this.handleClose();
  //   } catch (error) {
  //     console.error(error);
  //     toast.error(i18n.t("toast.error"));
  //   }
  // };

  handleSelectListDelete = (staffAllowance) => {
    this.listOnDelete = staffAllowance;
  };

  saveStaffAllowance = async (staffAllowance) => {
    try {
      const { data } = await saveStaffAllowance(staffAllowance);
      toast.success("Thông tin phụ cấp của nhân viên đã được lưu");
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
      throw new Error(i18n.t("toast.error"));
    }
  };

  getStaffAllowance = async (id) => {
    if (id != null) {
      try {
        const { data } = await getStaffAllowanceById(id);
        this.selectedStaffAllowance = data;
        this.openCreateEditPopup = true;
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.error"));
      }
    } else {
      this.handleSelectAllowance(null);
    }
  };

  handleSetSearchObject = (searchObject) => {
    if (searchObject.allowance == null) {
      searchObject.allowanceId = null;
    }
    else {
      searchObject.allowanceId = searchObject.allowance.id;
    }
    this.searchObject = { ...searchObject };
  };

  getListStaffAllowanceByStaffId = async (staffId) => {
    try {
      const { data } = await getListStaffAllowanceByStaffId(staffId);

      this.listStaffAllowance = data;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

}
