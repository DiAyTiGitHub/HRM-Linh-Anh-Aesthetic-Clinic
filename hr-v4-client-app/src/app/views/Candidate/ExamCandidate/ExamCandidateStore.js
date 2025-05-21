import { makeAutoObservable } from "mobx";
import {
  pagingExamCandidates,
  updateExamStatus
} from "./ExamCandidateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { Candidate } from "app/common/Model/Candidate/Candidate";
import LocalConstants from "app/LocalConstants";
import { SearchObjectCandidate } from "app/common/Model/SearchObject/SearchObjectCandidate";
import localStorageService from "app/services/localStorageService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class ExamCandidateStore {
  intactSearchObject = {
    ...new SearchObjectCandidate(),
    interviewDate: new Date(),
    examStatus: 0,
    positionTitle: null
  };

  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  totalElements = 0;
  totalPages = 0;
  listExamCandidates = [];
  openCreateEditPopup = false;
  selectedCandidate = new Candidate();
  listChosen = [];

  openRejectPopup = false;
  openPassPopup = false;
  openFailPopup = false;
  openResetPopup = false;
  openRecruitingPopup = false;

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {

    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    this.totalElements = 0;
    this.totalPages = 0;
    this.listExamCandidates = [];
    this.openCreateEditPopup = false;
    this.selectedCandidate = new Candidate();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatus = false;

    this.openRejectPopup = false;
    this.openPassPopup = false;
    this.openFailPopup = false;
    this.openResetPopup = false;
    this.openRecruitingPopup = false;
  }

  //lọc theo trạng thái kết quả thi tuyển = thay đổi tab
  handleChangeExamStatus = (status) => {
    const so = { ...this.searchObject, examStatus: status };
    this.searchObject = so;
  }

  handleSetSearchObject = (searchObject) => {
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    }
    else {
      searchObject.departmentId = searchObject.department.id;
    }
    this.searchObject = { ...searchObject };
  }


  mapTabToStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab == 0) return null;
    // tab 1 => Chưa dự tuyển
    if (tab == 1) return LocalConstants.CandidateExamStatus.NOT_TESTED_YET.value;
    // tab 2 => Đang dự tuyển
    if (tab == 2) return LocalConstants.CandidateExamStatus.RECRUITING.value;
    // tab 3 => Đạt
    if (tab == 3) return LocalConstants.CandidateExamStatus.PASSED.value;
    // tab 4 => Không đạt
    if (tab == 4) return LocalConstants.CandidateExamStatus.FAILED.value;
    // tab 5 => Đã từ chối
    if (tab == 4) return LocalConstants.CandidateExamStatus.REJECTED.value;

    return null;
  }

  pagingExamCandidates = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();

      const payload = {
        ...this.searchObject,
        examStatus: this.mapTabToStatus(this?.searchObject?.examStatus),
      };

      const data = await pagingExamCandidates(payload);

      this.listExamCandidates = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingExamCandidates();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingExamCandidates();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListChosen = (chosenItems) => {
    this.listChosen = chosenItems;
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openConfirmUpdateStatus = false;
    this.openRejectPopup = false;
    this.openPassPopup = false;
    this.openFailPopup = false;
    this.openResetPopup = false;
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

  handleOpenRejectPopup = () => {
    this.openRejectPopup = true;
  }

  handleConfirmRejectCandidate = async (formValues) => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error("Không có ứng viên nào được chọn TỪ CHỐI");
        return;
      }

      const payload = {
        candidateIds: this.getSelectedIds(),
        examStatus: LocalConstants.CandidateExamStatus.REJECTED.value,
        refusalReason: formValues?.refusalReason
      };

      const { data } = await updateExamStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cập nhật trạng thái thi tuyển cho ứng viên thành công!");

      this.handleClose();
      this.pagingExamCandidates();
    }
    catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  getExamStatus = (status) => {
    return LocalConstants.CandidateExamStatus.getListData().find(i => i.value == status)?.name;
  }

  handleOpenPassPopup = () => {
    this.openPassPopup = true;
  }

  handleConfirmPassResult = async (formValues) => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error("Không có ứng viên nào được chọn PASS");
        return;
      }

      const payload = {
        candidateIds: this.getSelectedIds(),
        examStatus: LocalConstants.CandidateExamStatus.PASSED.value,
      };

      const { data } = await updateExamStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cập nhật trạng thái thi tuyển cho ứng viên thành công!");

      this.handleClose();
      this.pagingExamCandidates();
    }
    catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  handleOpenFailPopup = () => {
    this.openFailPopup = true;
  }

  handleConfirmFailResult = async (formValues) => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error("Không có ứng viên nào được chọn FAIL");
        return;
      }

      const payload = {
        candidateIds: this.getSelectedIds(),
        examStatus: LocalConstants.CandidateExamStatus.FAILED.value,
      };

      const { data } = await updateExamStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cập nhật trạng thái thi tuyển cho ứng viên thành công!");

      await this.pagingExamCandidates();
      this.handleClose();
    }
    catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  handleOpenResetPopup = () => {
    this.openResetPopup = true;
  }

  handleConfirmResetResult = async (formValues) => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error("Không có ứng viên nào được chọn ĐẶT LẠI KẾT QUẢ THI");
        return;
      }

      const payload = {
        candidateIds: this.getSelectedIds(),
        examStatus: LocalConstants.CandidateExamStatus.NOT_TESTED_YET.value,
      };

      const { data } = await updateExamStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cập nhật trạng thái thi tuyển cho ứng viên thành công!");

      this.handleClose();
      this.pagingExamCandidates();
    }
    catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }
}
