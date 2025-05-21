import { makeAutoObservable } from "mobx";
import {
  getAllSalaryAutoMap,
  getSalaryAutoMap,
  saveOrUpdateSalaryAutoMap
} from "./SalaryAutoMapService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class SalaryAutoMapStore {
  salaryAutoMapList = [];
  selectedSalaryAutoMap = null;
  selectedSalaryAutoMapList = [];
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  keyword = "";
  loadingInitial = false;
  shouldOpenEditorDialog = false;
  shouldOpenConfirmationDialog = false;
  shouldOpenConfirmationDeleteListDialog = false;

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
      this.getAllSalaryAutoMap();
    } else {
      this.getAllSalaryAutoMap();
    }
  };

  getAllSalaryAutoMap = async () => {
    this.loadingInitial = true;
    var searchObject = {
      keyword: this.keyword,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
    };

    try {
      let data = await getAllSalaryAutoMap(searchObject);
      this.salaryAutoMapList = data.data;
      //this.salaryAutoMapList = data.data.content;
      // this.totalElements = data.data.totalElements;
      // this.totalPages = data.data.totalPages;
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
      this.setLoadingInitial(false);
    }
  };

  setPage = (page) => {
    this.page = page;
    this.updatePageData();
  };

  setRowsPerPage = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    this.updatePageData();
  };

  handleChangePage = (event, newPage) => {
    this.setPage(newPage);
  };

  handleEditSalaryAutoMap = (id) => {
    this.getSalaryAutoMap(id).then(() => {
      this.shouldOpenEditorDialog = true;
    });
  };

  handleClose = () => {
    this.shouldOpenEditorDialog = false;
    this.shouldOpenConfirmationDialog = false;
    this.shouldOpenConfirmationDeleteListDialog = false;
    this.updatePageData();
  };

  handleDelete = (id) => {
    this.getSalaryAutoMap(id).then(() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  getSalaryAutoMap = async (id) => {
    if (id != null) {
      try {
        let data = await getSalaryAutoMap(id);
        this.handleSelectSalaryAutoMap(data.data);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.error"));
      }
    } else {
      this.handleSelectSalaryAutoMap(null);
    }
  };

  handleSelectSalaryAutoMap = (salaryAutoMap) => {
    this.selectedSalaryAutoMap = salaryAutoMap;
  };

  handleSelectListSalaryAutoMap = (salaryAutoMaps) => {
    this.selectedSalaryAutoMapList = salaryAutoMaps;
    console.log(this.selectedSalaryAutoMapList);
  };

  saveOrUpdateSalaryAutoMap = async (salaryAutoMap) => {
    try {
      await saveOrUpdateSalaryAutoMap(salaryAutoMap);
      toast.success(i18n.t("toast.add_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

}
