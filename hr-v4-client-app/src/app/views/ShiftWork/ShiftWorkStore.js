import { makeAutoObservable } from "mobx";
import {
  pagingShiftWork,
  getShiftWork,
  createShiftWork,
  editShiftWork,
  deleteShiftWork,
  checkCode,
  checkCodeShiftWork,
  saveShiftWork, importShiftWork, downloadShiftWorkTemplate,
} from "./ShiftWorkService";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchObject } from "app/common/Model/SearchObject/SearchObject";
import history from "../../../history";
import { ShiftWork } from "app/common/Model/Timekeeping/ShiftWork";
import { saveAs } from "file-saver";

export default class ShiftWorkStore {
  shiftWorkList = [];
  selectedShiftWork = null;
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 25;
  keyword = "";
  department = null;
  organization = null;
  loadingInitial = false;
  shouldOpenEditorDialog = false;
  shouldOpenConfirmationDialog = false;

  searchShiftWork = new SearchObject ();
  pageShiftWork = null;
  selectedShiftWorkEdit = null;
  selectedShiftWorkDeleted = null;

  selectedShiftWorkList = [];
  shouldOpenConfirmationDeleteListDialog = false;
  openViewPopup = false;
  handleOpenView = async (id) => {
    try {
      if (id) {
        const {data} = await getShiftWork (id);
        this.selectedShiftWork = data;
      } else {
        this.selectedShiftWork = {
          ... new ShiftWork (),
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

  uploadFileExcel = (event) => {
    const file = event.target.files[0];
    importShiftWork (file)
        .then (() => {
          toast.success ("Nhập excel thành công");
          this.search ();
        })
        .catch (async (error) => {
          let message = "Nhập excel thất bại";
          if (error.response && error.response.data) {
            const data = error.response.data;
            if (typeof data === 'string') {
              message = data;
            } else if (data.message) {
              message = data.message;
            }
          }
          toast.error (message);
        })
        .finally (() => {
          this.handleClose ();
        });

    event.target.value = null;
  };


  handleDownloadShiftWorkTemplate = async () => {
    try {
      const res = await downloadShiftWorkTemplate ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu import phòng ban áp dụng ca làm việc.xlsx");
      toast.success ("Đã tải mẫu import phòng ban áp dụng ca làm việc");
    } catch (error) {
      console.error ("Error downloading shift workd template:", error);
    }
  };

  onPagingShiftWork = async () => {
    //const searchObj = new SearchObject(true)
    //this.searchShiftWork = searchObj;
    try {
      this.search ();
      //this.pageShiftWork = res.data
    } catch (e) {
      toast.warning (i18n.t ("toast.error"))
    }
  }

  onChangeFormSearch = (obj) => {
    const value = SearchObject.checkSearchObject (this.searchShiftWork, obj);
    const url = SearchObject.pushSearchToUrl (value);
    history.push (url.pathname + url.search)
  }

  onOpenShiftWorkEdit = async (shiftWorkId) => {
    let shiftWork = null;

    if (shiftWorkId) {
      shiftWork = (await getShiftWork (shiftWorkId))?.data;
    }

    if (!shiftWork) {
      shiftWork = new ShiftWork ();
    }


    this.selectedShiftWorkEdit = shiftWork;
  }

  onSaveShiftWork = async (shiftWork, {setSubmitting}) => {
    try {
      const response = await checkCodeShiftWork (shiftWork.id, shiftWork.code);
      if (response?.data) {
        throw new Error (i18n.t ("toast.duplicate_code"))
      }

      const res = await saveShiftWork (shiftWork);
      if (!res?.data) {
        throw new Error ();
      }

      toast.success (i18n.t (shiftWork.id? "toast.update_success" : "toast.add_success"));
      // this.onClosePopup();
      // this.onPagingShiftWork();

      this.handleClose ();


    } catch (error) {
      toast.error (error?.message? error.message : i18n.t ("toast.error"));
    } finally {
      setSubmitting (false)
    }
  }

  setSelectShiftWorkDeleted = (shiftWorkId) => {
    this.selectedShiftWorkDeleted = shiftWorkId;
  }

  onDeletedShiftWork = async () => {
    try {
      const res = await deleteShiftWork (this.selectedShiftWorkDeleted);
      if (res.data) {
        toast.success (i18n.t ("toast.delete_success"));
        this.onClosePopup ();

        // ??
        // if (
        //   this.pageShiftWork.content.length === 1 && this.searchShiftWork.pageIndex > 1 &&
        //   this.pageShiftWork.totalPages === this.searchShiftWork.pageIndex
        // ) {
        //   this.onChangeFormSearch({ pageIndex: this.searchShiftWork.pageIndex - 1 });
        // } else {
        //   this.onPagingShiftWork();
        // }

        this.onPagingShiftWork ();
      }
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  }

  handleSelectListShiftWork = (shiftWork) => {
    this.selectedShiftWorkList = shiftWork;
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedShiftWorkList.length; i++) {
      try {
        await deleteShiftWork (this.selectedShiftWorkList[i].id);
      } catch (error) {
        listAlert.push (this.selectedShiftWorkList[i].name);
        console.log (error);
        console.log (listAlert.toString ());
        toast.warning (i18n.t ("toast.error"));
      }
    }
    this.handleClose ();
    toast.success (i18n.t ("toast.delete_success"));
  };

  onClosePopup = () => {
    this.selectedShiftWorkEdit = null;
    this.selectedShiftWorkDeleted = null;
    this.shouldOpenConfirmationDeleteListDialog = false;
    this.openViewPopup = false;

  }

  resetStore = () => {
    this.searchEmployeeStatus = new SearchObject ();
    this.pageShiftWork = null;
    this.onClosePopup ();
  }


  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  updatePageData = (item) => {
    if (item != null) {
      this.page = 1;
      this.keyword = item.keyword;
      this.department = item.department;
      this.organization = item.organization;
      return this.search ()
    } else {
      return this.search ();
    }
  };

  search = async () => {
    this.loadingInitial = true;
    const searchObject = {
      keyword:this.keyword,
      pageIndex:this.page,
      pageSize:this.rowsPerPage,
      departmentId:this.department?.id,
      organizationId:this.organization?.id,
      department:null,
      organization:null,
    };
    try {
      let data = await pagingShiftWork (searchObject);
      this.shiftWorkList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
      this.setLoadingInitial (false);

      return data.data.content;
    } catch (error) {
      toast.warning (i18n.t ("toast.error"));
      this.setLoadingInitial (false);
      return [];
    }
  };

  setPage = (page) => {
    this.page = page;
    this.updatePageData ();
  };

  setRowsPerPage = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    this.updatePageData ();
  };

  handleChangePage = (event, newPage) => {
    this.setPage (newPage);
  };

  handleEditShiftWork = (id) => {
    this.getShiftWork (id).then (() => {
      this.shouldOpenEditorDialog = true;
    });
  };

  handleClose = () => {
    this.shouldOpenEditorDialog = false;
    this.shouldOpenConfirmationDialog = false;
    this.shouldOpenConfirmationDeleteListDialog = false;
    this.openViewPopup = false;
    this.updatePageData ();
  };

  handleDelete = (id) => {
    this.getShiftWork (id).then (() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleConfirmDelete = async () => {
    try {
      await deleteShiftWork (this.selectedShiftWork.id);
      toast.success (i18n.t ("toast.delete_success"));
      this.handleClose ();
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  getShiftWork = async (id) => {
    if (id != null) {
      try {
        let data = await getShiftWork (id);
        this.handleSelectShiftWork (data.data);
      } catch (error) {
        console.log (error);
        toast.warning (i18n.t ("toast.error"));
      }
    } else {
      this.handleSelectShiftWork (null);
    }
  };

  handleSelectShiftWork = (shiftWork) => {
    this.selectedShiftWork = shiftWork;
  };

  createShiftWork = async (shiftWork) => {
    try {
      let response = await checkCode (shiftWork.id, shiftWork.code);
      if (response.data) {
        toast.warning (i18n.t ("toast.duplicate_code"));
      } else {
        await createShiftWork (shiftWork);
        toast.success (i18n.t ("toast.add_success"));
        this.handleClose ();
      }
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  editShiftWork = async (shiftWork) => {
    try {
      let response = await checkCode (shiftWork.id, shiftWork.code);
      if (response.data) {
        toast.warning (i18n.t ("toast.duplicate_code"));
      } else {
        await editShiftWork (shiftWork);
        toast.success (i18n.t ("toast.update_success"));
        this.handleClose ();
      }
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };
}
