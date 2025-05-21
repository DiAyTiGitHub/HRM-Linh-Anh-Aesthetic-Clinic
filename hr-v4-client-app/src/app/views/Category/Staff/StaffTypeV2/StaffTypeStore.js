import { makeAutoObservable } from "mobx";
import {
  pagingStaffType,
  getById,
  saveStaffType,
  deleteMultiple,
  deleteStaffType,
} from "./StaffTypeService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { SalaryItem } from "app/common/Model/Salary/SalaryItem";
import LocalConstants from "app/LocalConstants";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class StaffTypeStore {
  searchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
  };
  totalElements = 0;
  totalPages = 0;
  listStaffType = [];
  openCreateEditPopup = false;
  selectedStaffType = null;
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
    this.listStaffType = [];
    this.openCreateEditPopup = false;
    this.selectedStaffType = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  }

  handleSetSearchObject = (searchObject) => {
    this.searchObject = { ...searchObject };
  }

  pagingStaffType = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ...this.searchObject,
      };
      const data = await pagingStaffType(payload);

      this.listStaffType = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingStaffType();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingStaffType();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteStaffTypes) => {
    this.listOnDelete = deleteStaffTypes;
  };

  getById = async (salaryItemId) => {
    try {
      const { data } = await getById(salaryItemId);
      this.selectedStaffType = data;
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
    this.listOnDelete = [];
  };

  handleDelete = (salaryItem) => {
    this.selectedStaffType = { ...salaryItem };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (salaryItemId) => {
    try {
      if (salaryItemId) {
        const { data } = await getById(salaryItemId);
        this.selectedStaffType = {
          ...JSON.parse(JSON.stringify(data))
        };
      }
      else {
        this.selectedStaffType = {
          ...new SalaryItem()
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
      const { data } = await deleteStaffType(this?.selectedStaffType?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingStaffType();

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

      await this.pagingStaffType();
      this.listOnDelete = [];

      this.handleClose();


    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveStaffType = async (salaryItem) => {
    try {
      const { data } = await saveStaffType(salaryItem);
      toast.success("Thông tin loại nhân viên đã được lưu");
      this.handleClose();

      return data;

    } catch (error) {
      console.error(error);
      if (error.response.status == 409) {
        toast.error("Mã loại nhân viên đã được sử dụng, vui lòng sử dụng mã khác", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      }
      else if (error.response.status == 304) {
        toast.warning("Thành phần mặc định của hệ thống không được phép chỉnh sửa", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      }
      else {
        toast.error(i18n.t("toast.error"));
      }

      throw new Error(i18n.t("toast.error"));
    }
  };

  getSalaryItemTypeName = (type) => {
    return LocalConstants.SalaryItemType.getListData().find(i => i.value == type)?.name;
  }

  getSalaryItemCalculationTypeName = (type) => {
    return LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == type)?.name;
  }

  getSalaryItemValueTypeName = (type) => {
    return LocalConstants.SalaryItemValueType.getListData().find(i => i.value == type)?.name;
  }

}
