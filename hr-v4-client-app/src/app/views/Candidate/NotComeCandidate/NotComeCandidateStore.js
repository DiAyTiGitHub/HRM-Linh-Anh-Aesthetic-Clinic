import { makeAutoObservable } from "mobx";
import {
  pagingNotComeCandidates,
} from "./NotComeCandidateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { Candidate } from "app/common/Model/Candidate/Candidate";
import LocalConstants from "app/LocalConstants";
import { deleteCandidate, deleteMultiple } from "../Candidate/CandidateService";
import { SearchObjectCandidate } from "app/common/Model/SearchObject/SearchObjectCandidate";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class NotComeCandidateStore {
  intactSearchObject = {
    ...new SearchObjectCandidate(),
    organization: null,
    positionTitle: null
  };

  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  totalElements = 0;
  totalPages = 0;
  listNotComeCandidates = [];
  selectedCandidate = new Candidate();
  listChosen = [];
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {

    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    this.totalElements = 0;
    this.totalPages = 0;
    this.listNotComeCandidates = [];
    this.openCreateEditPopup = false;
    this.selectedCandidate = new Candidate();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatus = false;
    this.openReceiveJobPopup = false;
    this.openNotComeToReceivePopup = false;
  }

  handleSetSearchObject = (searchObject) => {
    if (searchObject.organization == null) {
      searchObject.organizationId = null;
    } else {
      searchObject.organization = searchObject.organization.id;
    }
    
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    } else {
      searchObject.departmentId = searchObject.department.id;
    }

    if (searchObject.position == null) {
      searchObject.positionId = null;
    } else {
      searchObject.positionId = searchObject.position.id;
    }

    this.searchObject = { ...searchObject };
  }

  pagingNotComeCandidates = async () => {
    try {
      const searchData = { ...this?.searchObject };

      const data = await pagingNotComeCandidates(searchData);

      this.listNotComeCandidates = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingNotComeCandidates();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingNotComeCandidates();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListChosen = (chosenItems) => {
    this.listChosen = chosenItems;
  };

  handleClose = async () => {
    this.pagingNotComeCandidates();

    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];

  };

  handleRemoveActionItem = (onRemoveId) => {
    this.listChosen = this?.listChosen?.filter(item => item?.id !== onRemoveId);
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listChosen?.forEach(function (candidate) {
      ids.push(candidate?.id);
    });

    return ids;
  }

  handleDelete = (candidate) => {
    this.selectedCandidate = { ...candidate };
    this.openConfirmDeletePopup = true;
  };

  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteCandidate(this?.selectedCandidate?.id);
      toast.success(i18n.t("toast.delete_success"));

      this.handleClose();
      return data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
      // throw new Error(error);
    }
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listChosen?.length; i++) {
        deleteData.push(this?.listChosen[i]?.id);
      }

      await deleteMultiple(deleteData);
      toast.success(i18n.t("toast.delete_success"));
      this.listChosen = [];

      await this.handleClose();

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };
}
