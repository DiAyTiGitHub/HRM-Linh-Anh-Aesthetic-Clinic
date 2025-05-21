import { makeAutoObservable } from "mobx";
import {
  pagingPositionRole,
  getById,
  savePositionRole,
  deleteMultiple,
  deletePositionRole,
} from "./PositionRoleService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class PositionRoleStore {
  searchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
  };
  totalElements = 0;
  totalPages = 0;
  listPositionRole = [];
  openCreateEditPopup = false;
  selectedPositionRole = null;
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
    this.listPositionRole = [];
    this.openCreateEditPopup = false;
    this.selectedPositionRole = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  }

  handleSetSearchObject = (searchObject) => {
    this.searchObject = { ...searchObject };
  }

  pagingPositionRole = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ...this.searchObject,
        organizationId: loggedInStaff?.user?.org?.id
      };
      const data = await pagingPositionRole(payload);

      this.listPositionRole = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingPositionRole();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingPositionRole();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deletePositionRoles) => {
    this.listOnDelete = deletePositionRoles;
  };

  getById = async (positionRoleId) => {
    try {
      const { data } = await getById(positionRoleId);
      this.selectedPositionRole = data;
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

  handleDelete = (rankTitle) => {
    this.selectedPositionRole = { ...rankTitle };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  initialPositionRole = {
    id: null,
    name: null,
    shortName: null,
    otherName: null,
    description: null,
  }

  handleOpenCreateEdit = async (positionRoleId) => {
    try {
      if (positionRoleId) {
        const { data } = await getById(positionRoleId);
        this.selectedPositionRole = data;
      }
      else {
        this.selectedPositionRole = {
          ...this.initialPositionRole
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
      const { data } = await deletePositionRole(this?.selectedPositionRole?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingPositionRole();

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

      await this.pagingPositionRole();
      this.listOnDelete = [];

      this.handleClose();


    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  savePositionRole = async (rankTitle) => {
    try {
      const { data } = await savePositionRole(rankTitle);
      toast.success("Thông tin Nhóm quyền mặc định đã được lưu");
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
    }
  };
}
