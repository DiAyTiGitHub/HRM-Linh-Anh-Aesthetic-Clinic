import { makeAutoObservable } from "mobx";
import {
  pagingAllowance,
  getAllowanceById,
  saveAllowance,
  deleteAllowance,
  deleteMultiple
} from "./AllowanceService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SalaryItem } from "app/common/Model/Salary/SalaryItem";
import LocalConstants from "app/LocalConstants";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class AllowanceStore {
  intactSearchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
    allowanceType: null,
    allowanceTypeId: null
  };
  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  initialAllowance = {
    id: null,
    name: null,
    code: null,
    description: null,
    allowanceType: null,
    salaryItem: new SalaryItem(),
  };

  allowanceList = [];
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

  selectedAllowance = null;
  selectedAllowanceList = [];

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.allowanceList = [];
    this.openCreateEditPopup = false;
    this.selectedAllowance = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  };

  pagingAllowance = async () => {
    try {
      const payload = {
        ...this.searchObject
      };
      const data = await pagingAllowance(payload);
      this.allowanceList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;
    await this.pagingAllowance();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    await this.pagingAllowance();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleOpenCreateEdit = async (allowanceId) => {
    try {
      if (allowanceId) {
        const { data } = await getAllowanceById(allowanceId);
        this.selectedAllowance = data;
      } else {
        this.selectedAllowance = {
          ...this.initialAllowance,
          salaryItem: {
            ...this.initialAllowance.salaryItem,
            type: LocalConstants.SalaryItemType.ADDITION.value,
            calculationType: LocalConstants.SalaryItemCalculationType.AUTO_SYSTEM.value,
            valueType: LocalConstants.SalaryItemValueType.MONEY.value,
            isActive: true,
          },
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

  handleDelete = (allowance) => {
    this.selectedAllowance = { ...allowance };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteAllowance(this.selectedAllowance.id);
      toast.success(i18n.t("toast.delete_success"));
      await this.pagingAllowance();
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        deleteData.push(this?.listOnDelete[i]?.id);
      }
      await deleteMultiple(deleteData);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingAllowance();
      this.listOnDelete = [];

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleSelectListDelete = (allowances) => {
    this.listOnDelete = allowances;
  };

  saveAllowance = async (allowance) => {
    try {
      const { data } = await saveAllowance(allowance);
      toast.success("Thông tin phụ cấp đã được lưu");
      this.handleClose();
    } catch (error) {
      console.error(error);
      if (error.response.status == 409) {
        toast.error("Mã phụ cấp đã được sử dụng, vui lòng sử dụng mã phụ cấp khác", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      }
      else {
        toast.error(i18n.t("toast.error"));
      }
    }
  };

  getAllowance = async (id) => {
    if (id != null) {
      try {
        const { data } = await getAllowanceById(id);
        this.selectedAllowance = data;
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
    if (searchObject.allowanceType == null) {
      searchObject.allowanceTypeId = null;
    }
    else {
      searchObject.allowanceTypeId = searchObject.allowanceType.id;
    }
    this.searchObject = { ...searchObject };
  };

  // handleSelectListDelete = (deleteAllowance) => {
  //   this.listOnDelete = deleteAllowance;
  // };

}
