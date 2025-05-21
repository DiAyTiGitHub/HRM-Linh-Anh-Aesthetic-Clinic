import { makeAutoObservable } from "mobx";
import {
  pagingPositionTitle,
  getPosition,
  createPosition,
  editPosition,
  deletePosition,
  checkCode,
} from "./PositionTitleService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class PositionTitleStore {
  positionTitleList = [];
  initialPositionTitle = {
    id: "",
    code: "",
    value: "",
  };
  selectedPositionTitle = this.initialPositionTitle;
  selectedPositionTitleList = [];
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  keyword = "";
  loadingInitial = false;
  shouldOpenEditorDialog = false;
  shouldOpenConfirmationDialog = false;
  shouldOpenConfirmationDeleteListDialog = false;
  shouldOpenPositionTitlePopup = false;
  shouldOpenImportExcelDialog = false;
  searchObject = {
    // datatype: null,
    // conceptType: null,
    // parent: null,
    // specimenType: null,
    // conceptTypeTest:null,
  }

  constructor() {
    makeAutoObservable(this);
  }

  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  updatePageData = (item) => {
    if (item != null) {
      this.page = 1;
      this.keyword = item.keyword;
      this.search();
    } else {
      this.search();
    }

  };

  search = async () => {
    this.loadingInitial = true;
    var searchObject = {
      keyword: this.keyword,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
    };

    try {
      let data = await pagingPositionTitle(searchObject);
      this.positionTitleList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
      this.setLoadingInitial(false);
    }
  };

  handleSetSearchObject = (item) => {
    if (item.pageIndex) {
      this.page = item.pageIndex;
    }
    this.searchObject = item;
  }

  setPage = (page) => {
    this.page = page;
    this.updatePageData();
  };

  setRowsPerPage = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    this.updatePageData();
    this.handleSelectListPositionTitle([]);
  };

  handleChangePage = (event, newPage) => {
    this.setPage(newPage);
    this.handleSelectListPositionTitle([]);
  };

  handleEditPosition = (id) => {
    this.getPosition(id).then(() => {
      this.shouldOpenEditorDialog = true;
    });
  };

  handleClose = () => {
    this.shouldOpenEditorDialog = false;
    this.shouldOpenConfirmationDialog = false;
    this.shouldOpenConfirmationDeleteListDialog = false;
    this.shouldOpenImportExcelDialog = false;
    this.updatePageData();
  };

  handleDelete = (id) => {
    this.getPosition(id).then(() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  handleConfirmDelete = async () => {
    try {
      await deletePosition(this.selectedPositionTitle.id);
      toast.success(i18n.t("toast.delete_success"));
      this.handleSelectListPositionTitle([]);
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedPositionTitleList.length; i++) {
      try {
        await deletePosition(this.selectedPositionTitleList[i].id);
      } catch (error) {
        listAlert.push(this.selectedPositionTitleList[i].name);
        toast.warning(i18n.t("toast.error"));
      }
    }
    this.handleClose();
    toast.success(i18n.t("toast.delete_success"));
    this.handleSelectListPositionTitle([]);
  };

  getPosition = async (id) => {
    this.loadingInitial = true;
    if (id != null) {
      try {
        let data = await getPosition(id);
        this.handleSelectPositionTitle(data.data);
        this.setLoadingInitial(false);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.error"));
        this.setLoadingInitial(false);
      }
    } else {
      this.handleSelectPositionTitle(this.initialPositionTitle);
      this.setLoadingInitial(false);
    }
  };

  handleSelectPositionTitle = (position) => {
    this.selectedPositionTitle = position;
  };

  handleSelectListPositionTitle = (position) => {
    this.selectedPositionTitleList = position;
  };

  createPosition = async (position) => {
    try {
      let responseCheckCode = await checkCode(position.id, position.code);
      if (responseCheckCode.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await createPosition(position);
        toast.success(i18n.t("toast.add_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  editPosition = async (position) => {
    try {
      let responseCheckCode = await checkCode(position.id, position.code);
      if (responseCheckCode.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await editPosition(position);
        toast.success(i18n.t("toast.update_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  handleTogglePositionTitlePopup = () => {
    if (this.shouldOpenPositionTitlePopup === true) {
      this.shouldOpenPositionTitlePopup = false;
    } else {
      this.shouldOpenPositionTitlePopup = true;
    }
  };

  handleClosePopup = () => {
    this.shouldOpenPositionTitlePopup = false;
  };

  importExcel = () => {
    this.shouldOpenImportExcelDialog = true;
  };
}
