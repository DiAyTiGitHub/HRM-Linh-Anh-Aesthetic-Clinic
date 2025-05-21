import { makeAutoObservable } from "mobx";
import { pagingOnboardedCandidates } from "./OnboaredCandidateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { Candidate } from "app/common/Model/Candidate/Candidate";
import LocalConstants from "app/LocalConstants";
import {deleteCandidate, deleteMultiple, exportCandidateHDLD, resignMultiple} from "../Candidate/CandidateService";
import { SearchObjectCandidate } from "app/common/Model/SearchObject/SearchObjectCandidate";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class OnboaredCandidateStore {
  searchObject = {
    ...new SearchObjectCandidate(),
    organization:null
  };

  totalElements = 0;
  totalPages = 0;
  listOnboardedCandidates = [];
  selectedCandidate = new Candidate();
  listChosen = [];
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openResignListPopup = false;

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = new SearchObjectCandidate();

    this.totalElements = 0;
    this.totalPages = 0;
    this.listOnboardedCandidates = [];
    this.openCreateEditPopup = false;
    this.selectedCandidate = new Candidate();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatus = false;
    this.openReceiveJobPopup = false;
    this.openNotComeToReceivePopup = false;
    this.openResignListPopup = false;

  };

  handleSetSearchObject = (searchObject) => {
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    } else {
      searchObject.departmentId = searchObject.department.id;
    }
    this.searchObject = { ...searchObject };
  };

  pagingOnboardedCandidates = async () => {
    try {
      const searchData = { ...this.searchObject };

      const data = await pagingOnboardedCandidates(searchData);

      this.listOnboardedCandidates = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingOnboardedCandidates();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingOnboardedCandidates();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListChosen = (chosenItems) => {
    this.listChosen = chosenItems;
  };

  handleClose = async () => {
    this.pagingOnboardedCandidates();

    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
  };

  handleRemoveActionItem = (onRemoveId) => {
    this.listChosen = this?.listChosen?.filter((item) => item?.id !== onRemoveId);
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listChosen?.forEach(function (candidate) {
      ids.push(candidate?.id);
    });

    return ids;
  };

  handleDelete = (candidate) => {
    this.selectedCandidate = { ...candidate };
    this.openConfirmDeletePopup = true;
  };

  handleExportHDLD = async (candidateId) => {
    try {
      if (candidateId) {
        // Call the export function and handle the download
        await exportCandidateHDLD(candidateId);

        toast.success(i18n.t("toast.success")); // Assuming you want a success message
      } else {
        // Handle case where the candidateId is not provided
        toast.error(i18n.t("toast.missingId"));
      }
    } catch (error) {
      console.error("Error during export:", error);
      toast.error(i18n.t("toast.error"));
    }
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
 handleOpenResignListPopup = (value) => {
   this.openResignListPopup = value;
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

  handleResignMultiple = async () => {
    try {
      await resignMultiple(this?.listChosen?.map(item => item.id));
      toast.success("Thành công");
      this.listChosen = [];

      await this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };
}
