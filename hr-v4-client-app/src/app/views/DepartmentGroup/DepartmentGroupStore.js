import { makeAutoObservable } from "mobx";
import {
  pagingDepartmentGroup,
  getById,
  saveDepartmentGroup,
  deleteMultiple,
  deleteDepartmentGroup,
} from "./DepartmentGroupService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class DepartmentGroupStore {
  searchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
  };
  totalElements = 0;
  totalPages = 0;
  listDepartmentGroup = [];
  openCreateEditPopup = false;
  selectedDepartmentGroup = null;
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
    this.listDepartmentGroup = [];
    this.openCreateEditPopup = false;
    this.selectedDepartmentGroup = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  }

  handleSetSearchObject = (searchObject) => {
    this.searchObject = { ...searchObject };
  }

  pagingDepartmentGroup = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ...this.searchObject,
        organizationId: loggedInStaff?.user?.org?.id
      };
      const data = await pagingDepartmentGroup(payload);

      this.listDepartmentGroup = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingDepartmentGroup();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingDepartmentGroup();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteDepartmentGroups) => {
    this.listOnDelete = deleteDepartmentGroups;
  };

  getById = async (departmentGroupId) => {
    try {
      const { data } = await getById(departmentGroupId);
      this.selectedDepartmentGroup = data;
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

  handleDelete = (departmentGroup) => {
    this.selectedDepartmentGroup = { ...departmentGroup };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  initialDepartmentGroup = {
    id: null,
    name: null,
    shortName: null,
    otherName: null,
    sortNumber: null,
    description: null,
  }

  handleOpenCreateEdit = async (departmentGroupId) => {
    try {
      if (departmentGroupId) {
        const { data } = await getById(departmentGroupId);
        this.selectedDepartmentGroup = data;
      }
      else {
        this.selectedDepartmentGroup = {
          ...this.initialDepartmentGroup
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
      const { data } = await deleteDepartmentGroup(this?.selectedDepartmentGroup?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingDepartmentGroup();

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

      await this.pagingDepartmentGroup();
      this.listOnDelete = [];

      this.handleClose();


    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveDepartmentGroup = async (departmentGroup) => {
    try {
      const { data } = await saveDepartmentGroup(departmentGroup);
      toast.success("Thông tin Nhóm phòng ban đã được lưu");
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
    }
  };
}
