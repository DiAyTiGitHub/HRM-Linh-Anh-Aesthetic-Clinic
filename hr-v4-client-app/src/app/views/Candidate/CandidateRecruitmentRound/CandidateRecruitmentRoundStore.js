import { makeAutoObservable } from "mobx";
import {
  pagingCandidateRecruitmentRound,
  getById,
  saveCandidateRecruitmentRound,
  deleteMultiple,
  deleteCandidateRecruitmentRound,
  updateRecruitmentRoundResult,
  moveToNextRecruitmentRound,
  doActionAssignment,
  passToNextRound,
  rejectCandidateRound,
  getCandidateRoundByCandidateId,
  passListToNextRound,
} from "./CandidateRecruitmentRoundService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { SearchCandidateRecruitmentRound } from "app/common/Model/SearchObject/SearchCandidateRecruitmentRound";
import { CandidateRecruitmentRound } from "app/common/Model/Candidate/CandidateRecruitmentRound";
import LocalConstants, { HttpStatus } from "app/LocalConstants";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class CandidateRecruitmentRoundStore {
  intactSearchObject = {
    ... new SearchCandidateRecruitmentRound (),
  };
  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listCandidateRecruitmentRound = [];

  openCreateEditPopup = false;
  openEvaluationCandidateRound = false;
  selectedCandidateRecruitmentRound = new CandidateRecruitmentRound ();
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listOnDelete = [];

  openUpdateResultPopup = false;
  openMoveToNextRoundPopup = false;

  tabIndex = 0;
  responseData = []

  setTabIndex = async (index, recruitmentRoundId) => {
    this.tabIndex = index;

    // update searchObject with correct recruitmentRound
    const newSO = {
      ... this.searchObject,
      recruitmentRoundId:recruitmentRoundId,
    };

    this.handleSetSearchObject (newSO);

    await this.pagingCandidateRecruitmentRound ();
  };

  constructor () {
    makeAutoObservable (this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listCandidateRecruitmentRound = [];
    this.openCreateEditPopup = false;
    this.openEvaluationCandidateRound = false;
    this.selectedCandidateRecruitmentRound = new CandidateRecruitmentRound ();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.tabIndex = 0;
    this.listOnDelete = [];
    this.responseData = []
  };

  handleSetSearchObject = (searchObject) => {
    this.searchObject = {... searchObject};
  };

  pagingCandidateRecruitmentRound = async (dto) => {
    try {
      const loggedInStaff = localStorageService.getLoginUser ();

      // console.log("searchObject: ", this.searchObject);
      const payload = {
        ... this.searchObject,
        organizationId:loggedInStaff?.user?.org?.id,
        ... dto,
      };
      // console.log("payload: ", payload);

      if (!payload?.recruitmentRoundId) {
        console.log ("catched in return: ", payload);
        return;
      }
      const data = await pagingCandidateRecruitmentRound (payload);

      this.listCandidateRecruitmentRound = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  getCandidateRoundByCandidateId = async (candidateId) => {
    try {
      if (candidateId) {
        const {data} = await getCandidateRoundByCandidateId (candidateId);
        this.listCandidateRecruitmentRound = data;
      }
    } catch (error) {
      toast.error ("Có lỗi xảy ra");
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingCandidateRecruitmentRound ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingCandidateRecruitmentRound ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleSelectListDelete = (deleteCandidateRecruitmentRounds) => {
    this.listOnDelete = deleteCandidateRecruitmentRounds;
  };

  getById = async (recruitmentId) => {
    try {
      if (!recruitmentId) {
        this.selectedCandidateRecruitmentRound = new CandidateRecruitmentRound ();
        return;
      }

      const {data} = await getById (recruitmentId);

      this.selectedCandidateRecruitmentRound = data;
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleClose = async () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;

    this.openUpdateResultPopup = false;
    this.openMoveToNextRoundPopup = false;
    this.listOnDelete = [];
    this.responseData = []
    await this.pagingCandidateRecruitmentRound ();
  };

  handleDelete = (position) => {
    this.selectedCandidateRecruitmentRound = {... position};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleSelectRound = (round) => {
    this.selectedCandidateRecruitmentRound = {... round};
  };

  handleOpenCreateEdit = async (candidateRecruitmentRound) => {
    try {
      if (!candidateRecruitmentRound?.id) {
        // Trường hợp thêm mới
        this.selectedCandidateRecruitmentRound = new CandidateRecruitmentRound ();
      } else {
        // Trường hợp chỉnh sửa - lấy dữ liệu từ API
        const {data} = await getById (candidateRecruitmentRound?.id);
        this.selectedCandidateRecruitmentRound = data;
      }
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleCloseEvaluationCandidateRound = () => {
    this.openEvaluationCandidateRound = false;
  };

  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteCandidateRecruitmentRound (this?.selectedCandidateRecruitmentRound?.id);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingCandidateRecruitmentRound ();

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        deleteData.push (this?.listOnDelete[i]?.id);
      }

      await deleteMultiple (deleteData);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingCandidateRecruitmentRound ();
      this.listOnDelete = [];

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  saveCandidateRecruitmentRound = async (payload) => {
    try {
      const {data} = await saveCandidateRecruitmentRound (payload);
      toast.success ("Thông tin quá trình tuyển dụng đã được lưu");
      this.handleClose ();

      return data;
    } catch (error) {
      console.error (error);
      // toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");

      if (error.response.status == 409) {
        toast.error ("Ứng viên đã được ghi nhận tại vòng tuyển dụng, vui lòng kiểm tra lại thông tin", {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      } else {
        toast.error (i18n.t ("toast.error"));
      }

      throw new Error (i18n.t ("toast.error"));
    }
  };

  handleRemoveActionItem = (onRemoveId) => {
    this.listOnDelete = this?.listOnDelete?.filter ((item) => item?.id !== onRemoveId);
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listOnDelete?.forEach (function (item) {
      ids.push (item?.id);
    });

    return ids;
  };

  handleOpenUpdateResultPopup = () => {
    this.openUpdateResultPopup = true;
  };

  handleConfirmUpdateResult = async (formValues) => {
    try {
      if (this?.listOnDelete?.length <= 0) {
        toast.error ("Không có ứng viên nào được chọn");
        return;
      }

      const payload = {
        chosenRecordIds:this.getSelectedIds (),
        result:formValues?.result,
        note:formValues?.note,
      };

      const {data} = await updateRecruitmentRoundResult (payload);
      console.log ("checking data: ", data);
      if (!data) throw new Error ("");

      toast.success ("Cập nhật kết quả vòng thi thành công!");

      // await this.pagingCandidateRecruitmentRound();
      await this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleOpenMoveToNextRoundPopup = () => {
    this.openMoveToNextRoundPopup = true;
  };

  handleConfirmMoveToNextRound = async (formValues) => {
    try {
      if (this?.listOnDelete?.length <= 0) {
        toast.error ("Không có ứng viên nào được chọn");
        return;
      }

      const payload = {
        chosenRecordIds:this.getSelectedIds (),
        actualTakePlaceDate:formValues?.actualTakePlaceDate,
        examPosition:formValues?.examPosition,
      };

      const {data} = await moveToNextRecruitmentRound (payload);
      if (!data) throw new Error ("");

      toast.success ("Đã đăng ký ứng viên cho vòng thi tiếp theo!");

      // await this.pagingCandidateRecruitmentRound();
      await this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  isLastRecruitmentRound = (recruitment) => {
    return (
        this?.searchObject?.recruitmentRoundId ==
        recruitment?.recruitmentRounds[recruitment?.recruitmentRounds?.length - 1]?.id
    );
  };

  doActionAssignment = async (crrId, status) => {
    const response = await doActionAssignment (crrId, status);
    if (response.status === HttpStatus.OK) {
      if (response.data?.status === HttpStatus.OK) {
        toast.success (response.data?.message);
      } else {
        toast.warning (response.data?.message);
      }
    }
  };
  passToNextRound = async (crrId) => {
    const response = await passToNextRound (crrId);
    if (response.status === HttpStatus.OK) {
      if (response.data?.status === HttpStatus.OK) {
        toast.success (response.data?.message);
      } else {
        toast.warning (response.data?.message);
      }
    }
  };

  passListToNextRound = async (searchObject) => {
    const response = await passListToNextRound (searchObject);
    if (response.status === HttpStatus.OK) {
      if (response.data?.status === HttpStatus.OK) {
        this.responseData = response.data?.data;
        toast.success (response.data?.message);
      } else {
        toast.warning (response.data?.message);
      }
    } else {
      toast.warning (i18n.t ("toast.error"));
    }
  };

  rejectCandidateRound = async (crrId) => {
    const response = await rejectCandidateRound (crrId);
    if (response.status === HttpStatus.OK) {
      if (response.data?.status === HttpStatus.OK) {
        toast.success (response.data?.message);
      } else {
        toast.warning (response.data?.message);
      }
    }
  };

  handleClearResponseData = () => {
    this.responseData = [];
  }
}
