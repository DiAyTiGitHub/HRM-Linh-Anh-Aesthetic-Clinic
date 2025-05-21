import { makeAutoObservable } from "mobx";
import {
  deleteMultiple,
  deleteRankTitle,
  downloadRankTitleTemplate,
  getById,
  importRankTitle,
  pagingRankTitle,
  saveRankTitle,
  autoGenCode
} from "./RankTitleService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { saveAs } from "file-saver";
import { RankTitle } from "app/common/Model/HumanResource/RankTitle";
import {HttpStatus} from "../../LocalConstants";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class RankTitleStore {
  searchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
  };
  totalElements = 0;
  totalPages = 0;
  listRankTitle = [];
  openCreateEditPopup = false;
  selectedRankTitle = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listOnDelete = [];
  openViewPopup = false;

  handleOpenView = async (rankTitleId) => {
    try {
      if (rankTitleId) {
        const {data} = await getById (rankTitleId);
        this.selectedRankTitle = data;
      } else {
        this.selectedRankTitle = {
          ... new RankTitle ()
        };
      }

      this.openViewPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  constructor () {
    makeAutoObservable (this);
  }

  resetStore = () => {
    this.searchObject = {
      pageIndex:1,
      pageSize:10,
      keyword:null,
    };
    this.totalElements = 0;
    this.totalPages = 0;
    this.listRankTitle = [];
    this.openCreateEditPopup = false;
    this.selectedRankTitle = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    this.openViewPopup = false;
  }
  uploadFileExcel = async (event) => {
    const fileInput = event.target; // Lưu lại trước
    const file = fileInput.files[0];
    let message = "Nhập excel thất bại";

    try {
      await importRankTitle (file);
      toast.success ("Nhập excel thành công");
      this.pagingRankTitle ();
    } catch (error) {
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'string') {
          message = data;
        } else if (data.message) {
          message = data.message;
        }
      }
      toast.error (message);
    } finally {
      this.handleClose ();
      fileInput.value = null;
    }
  };

  handleDownloadRankTitleTemplate = async () => {
    try {
      const res = await downloadRankTitleTemplate ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu nhập dữ liệu cấp bậc.xlsx");
      toast.success (i18n.t ("general.successExport"));
    } catch (error) {
      console.error ("Error downloading timesheet detail template:", error);
    }
  };

  handleSetSearchObject = (searchObject) => {
    this.searchObject = {... searchObject};
  }

  pagingRankTitle = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser ();
      const payload = {
        ... this.searchObject,
        organizationId:loggedInStaff?.user?.org?.id
      };
      const data = await pagingRankTitle (payload);

      this.listRankTitle = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingRankTitle ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingRankTitle ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleSelectListDelete = (deleteRankTitles) => {
    this.listOnDelete = deleteRankTitles;
  };

  getById = async (rankTitleId) => {
    try {
      const {data} = await getById (rankTitleId);
      this.selectedRankTitle = data;
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openViewPopup = false;
  };

  handleDelete = (rankTitle) => {
    this.selectedRankTitle = {... rankTitle};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (rankTitleId) => {
    try {
      if (rankTitleId) {
        const {data} = await getById (rankTitleId);
        this.selectedRankTitle = data;
      } else {
        this.selectedRankTitle = {
          ... new RankTitle ()
        };
      }

      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteRankTitle (this?.selectedRankTitle?.id);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingRankTitle ();

      this.handleClose ();

    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        deleteData.push (this?.listOnDelete[i]?.id);
      }

      // console.log("deleteData", deleteData)
      await deleteMultiple (deleteData);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingRankTitle ();
      this.listOnDelete = [];

      this.handleClose ();


    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  saveRankTitle = async (rankTitle) => {
    try {
      const {data} = await saveRankTitle (rankTitle);
      toast.success ("Thông tin Cấp bậc đã được lưu");
      this.handleClose ();

    } catch (error) {
      console.error (error);
      if (error.response.status == 409) {
        toast.error ("Mã cấp bậc đã được sử dụng, vui lòng sử dụng mã khác", {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      } else {
        toast.error (i18n.t ("toast.error"));
      }
    }
  };
  autoGenCode = async (configKey) =>{
    const response = await autoGenCode(configKey)
    if(response.status === HttpStatus.OK){
      return response.data;
    }
  }
}
