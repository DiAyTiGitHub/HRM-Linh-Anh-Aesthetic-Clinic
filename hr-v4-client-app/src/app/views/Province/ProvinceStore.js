import { makeAutoObservable } from "mobx";
import {
  pagingProvinces,
  getProvince,
  createProvince,
  editProvince,
  deleteProvince,
  checkCode,
} from "./ProvinceService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
  //etc you get the idea
});

export default class Store {
  provinceList = [];
  selectedProvinceList = [];
  selectedProvince = null;
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  text = "";
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

  updatePageData = (province) => {
    var searchObject = {};
    searchObject.text = this.text;
    searchObject.pageIndex = this.page;
    searchObject.pageSize = this.rowsPerPage;
    if (province != null) {
      this.page = 1;
      this.text = province.text;
      this.search(searchObject);
    } else {
      this.search(searchObject);
    }
  };

  search = async (searchObject) => {
    this.loadingInitial = true;
    try {
      let data = await pagingProvinces(searchObject);
      this.provinceList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
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

  handleEditProvince = (id) => {
    this.getProvince(id).then(() => {
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
    this.getProvince(id).then(() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleConfirmDelete = () => {
    this.deleteProvince(this.selectedProvince.id);
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedProvinceList.length; i++) {
      try {
        await deleteProvince(this.selectedProvinceList[i].id);
      } catch (error) {
        listAlert.push(this.selectedProvinceList[i].name);
        console.log(error);
        console.log(listAlert.toString());
        toast.warning(i18n.t("toast.error"));
      }
    }
    this.handleClose();
    toast.success(i18n.t("toast.delete_success"));
  };

  getProvince = async (id) => {
    if (id != null) {
      try {
        let data = await getProvince(id);
        this.handleSelectProvince(data.data);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.error"));
      }
    } else {
      this.handleSelectProvince(null);
    }
  };

  handleSelectProvince = (province) => {
    this.selectedProvince = province;
  };

  handleSelectListProvince = (provinces) => {
    this.selectedProvinceList = provinces;
  };

  createProvince = async (province) => {
    try {
      let responseCheckCode = await checkCode(province.id, province.code);
      if (responseCheckCode.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await createProvince(province);
        toast.success(i18n.t("toast.add_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  editProvince = async (province) => {
    try {
      let responseCheckCode = await checkCode(province.id, province.code);
      if (responseCheckCode.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await editProvince(province);
        toast.success(i18n.t("toast.update_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  deleteProvince = async (id) => {
    try {
      await deleteProvince(id);
      toast.success(i18n.t("toast.delete_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };
}
