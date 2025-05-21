import { makeAutoObservable } from "mobx";
import {
  pagingOrganizationBranches,
  getById,
  saveOrganizationBranch,
  deleteMultiple,
  deleteOrganizationBranch,
} from "./OrganizationBranchService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { Staff } from "app/common/Model/Staff";
import { uploadImage } from "../HumanResourcesInformation/StaffService";
import { pagingCivilServantTypes } from "../CivilServantType/CivilServantTypeService";
import localStorageService from "app/services/localStorageService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class OrganizationBranchStore {
  searchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
  };
  totalElements = 0;
  totalPages = 0;
  listOrganizationBranches = [];
  openCreateEditPopup = false;
  selectedOrganization = null;
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
    this.listOrganizationBranches = [];
    this.openCreateEditPopup = false;
    this.selectedOrganization = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  }

  handleSetSearchObject = (searchObject) => {
    this.searchObject = { ...searchObject };
  }

  pagingOrganizationBranches = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      console.log("loggedInStaff", loggedInStaff)
      const payload = {
        ...this.searchObject,
        organizationId: loggedInStaff?.user?.org?.id
      };
      console.log("payload", payload)
      const data = await pagingOrganizationBranches(payload);

      this.listOrganizationBranches = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingOrganizationBranches();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingOrganizationBranches();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteOrganizationBranchs) => {
    this.listOnDelete = deleteOrganizationBranchs;
  };

  getById = async (organizationBranchId) => {
    try {
      const { data } = await getById(organizationBranchId);
      this.selectedOrganization = data;
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

  handleDelete = (organizationBranch) => {
    this.selectedOrganization = { ...organizationBranch };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  initialOrganizationBranch = {
    id: null,
    code: null,
    name: null,
    country: null,
    province: null,
    district: null,
    commune: null,
    address: null,
    phoneNumber: null,
    note: null
  }

  handleOpenCreateEdit = async (organizationBranchId) => {
    try {
      if (organizationBranchId) {
        const { data } = await getById(organizationBranchId);
        this.selectedOrganization = data;
      }
      else {
        this.selectedOrganization = {
          ...this.initialOrganizationBranch
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
      const { data } = await deleteOrganizationBranch(this?.selectedOrganization?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingOrganizationBranches();

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

      console.log("deleteData", deleteData)
      await deleteMultiple(deleteData);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingOrganizationBranches();
      this.listOnDelete = [];

      this.handleClose();


    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveOrganizationBranch = async (organizationBranch) => {
    try {
      const { data } = await saveOrganizationBranch(organizationBranch);
      toast.success("Thông tin địa điểm làm việc đã được lưu");
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };
}
