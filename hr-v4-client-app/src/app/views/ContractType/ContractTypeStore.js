import { makeAutoObservable } from "mobx";
import {
  pagingContractTypes,
  getContractType,
  createContractType,
  editContractType,
  deleteContractType,
  checkCode,
} from "./ContractTypeService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class ContractTypeStore {
  contractTypeList = [];
  selectedContractType = null;
  selectedContractTypeList = [];
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
      let data = await pagingContractTypes(searchObject);
      this.contractTypeList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
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

  handleEditContractType = (id) => {
    this.getContractType(id).then(() => {
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
    this.getContractType(id).then(() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  handleConfirmDelete = async () => {
    try {
      await deleteContractType(this.selectedContractType.id);
      toast.success(i18n.t("toast.delete_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedContractTypeList.length; i++) {
      try {
        await deleteContractType(this.selectedContractTypeList[i].id);
      } catch (error) {
        listAlert.push(this.selectedContractTypeList[i].name);
        console.log(error);
        console.log(listAlert.toString());
        toast.warning(i18n.t("toast.error"));
      }
    }
    this.handleClose();
    toast.success(i18n.t("toast.delete_success"));
  };

  getContractType = async (id) => {
    if (id != null) {
      try {
        let data = await getContractType(id);
        this.handleSelectContractType(data.data);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.error"));
      }
    } else {
      this.handleSelectContractType(null);
    }
  };

  handleSelectContractType = (contractType) => {
    this.selectedContractType = contractType;
  };

  handleSelectListContractType = (contractTypes) => {
    this.selectedContractTypeList = contractTypes;
    console.log(this.selectedContractTypeList);
  };

  createContractType = async (contractType) => {
    try {
      let response = await checkCode(contractType.id, contractType.code);
      if (response.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await createContractType(contractType);
        toast.success(i18n.t("toast.add_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  editContractType = async (contractType) => {
    try {
      let response = await checkCode(contractType.id, contractType.code);
      if (response.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await editContractType(contractType);
        toast.success(i18n.t("toast.update_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };
}
