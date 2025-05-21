import { makeAutoObservable } from "mobx";
import {
  pagingSalaryConfig,
  getById,
  saveSalaryConfig,
  deleteMultiple,
  deleteSalaryConfig,
} from "./SalaryConfigService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class SalaryConfigStore {
  searchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
  };
  totalElements = 0;
  totalPages = 0;
  listSalaryConfig = [];
  openCreateEditPopup = false;
  selectedSalaryConfig = null;
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
    this.listSalaryConfig = [];
    this.openCreateEditPopup = false;
    this.selectedSalaryConfig = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  }

  handleSetSearchObject = (searchObject) => {
    this.searchObject = { ...searchObject };
  }

  pagingSalaryConfig = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ...this.searchObject,
        organizationId: loggedInStaff?.user?.org?.id
      };
      const data = await pagingSalaryConfig(payload);

      this.listSalaryConfig = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingSalaryConfig();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingSalaryConfig();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteSalaryConfigs) => {
    this.listOnDelete = deleteSalaryConfigs;
  };

  getById = async (salaryConfigId) => {
    try {
      const { data } = await getById(salaryConfigId);
      this.selectedSalaryConfig = data;
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

  handleDelete = (salaryConfig) => {
    this.selectedSalaryConfig = { ...salaryConfig };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  initialsalaryConfig = {
    id: null,
    name: null,
    shortName: null,
    otherName: null,
    level: null,
    subLevel: null,
    description: null,
  }

  handleOpenCreateEdit = async (salaryConfigId) => {
    try {
      if (salaryConfigId) {
        const { data } = await getById(salaryConfigId);
        this.selectedSalaryConfig = data;
      }
      else {
        this.selectedSalaryConfig = {
          ...this.initialsalaryConfig
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
      const { data } = await deleteSalaryConfig(this?.selectedSalaryConfig?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingSalaryConfig();

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

      await this.pagingSalaryConfig();
      this.listOnDelete = [];

      this.handleClose();


    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveSalaryConfig = async (salaryConfig) => {
    try {
      const { data } = await saveSalaryConfig(salaryConfig);
      toast.success("Thông tin Cấp bậc đã được lưu");
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
    }
  };

  getDisplayVoidedStatus = status => {
    if (!status) return "Được sử dụng";
    return "Không được sử dụng";
  }
}
