import { SalaryItem } from "app/common/Model/Salary/SalaryItem";
import LocalConstants from "app/LocalConstants";
import localStorageService from "app/services/localStorageService";
import i18n from "i18n";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {
  deleteMultiple,
  deleteSalaryItem,
  downloadSalaryItemTemplate,
  getById,
  getByStaffId,
  importSalaryItem,
  pagingSalaryItem,
  saveSalaryItem,
} from "./SalaryItemV2Service";
import { saveAs } from "file-saver";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class SalaryItemStore {
  searchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
  };
  totalElements = 0;
  totalPages = 0;
  listSalaryItem = [];
  openCreateEditPopup = false;
  selectedSalaryItem = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listOnDelete = [];
  isUser = false;
  openViewPopup = false;

  handleOpenView = async (salaryItemId) => {
    try {
      if (salaryItemId) {
        const { data } = await getById(salaryItemId);
        this.selectedSalaryItem = {
          ...JSON.parse(JSON.stringify(data)),
        };
      } else {
        this.selectedSalaryItem = {
          ... new SalaryItem(),
        };
      }
      this.openViewPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

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
    this.listSalaryItem = [];
    this.openCreateEditPopup = false;
    this.selectedSalaryItem = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    this.openViewPopup = false;
  };

  handleSetSearchObject = (searchObject) => {
    this.searchObject = { ...searchObject };
  };

  pagingSalaryItem = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ... this.searchObject,
        organizationId: loggedInStaff?.user?.org?.id,
      };
      const data = await pagingSalaryItem(payload);

      this.listSalaryItem = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  getListByStaffId = async (staffId) => {
    try {
      const { data } = await getByStaffId(staffId);

      this.listSalaryItem = data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingSalaryItem();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingSalaryItem();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteSalaryItems) => {
    this.listOnDelete = deleteSalaryItems;
  };

  getById = async (salaryItemId) => {
    try {
      const { data } = await getById(salaryItemId);
      this.selectedSalaryItem = data;
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
    this.openViewPopup = false;
  };

  handleSelectedSalaryItem = (salaryItem) => {
    this.selectedSalaryItem = salaryItem;
  };

  handleDelete = (salaryItem) => {
    this.selectedSalaryItem = { ...salaryItem };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (salaryItemId) => {
    try {
      if (salaryItemId) {
        const { data } = await getById(salaryItemId);
        this.selectedSalaryItem = {
          ...JSON.parse(JSON.stringify(data)),
        };
      } else {
        this.selectedSalaryItem = {
          ... new SalaryItem(),
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
      const { data } = await deleteSalaryItem(this?.selectedSalaryItem?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingSalaryItem();

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

      await this.pagingSalaryItem();
      this.listOnDelete = [];

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveSalaryItem = async (salaryItem) => {
    try {
      const { data } = await saveSalaryItem(salaryItem);
      toast.success("Thông tin Thành phần lương đã được lưu");
      this.handleClose();

      return data;
    } catch (error) {
      console.error(error);
      if (error.response.status == 409) {
        toast.error("Mã thành phần đã được sử dụng, vui lòng sử dụng mã thành phần khác", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      } else if (error.response.status == 304) {
        toast.warning("Thành phần mặc định của hệ thống không được phép chỉnh sửa", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      } else {
        toast.error(i18n.t("toast.error"));
      }

      throw new Error(i18n.t("toast.error"));
    }
  };

  getSalaryItemTypeName = (type) => {
    return LocalConstants.SalaryItemType.getListData().find((i) => i.value == type)?.name;
  };

  getSalaryItemCalculationTypeName = (type) => {
    return LocalConstants.SalaryItemCalculationType.getListData().find((i) => i.value == type)?.name;
  };

  getSalaryItemValueTypeName = (type) => {
    return LocalConstants.SalaryItemValueType.getListData().find((i) => i.value == type)?.name;
  };

  uploadFileExcel = async (event) => {
    const fileInput = event.target; // Lưu lại trước
    const file = fileInput.files[0];
    let message = "Nhập excel thất bại";

    try {
      await importSalaryItem(file);
      toast.success("Nhập excel thành công");
      await this.pagingSalaryItem();
    } catch (error) {
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'string') {
          message = data;
        } else if (data.message) {
          message = data.message;
        }
      }
      toast.error(message);
    } finally {
      this.handleClose();
      fileInput.value = null;
    }
  };


  handleDownloadSalaryItemTemplate = async () => {
    try {
      const res = await downloadSalaryItemTemplate();
      let blob = new Blob([res.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs(blob, "Mẫu import thành phân lương.xlsx");
      toast.success("Đã tải mẫu import thành phân lương thành công");
    } catch (error) {
      toast.error("Tải mẫu import thành phân lương thất bại");
      console.error(error);
    }
  };
}
