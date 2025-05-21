import { makeAutoObservable } from "mobx";
import {
  pagingSalaryType,
  getById,
  saveSalaryType,
  deleteMultiple,
  deleteSalaryType,
} from "./SalaryTypeService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class SalaryTypeStore {
  searchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
  };
  totalElements = 0;
  totalPages = 0;
  listSalaryType = [];
  openCreateEditPopup = false;
  selectedSalaryType = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listOnDelete = [];

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = {
      pageIndex: 1,
      pageSize: 10,
      keyword: null,
    };
    this.totalElements = 0;
    this.totalPages = 0;
    this.listSalaryType = [];
    this.openCreateEditPopup = false;
    this.selectedSalaryType = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  }

  handleSetSearchObject = (searchObject) => {
    this.searchObject = { ...searchObject };
  }

  pagingSalaryType = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ...this.searchObject,
        organizationId: loggedInStaff?.user?.org?.id
      };
      const data = await pagingSalaryType(payload);

      this.listSalaryType = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingSalaryType();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingSalaryType();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteSalaryTypes) => {
    this.listOnDelete = deleteSalaryTypes;
  };

  getById = async (salaryTypeId) => {
    try {
      const { data } = await getById(salaryTypeId);
      this.selectedSalaryType = data;
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

  handleDelete = (salaryType) => {
    this.selectedSalaryType = { ...salaryType };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  initialSalaryType = {
    id: null,
    name: null,
    otherName: null,
    description: null,
  }

  handleOpenCreateEdit = async (salaryTypeId) => {
    try {
      if (salaryTypeId) {
        const { data } = await getById(salaryTypeId);
        this.selectedSalaryType = data;
      }
      else {
        this.selectedSalaryType = {
          ...this.initialSalaryType
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
      const { data } = await deleteSalaryType(this?.selectedSalaryType?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingSalaryType();

      this.handleClose();

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        deleteData.push(this?.listOnDelete[i]?.id);
      }

      // console.log("deleteData", deleteData)
      await deleteMultiple(deleteData);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingSalaryType();
      this.listOnDelete = [];

      this.handleClose();


    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveSalaryType = async (salaryType) => {
    try {
      const { data } = await saveSalaryType(salaryType);
      toast.success("Thông tin Nhóm dữ liệu lương đã được lưu");
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
    }
  };
}
