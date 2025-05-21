import { makeAutoObservable } from "mobx";
import {
  deleteMultiple,
  deleteRecruitmentPlan,
  downloadRecruitmentPlanTemplate,
  downloadRecruitmentRoundTemplate,
  getById,
  importRecruitmentPlan,
  importRecruitmentRound,
  pagingRecruitmentPlan,
  saveRecruitmentPlan,
  updatePlanStatus,
  autoGenCode
} from "./RecruitmentPlanV2Service";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants, {HttpStatus} from "app/LocalConstants";
import { RecruitmentPlan } from "app/common/Model/Recruitment/RecruitmentPlan";
import { saveAs } from "file-saver";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class RecruitmentPlanStore {
  intactSearchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    status:0,
    departmentId:null,
    department:null,
    recruitmentRequestId:null,
    recruitmentRequest:null,
    fromDate:null,
    toDate:null,
    organization:null,
    positionTitle:null,
    positionTitleId:null,
  };

  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listRecruitmentPlans = [];
  openCreateEditPopup = false;
  selectedRecruitmentPlan = new RecruitmentPlan ();
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listChosen = [];
  openConfirmUpdateStatusPopup = false;
  onUpdateStatus = null;
  openChooseMultipleCandidatesPopup = false;
  openCreateLink = false;

  constructor () {
    makeAutoObservable (this);
  }

  handleOpenChooseMultipleCandidatesPopup = (state) => {
    this.openChooseMultipleCandidatesPopup = state;
  }
  resetStore = () => {
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listRecruitmentPlans = [];
    this.openCreateEditPopup = false;
    this.selectedRecruitmentPlan = new RecruitmentPlan ();
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listChosen = [];
    this.openConfirmUpdateStatusPopup = false;
    this.onUpdateStatus = null;
    this.openChooseMultipleCandidatesPopup = false;

  };

  handleChangePagingStatus = (status) => {
    const so = {... this.searchObject, status:status};
    this.searchObject = so;
  };

  setFieldSelected = (field) => {
    this.selectedRecruitmentPlan = {
      ... new RecruitmentPlan (),
      ... field,
    };
    this.openCreateEditPopup = true;
  };

  handleSetSearchObject = (searchObject) => {
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

    if (searchObject.recruitmentRequest == null) {
      searchObject.recruitmentRequestId = null;
    } else {
      searchObject.recruitmentRequestId = searchObject.recruitmentRequest.id;
    }

    this.searchObject = {... searchObject};
  };

  mapTabToStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab == 0) return null;
    // tab 1 => Chưa phê duyệt
    if (tab == 1) return LocalConstants.RecruitmentPlanStatus.NOT_APPROVED_YET.value;
    // tab 2 => Đã Phê duyệt
    if (tab == 2) return LocalConstants.RecruitmentPlanStatus.APPROVED.value;
    // tab 3 => Đã từ chối
    if (tab == 3) return LocalConstants.RecruitmentPlanStatus.REJECTED.value;
    // tab 4 => Đã hoàn thành
    if (tab == 4) return LocalConstants.RecruitmentPlanStatus.COMPLETED.value;

    return null;
  };

  pagingRecruitmentPlan = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser ();
      const payload = {
        ... this.searchObject,
        status:this.mapTabToStatus (this.searchObject.status),
        // organizationId: this.searchObject?.organization?.id ?? loggedInStaff?.user?.org?.id
      };
      const data = await pagingRecruitmentPlan (payload);

      this.listRecruitmentPlans = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingRecruitmentPlan ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingRecruitmentPlan ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleSelectListDelete = (deleteRecruitmentPlans) => {
    this.listChosen = deleteRecruitmentPlans;
  };

  getById = async (recruitmentRequestId) => {
    try {
      if (!recruitmentRequestId) {
        this.selectedRecruitmentPlan = new RecruitmentPlan ();
        return;
      }

      const {data} = await getById (recruitmentRequestId);
      this.selectedRecruitmentPlan = data;
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmUpdateStatusPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.onUpdateStatus = null;
    this.listChosen = [];
    this.openChooseMultipleCandidatesPopup = false;
  };

  handleDelete = (position) => {
    this.selectedRecruitmentPlan = {... position};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };
  handleOpenCreateLink = (value) => {
    this.openCreateLink = value;
  };

  handleOpenCreateEdit = async (plan) => {
    try {
      if (plan) {
        const {data} = await getById (plan?.id);
        this.selectedRecruitmentPlan = data;
      } else {
        this.selectedRecruitmentPlan = {
          ... new RecruitmentPlan (),
        };
      }

      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleOpenOnlyCreate = (field) => {
    this.setFieldSelected (field);
    this.openCreateEditPopup = true;
  };
  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteRecruitmentPlan (this?.selectedRecruitmentPlan?.id);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingRecruitmentPlan ();

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listChosen?.length; i++) {
        deleteData.push (this?.listChosen[i]?.id);
      }

      await deleteMultiple (deleteData);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingRecruitmentPlan ();
      this.listChosen = [];

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  saveRecruitmentPlan = async (plan) => {
    try {
      const {data} = await saveRecruitmentPlan (plan);
      toast.success ("Thông tin Kế hoạch tuyển dụng đã được lưu");
      this.handleClose ();

      return data;
    } catch (error) {
      console.error (error);
      // toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");

      if (error.response.status == 409) {
        toast.error ("Mã kế hoạch đã được sử dụng, vui lòng sử dụng mã kế hoạch khác", {
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
    this.listChosen = this?.listChosen?.filter ((item) => item?.id !== onRemoveId);
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listChosen?.forEach (function (candidate) {
      ids.push (candidate?.id);
    });

    return ids;
  };

  handleOpenConfirmUpdateStatusPopup = (status) => {
    this.onUpdateStatus = status;
    this.openConfirmUpdateStatusPopup = true;
  };

  handleConfirmUpdateStatus = async () => {
    try {
      if (this?.listChosen?.length <= 0) {
        toast.error ("Không có kế hoạch tuyển dụng nào được chọn");
        return;
      }

      if (this.onUpdateStatus == null) {
        throw new Error ("On update status in invalid");
      }
      const payload = {
        chosenIds:this.getSelectedIds (),
        status:this.onUpdateStatus,
      };

      const {data} = await updatePlanStatus (payload);
      if (!data) throw new Error ("");

      toast.success ("Cập nhật trạng thái thành công!");

      this.onUpdateStatus = null;

      this.handleClose ();
      await this.pagingRecruitmentPlan ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  uploadFileExcel = async (event) => {
    const fileInput = event.target; // Lưu lại trước
    const file = fileInput.files[0];
    let message = "Nhập excel thất bại";

    try {
      await importRecruitmentPlan (file);
      toast.success ("Nhập excel thành công");
      this.pagingRecruitmentPlan ();
    } catch (error) {
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'string') {
          message = data;
        } else if (data.message) {
          message = data.message;
        }
      }
      toast.error (message);
    } finally {
      this.handleClose ();
      fileInput.value = null;
    }
  };

  handleDownloadRecruitmentTemplate = async () => {
    try {
      const res = await downloadRecruitmentPlanTemplate ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu import kế hoạch tuyển dụng.xlsx");
      toast.success ("Đã tải mẫu import kế hoạch tuyển dụng thành công");
    } catch (error) {
      toast.error ("Tải mẫu import kế hoạch tuyển dụng thất bại");
      console.error (error);
    }
  };

  uploadFileRecruitmentRoundExcel = async (event) => {
    const fileInput = event.target; // Lưu lại trước
    const file = fileInput.files[0];
    let message = "Nhập excel thất bại";

    try {
      await importRecruitmentRound (file);
      toast.success ("Nhập excel thành công");
      this.pagingRecruitmentPlan ();
    } catch (error) {
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'string') {
          message = data;
        } else if (data.message) {
          message = data.message;
        }
      }
      toast.error (message);
    } finally {
      this.handleClose ();
      fileInput.value = null;
    }
  };

  handleDownloadRecruitmentRoundTemplate = async () => {
    try {
      const res = await downloadRecruitmentRoundTemplate ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu import vòng tuyển dụng.xlsx");
      toast.success ("Đã tải mẫu import vòng tuyển dụng thành công");
    } catch (error) {
      toast.error ("Tải mẫu import vòng tuyển dụng thất bại");
      console.error (error);
    }
  };
  autoGenCode = async (configKey) =>{
    const response = await autoGenCode(configKey)
    if(response.status === HttpStatus.OK){
      return response.data;
    }
  }
}


