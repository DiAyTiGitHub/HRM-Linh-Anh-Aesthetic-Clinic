import { makeAutoObservable } from "mobx";
import {
  pagingAllowancePolicy,
  getAllowancePolicyById,
  saveAllowancePolicy,
  deleteAllowancePolicy
} from "./AllowancePolicyService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import OrganizationIndex from "../Organization/OrganizationIndex";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class AllowancePolicyStore {
  intactSearchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
    allowance: null,
    allowanceId: null
  };
  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  initialAllowancePolicy = {
    id: null,
    name: null,
    code: null,
    description: null,
    organization: null,
    department: null,
    position: null,
    allowance: null,
    formula: null,
    startDate: null,
    endDate: null
  };

  allowancePolicyList = [];
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
  selectedAllowancePolicy = null;
  selectedAllowancePolicyList = [];

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.allowancePolicyList = [];
    this.openCreateEditPopup = false;
    this.selectedAllowancePolicy = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  };

  pagingAllowancePolicy = async () => {
    try {
      const payload = {
        ...this.searchObject
      };
      console.log("payload", payload);
      const data = await pagingAllowancePolicy(payload);
      this.allowancePolicyList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;
    await this.pagingAllowancePolicy();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    await this.pagingAllowancePolicy();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };
  handleOpenCreateEdit = async (allowancePolicyId) => {
    try {
      if (allowancePolicyId) {
        const { data } = await getAllowancePolicyById(allowancePolicyId);
        this.selectedAllowancePolicy = {
          ...data,
          staffs: data.staffs || [], // Gán giá trị mặc định cho staffs nếu không có
        };
      } else {
        this.selectedAllowancePolicy = {
          ...this.initialAllowancePolicy,
          staffs: [], // Gán giá trị mặc định cho staffs nếu không có allowancePolicyId
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

  handleDelete = (allowancePolicy) => {
    this.selectedAllowancePolicy = { ...allowancePolicy };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteAllowancePolicy(this.selectedAllowancePolicy.id);
      toast.success(i18n.t("toast.delete_success"));
      await this.pagingAllowancePolicy();
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

  //     await this.pagingAllowancePolicy();
  //     this.listOnDelete = [];

  //     this.handleClose();
  //   } catch (error) {
  //     console.error(error);
  //     toast.error(i18n.t("toast.error"));
  //   }
  // };

  handleSelectListDelete = (allowancePolicy) => {
    this.listOnDelete = allowancePolicy;
  };

  handleSelectAllowancePolicy = (allowancePolicy) => {
    this.handleSelectAllowancePolicy = allowancePolicy;
  };

  saveAllowancePolicy = async (allowancePolicy) => {
    try {
      const { data } = await saveAllowancePolicy(allowancePolicy);
      toast.success("Thông tin chính sách phụ cấp đã được lưu");
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
      throw new Error(i18n.t("toast.error"));
    }
  };

  getAllowancePolicy = async (id) => {
    if (id != null) {
      try {
        const { data } = await getAllowancePolicyById(id);
        this.selectedAllowancePolicy = data;
        this.openCreateEditPopup = true;
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.error"));
      }
    } else {
      this.handleSelectAllowancePolicy(null);
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

}
