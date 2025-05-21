import { makeAutoObservable } from "mobx";
import {
  pagingExportCandidate,
  exportExcelCandidatesByFilter
} from "./ExportCandidateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { Candidate } from "app/common/Model/Candidate/Candidate";
import LocalConstants from "app/LocalConstants";
import { SearchObjectCandidate } from "app/common/Model/SearchObject/SearchObjectCandidate";
import localStorageService from "app/services/localStorageService";
import { deleteCandidate, deleteMultiple } from "../Candidate/CandidateService";
import { saveAs } from "file-saver";

toast.configure({
  autoClose: 4000,
  draggable: false,
  limit: 3,
});

export default class ExportCandidateStore {
  intactSearchObject = {
    ...new SearchObjectCandidate(),
    onboardStatus: 0,
    organization:null,
    tabs: 0
  };

  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  totalElements = 0;
  totalPages = 0;
  listReportCandidates = [];
  selectedCandidate = new Candidate();
  listChosen = [];

  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openConfirmExportExcel = false;

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {

    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    this.totalElements = 0;
    this.totalPages = 0;
    this.listReportCandidates = [];
    this.selectedCandidate = new Candidate();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openConfirmExportExcel = false;
    this.listChosen = [];
  }

  //lọc theo trạng thái nhận việc = thay đổi tab
  handleChangeOnboardStatus = (status) => {
      let s = null
    this.searchObject.tabs = status;
      if(status === 1){
          s = LocalConstants.CandidateStatus.PENDING_ASSIGNMENT.value;
      }else if(status === 2){
          s = LocalConstants.CandidateStatus.DECLINED_ASSIGNMENT.value;
      }else if(status === 3){
          s = LocalConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.value;
      }
      const so = { ...this.searchObject, status: s };
      this.searchObject = so;
  }

  handleSetSearchObject = (searchObject) => {
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    }
    else {
      searchObject.departmentId = searchObject.department.id;
    }

    if (searchObject.position == null) {
      searchObject.positionId = null;
    }
    else {
      searchObject.positionId = searchObject.position.id;
    }

    if (searchObject.recruitmentRequest == null) {
      searchObject.recruitmentRequestId = null;
    }
    else {
      searchObject.recruitmentRequestId = searchObject.recruitmentRequest.id;
    }

    if (searchObject.recruitmentPlan == null) {
      searchObject.recruitmentPlanId = null;
    }
    else {
      searchObject.recruitmentPlanId = searchObject.recruitmentPlan.id;
    }

    this.searchObject = { ...searchObject };
  }

  mapTabToStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab == 0) return null;
    // tab 1 => Chờ nhận việc
    if (tab == 1) return LocalConstants.CandidateOnboardStatus.WAITING.value;
    // tab 2 => Không đến nhận việc
    if (tab == 2) return LocalConstants.CandidateOnboardStatus.NOT_COME.value;
    // tab 3 => Đã nhận việc
    if (tab == 3) return LocalConstants.CandidateOnboardStatus.ONBOARDED.value;

    return null;
  }

  pagingExportCandidate = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ...this.searchObject,
        onboardStatus: this.mapTabToStatus(this.searchObject.onboardStatus),
      };
      const data = await pagingExportCandidate(payload);

      this.listReportCandidates = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingExportCandidate();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingExportCandidate();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListChosen = (chosenItems) => {
    this.listChosen = chosenItems;
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openConfirmExportExcel = false;
    this.listChosen = [];

    this.selectedCandidate = null;
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

  getOnboardStatusName = (status) => {
    return LocalConstants.CandidateOnboardStatus.getListData().find(i => i.value == status)?.name;
  }

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

      await this.pagingExportCandidate();
      this.handleClose();

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleDelete = (candidate) => {
    this.selectedCandidate = { ...candidate };
    this.openConfirmDeletePopup = true;
  };

  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteCandidate(this?.selectedCandidate?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingExportCandidate();
      this.handleClose();

      return data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
      // throw new Error(error);
    }
  };

  handleRemoveActionItem = (onRemoveId) => {
    this.listChosen = this?.listChosen?.filter(item => item?.id !== onRemoveId);
  };

  handleOpenConfirmExportExcel = () => {
    this.openConfirmExportExcel = true;
  }

  handleConfirmExportExcel = async () => {
    toast.info("Vui lòng đợi, yêu cầu đang được xử lý");

    try {
      let payload = {};
      if (this.listChosen?.length > 0) {
        payload.candidateIds = [];
        this.listChosen.forEach(item => payload.candidateIds.push(item.id));
      }
      else {
        payload = {
          ...JSON.parse(JSON.stringify(this.searchObject)),
          onboardStatus: this.mapTabToStatus(this.searchObject.onboardStatus),
        };
      }

      const { data } = await exportExcelCandidatesByFilter(payload);
      let blob = new Blob([data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      saveAs(blob, "Danh_sach_ung_vien_theo_bo_loc.xlsx");
      toast.success(i18n.t("general.successExport"));

      await this.pagingExportCandidate();
      this.handleClose();
    }
    catch (err) {
      console.error(err);
      toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất Excel, vui lòng thử lại sau");
    }
  };
}
