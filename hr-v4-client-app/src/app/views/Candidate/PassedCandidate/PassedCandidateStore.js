import { makeAutoObservable } from "mobx";
import {
  pagingPassedCandidates,
  updateReceptionStatus
} from "./PassedCandidateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { Candidate } from "app/common/Model/Candidate/Candidate";
import LocalConstants from "app/LocalConstants";
import { SearchObjectCandidate } from "app/common/Model/SearchObject/SearchObjectCandidate";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class PassedCandidateStore {
  intactSearchObject = {
    ...new SearchObjectCandidate(),
    receptionStatus: 0,
  };

  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  totalElements = 0;
  totalPages = 0;
  listPassedCandidates = [];
  selectedCandidate = new Candidate();
  listChosen = [];

  openReceptPopup = false;
  openRejectPopup = false;
  openResetPopup = false;

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {

    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    this.totalElements = 0;
    this.totalPages = 0;
    this.listPassedCandidates = [];
    this.openCreateEditPopup = false;
    this.selectedCandidate = new Candidate();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatus = false;
    this.openReceptPopup = false;
    this.openRejectPopup = false;
    this.openResetPopup = false;
  }

  //lọc theo trạng thái kết quả thi tuyển = thay đổi tab
  handleChangePagingStatus = (status) => {
    const so = { ...this.searchObject, receptionStatus: status };
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

  pagingPassedCandidates = async () => {
    try {
      const searchData = { ...this?.searchObject };
      if (!searchData?.receptionStatus || searchData?.receptionStatus == 0) searchData.receptionStatus = null;

      const data = await pagingPassedCandidates(searchData);

      this.listPassedCandidates = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingPassedCandidates();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingPassedCandidates();
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
    this.openConfirmUpdateStatus = false;
    this.openReceptPopup = false;
    this.openRejectPopup = false;
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

  handleOpenReceptPopup = () => {
    this.openReceptPopup = true;
  }

  handleConfirmReceptCandidate = async (formValues) => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error("Không có ứng viên nào được chọn CHỜ NHẬN VIỆC");
        return;
      }

      const payload = {
        candidateIds: this.getSelectedIds(),
        receptionStatus: LocalConstants.CandidateReceptionStatus.RECEPTED.value,
        onboardDate: formValues?.onboardDate // ngày tiếp nhận ứng viên
      };

      const { data } = await updateReceptionStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cập nhật trạng thái tiếp nhận cho ứng viên thành công!");

      this.handleClose();
      this.pagingPassedCandidates();
    }
    catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  handleOpenRejectPopup = () => {
    this.openRejectPopup = true;
  }

  handleConfirmRejectCandidate = async (formValues) => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error("Không có ứng viên nào được chọn TỪ CHỐI");
        this.handleClose();
        return;
      }

      const payload = {
        candidateIds: this.getSelectedIds(),
        receptionStatus: LocalConstants.CandidateReceptionStatus.REJECTED.value,
        refusalReason: formValues?.refusalReason
      };

      const { data } = await updateReceptionStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cập nhật trạng thái tiếp nhận ứng viên thành công!");

      this.handleClose();
      this.pagingPassedCandidates();
    }
    catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  handleOpenResetPopup = () => {
    this.openResetPopup = true;
  }

  handleConfirmResetCandidate = async (formValues) => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error("Không có ứng viên nào được chọn TỪ CHỐI");
        this.handleClose();
        return;
      }

      const payload = {
        candidateIds: this.getSelectedIds(),
        receptionStatus: LocalConstants.CandidateReceptionStatus.NOT_RECEPTED_YET.value,
      };

      const { data } = await updateReceptionStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cài lại trạng thái tiếp nhận thành công!");

      this.handleClose();
      this.pagingPassedCandidates();
    }
    catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  getReceptionStatusName = (status) => {
    return LocalConstants.CandidateReceptionStatus.getListData().find(i => i.value == status)?.name;
  }
}
