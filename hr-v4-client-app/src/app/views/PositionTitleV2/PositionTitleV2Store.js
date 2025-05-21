import { makeAutoObservable } from "mobx";
import {
  autoGenCode,
  deletePosition,
  downloadGroupPositionTitleTemplate,
  downloadPositionTitleTemplate,
  exportExcelPositionTitleData,
  getPosition,
  importGroupPositionTitle,
  importPositionTitle,
  pagingParentPositionTitle,
  pagingPositionTitle,
  savePositionTitle
} from "../PositionTitle/PositionTitleService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { saveAs } from "file-saver";
import { PositionTitle } from "app/common/Model/HumanResource/PositionTitle";
import {HttpStatus} from "../../LocalConstants";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class PositionTitleV2Store {

  intactSearchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    rankTitle:null,
    parent:null,
    positionRole:null,
    type:null,
    isGroup:null,
  };

  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listPositionTitle = [];
  openCreateEditPopup = false;
  selectedPositionTitle = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openImportExcelPopup = false;
  listOnDelete = [];
  openViewPopup = false;

  handleOpenView = async (positionTitleId) => {
    try {
      if (positionTitleId) {
        const {data} = await getPosition (positionTitleId);
        this.selectedPositionTitle = data;
      } else {
        this.selectedPositionTitle = {
          ... new PositionTitle ()
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
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listPositionTitle = [];
    this.openCreateEditPopup = false;
    this.selectedPositionTitle = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    this.openViewPopup = false;

  }

  uploadFileExcel = async (event) => {
    try {
      const fileInput = event.target;
      const file = fileInput.files[0];
      fileInput.value = null;
      await importPositionTitle (file);

      toast.success ("Nhập excel thành công");

      this.searchObject = {
        ... this.searchObject,
        pageIndex:1
      };
      await this.pagingPositionTitle ();

    } catch (error) {
      console.error (error);

      if (error.response && error.response.status === 409) {
        toast.error ("Mã Chức danh đã được sử dụng, vui lòng sử dụng mã Chức danh khác", {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      } else if (error.response && typeof error.response.data === "string") {
        toast.error (error.response.data, {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      } else {
        toast.error ("Nhập excel thất bại", {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      }
    } finally {
      this.handleClose ();
    }
  };


  handleDownloadPositionTemplate = async () => {
    try {
      const res = await downloadPositionTitleTemplate ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu nhập dữ liệu chức danh.xlsx");
      toast.success (i18n.t ("general.successExport"));
    } catch (error) {
      console.error ("Error downloading timesheet detail template:", error);
    }
  };
  uploadFileExcelGroupPositionTitle = async (event) => {
    try {
      const fileInput = event.target;
      const file = fileInput.files[0];
      fileInput.value = null;

      await importGroupPositionTitle (file);

      toast.success ("Nhập excel thành công");

      this.searchObject = {
        ... this.searchObject,
        pageIndex:1,
        isGroup:true
      };

      await this.pagingParentPositionTitle ();
    } catch (error) {
      console.error (error);

      if (error.response && error.response.status === 409) {
        toast.error ("Mã nhóm chức danh đã được sử dụng, vui lòng chọn mã khác", {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      } else {
        toast.error ("Nhập excel thất bại");
      }
    } finally {
      this.handleClose ();
    }
  };


  handleDownloadGroupPositionTemplate = async () => {
    try {
      const res = await downloadGroupPositionTitleTemplate ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu nhập dữ liệu nhóm ngạch.xlsx");
      toast.success (i18n.t ("general.successExport"));
    } catch (error) {
      console.error ("Error downloading timesheet detail template:", error);
    }
  };

  handleSetSearchObject = (searchObject) => {
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    } else {
      searchObject.departmentId = searchObject.department.id;
    }
    if (searchObject.organization == null) {
      searchObject.organizationId = null;
    } else {
      searchObject.organizationId = searchObject.organization.id;
    }

    this.searchObject = {... searchObject};
  };

  pagingPositionTitle = async () => {
    try {
      // const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ... this.searchObject,
        // organizationId: loggedInStaff?.user?.org?.id,
        rankTitle:null,
        parent:null,
        positionRole:null,
        rankTitleId:this.searchObject?.rankTitle?.id,
        parentId:this.searchObject?.parent?.id,
        positionRoleId:this.searchObject?.positionRole?.id,
      };
      const data = await pagingPositionTitle (payload);

      this.listPositionTitle = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingPositionTitle ();
  };

  setPageIndexParent = async (page) => {
    this.searchParentObject.pageIndex = page;

    await this.pagingParentPositionTitle ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingPositionTitle ();
  };

  setPageSizeParent = async (event) => {
    this.searchParentObject.pageSize = event.target.value;
    this.searchParentObject.pageIndex = 1;

    await this.pagingParentPositionTitle ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleChangePageParent = async (event, newPage) => {
    await this.setPageIndexParent (newPage);
  };

  handleSelectListDelete = (deleteRankTitles) => {
    this.listOnDelete = deleteRankTitles;
  };

  getById = async (positionTitleId) => {
    try {
      const {data} = await getPosition (positionTitleId);
      this.selectedPositionTitle = data;
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
    this.openImportExcelPopup = false;
    this.openViewPopup = false;
  };

  handleDelete = (positionTitle) => {
    this.selectedPositionTitle = {... positionTitle};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };


  handleOpenCreateEdit = async (positionTitleId) => {
    try {
      if (positionTitleId) {
        const {data} = await getPosition (positionTitleId);
        this.selectedPositionTitle = data;
      } else {
        this.selectedPositionTitle = {
          ... new PositionTitle ()
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
      const {data} = await deletePosition (this?.selectedPositionTitle?.id);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingPositionTitle ();

      this.handleClose ();

    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteParent = async () => {
    try {
      const {data} = await deletePosition (this?.selectedPositionTitle?.id);
      toast.success (i18n.t ("toast.delete_success"));
      await this.pagingParentPositionTitle ();
      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteListParent = async () => {
    try {
      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        await deletePosition (this?.listOnDelete[i]?.id);
      }
      toast.success (i18n.t ("toast.delete_success"));
      await this.pagingParentPositionTitle ();
      this.listOnDelete = [];
      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      // const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        // deleteData.push(this?.listOnDelete[i]?.id);
        await deletePosition (this?.listOnDelete[i]?.id);
      }

      // console.log("deleteData", deleteData)
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingPositionTitle ();
      this.listOnDelete = [];

      this.handleClose ();


    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  savePositionTitle = async (positionTitle) => {
    try {
      const {data} = await savePositionTitle (positionTitle);
      toast.success ("Thông tin Chức danh đã được lưu");
      this.handleClose ();
    } catch (error) {
      console.error (error);
      if (error.response.status == 409) {
        toast.error ("Mã Chức danh đã được sử dụng, vui lòng sử dụng mã Chức danh khác", {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      } else {
        toast.error (i18n.t ("toast.error"));
      }

      throw new Error (i18n.t ("toast.error"));
    }
  };


  getTitleType = type => {
    if (type == 1) return "Chính quyền";
    if (type == 2) return "Đoàn thể";
    return "";
  }


  //HANDLE FOR SELECT PARENT POSITION TITLE
  intactSearchParentObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    isGroup:true,
  };
  totalParentElements = 0;
  totalParentPages = 0;
  listParentPositionTitle = [];
  searchParentObject = JSON.parse (JSON.stringify (this.intactSearchParentObject));

  resetParentStore = () => {
    this.searchParentObject = JSON.parse (JSON.stringify (this.intactSearchParentObject));
    this.totalParentElements = 0;
    this.totalParentPages = 0;
    this.listParentPositionTitle = [];
    // this.openCreateEditPopup = false;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
  }

  handleSetParentSearchObject = (searchParentObject) => {
    this.searchParentObject = {... searchParentObject};
  }

  pagingParentPositionTitle = async () => {
    try {
      //const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ... this.searchParentObject,
        //organizationId: loggedInStaff?.user?.org?.id
      };
      const data = await pagingParentPositionTitle (payload);

      this.listParentPositionTitle = data.data.content;
      this.totalParentElements = data.data.totalElements;
      this.totalParentPages = data.data.totalPages;

    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setParentPageIndex = async (page) => {
    this.searchParentObject.pageIndex = page;
    await this.pagingParentPositionTitle ();
  };

  handleParentChangePage = async (event, newPage) => {
    await this.setParentPageIndex (newPage);
  };

  setParentPageSize = async (event) => {
    this.searchParentObject.pageSize = event.target.value;
    this.searchParentObject.pageIndex = 1;
    await this.pagingParentPositionTitle ();
  };


  handleExportExcelPositionTitleData = async () => {
    if (this.totalElements > 0) {
      try {
        const payload = {
          ... this.searchObject,
          // organizationId: loggedInStaff?.user?.org?.id,
          rankTitle:null,
          parent:null,
          positionRole:null,
          rankTitleId:this.searchObject?.rankTitle?.id,
          parentId:this.searchObject?.parent?.id,
          positionRoleId:this.searchObject?.positionRole?.id,
        };
        const res = await exportExcelPositionTitleData (payload);
        toast.success (i18n.t ("general.successExport"));
        let blob = new Blob ([res.data], {
          type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });

        saveAs (blob, "DuLieuChucDanh.xlsx");
      } finally {

      }
    } else {
      toast.warning (i18n.t ("general.noData"));
    }
  }
  autoGenCode = async (configKey) =>{
    const response = await autoGenCode(configKey)
      if(response.status === HttpStatus.OK){
          return response.data;
      }
  }
}
