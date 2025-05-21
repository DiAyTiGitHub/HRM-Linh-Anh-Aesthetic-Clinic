import localStorageService from "app/services/localStorageService";
import i18n from "i18n";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {
  deleteWorkplace,
  deleteMultiple,
  getById,
  pagingWorkplace,
  saveWorkplace,
} from "./WorkplaceService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class WorkplaceStore {
  searchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
  };
  totalElements = 0;
  totalPages = 0;
  listWorkplace = [];
  openCreateEditPopup = false;
  selectedWorkplace = null;
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
    this.listWorkplace = [];
    this.openCreateEditPopup = false;
    this.selectedWorkplace = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  }

  initialWorkplace = {
    id: null,
    code: null,
    name: null,
    description: null,
  }

  handleSetSearchObject = (searchObject) => {
    this.searchObject = { ...searchObject };
  }

  pagingWorkplace = async () => {
    try {
      //const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ...this.searchObject,
        //organizationId: loggedInStaff?.user?.org?.id
      };
      const data = await pagingWorkplace(payload);

      this.listWorkplace = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingWorkplace();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingWorkplace();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteWorkplaces) => {
    this.listOnDelete = deleteWorkplaces;
  };

  getById = async (workplaceId) => {
    try {
      const { data } = await getById(workplaceId);
      this.selectedWorkplace = data;
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

  handleDelete = (workplace) => {
    this.selectedWorkplace = workplace;
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (workplaceId) => {
    try {
      if (workplaceId) {
        const { data } = await getById(workplaceId);
        this.selectedWorkplace = data;
      }
      else {
        this.selectedWorkplace = {
          ...this.initialWorkplace
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
      const { data } = await deleteWorkplace(this.selectedWorkplace.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingWorkplace();

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

      await deleteMultiple(deleteData);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingWorkplace();
      this.listOnDelete = [];

      this.handleClose();


    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveWorkplace = async (workplace) => {
    try {
      const { data } = await saveWorkplace(workplace);
      toast.success("Thông tin Loại phòng ban đã được lưu");
      this.handleClose();
    } catch (error) {
      console.error(error);
      if (error.response.status === 409) {
        toast.error("Mã địa điểm làm việc đã được sử dụng, vui lòng sử dụng mã địa điểm làm việc khác", {
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

  // uploadFileExcel = async (event) => {
  //   const file = event.target.files[0];
  //   importWorkplace(file).then(() => {
  //     toast.success("Nhập excel thành công")
  //     this.searchObject = {
  //       ...this.searchObject,
  //       pageIndex: 1
  //     }
  //     this.pagingWorkplace();
  //   }).catch(() => {
  //     toast.error("Nhập excel thất bại")
  //   }).finally(() => {
  //     this.handleClose();
  //   })
  //   event.target.value = null;
  // };

  // handleDownloadWorkplaceTemplate = async () => {
  //   try {
  //     const res = await downloadWorkplaceTemplate();
  //     let blob = new Blob([res.data], {
  //       type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  //     });
  //     saveAs(blob, "Mẫu nhập dữ liệu địa điểm làm việc.xlsx");
  //     toast.success(i18n.t("general.successExport"));
  //   } catch (error) {
  //     console.error("Error downloading workplace template:", error);
  //   }
  // };
}
