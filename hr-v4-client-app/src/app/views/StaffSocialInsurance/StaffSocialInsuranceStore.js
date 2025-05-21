import { makeAutoObservable } from "mobx";
import {
  pagingStaffSocialInsurance,
  saveStaffSocialInsurance,
  deleteMultiple,
  deleteStaffSocialInsurance,
  updateStaffSocialInsurancePaidStatus,
  getStaffSocialInsuranceById,
  exportBHXH,
  generateSocialInsuranceTicketsForStaffsBySalaryPeriod,
  generateSingleSocialInsuranceTicket
} from "./StaffSocialInsuranceService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { StaffSocialInsurance } from "app/common/Model/StaffSocialInsurance";
import _ from "lodash";
import { saveAs } from "file-saver";
import { SearchStaffSocialInsurance } from "app/common/Model/SearchObject/SearchStaffSocialInsurance";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class StaffSocialInsuranceStore {
  intactSearchObject = {
    ... new SearchStaffSocialInsurance (),
    pageIndex:1,
    pageSize:10,
    keyword:null,
    salaryPeriod:null,
    salaryResult:null,
    paidStatus:0,
    staff:null,
  };
  isAdmin = false;
  isUser = false; //tách ra để xử lý bất đồng bộ
  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listStaffSocialInsurance = [];
  openCreateEditPopup = false;
  selectedStaffSocialInsurance = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openViewStaffSocialInsurance = false;
  listOnDelete = [];
  openAutoCreateInsuranceTicketPopup = false;
  openCreateSingleInsuranceTicketPopup = false;

  createInsuranceTicket = null;

  constructor () {
    makeAutoObservable (this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listStaffSocialInsurance = [];
    this.openCreateEditPopup = false;
    this.selectedStaffSocialInsurance = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];

    this.openAutoCreateInsuranceTicketPopup = false;
    this.openCreateSingleInsuranceTicketPopup = false;

    this.createInsuranceTicket = null;
  };

  //lọc theo trạng thái trả bảo hiểm = thay đổi tab
  handleChangeViewPaidStatus = (status) => {
    const so = {... this.searchObject, paidStatus:status};
    this.searchObject = so;
  };

  handleSetSearchObject = (searchObject) => {
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    } else {
      searchObject.departmentId = searchObject.department.id;
    }
    this.searchObject = {... searchObject};
  };

  checkAdmin = () => {
    let roles = localStorageService.getLoginUser ()?.user?.roles?.map ((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN"];

    if (roles.some ((role) => auth.indexOf (role) !== -1)) {
      this.isAdmin = true;
      this.isUser = false;
    } else {
      this.isAdmin = false;
      this.isUser = true;
    }
  };

  mapTabToStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab == 0) return null;
    // tab 1 => Chưa chi trả
    if (tab == 1) return LocalConstants.StaffSocialInsurancePaidStatus.UNPAID.value;
    // tab 2 => Đã chi trả
    if (tab == 2) return LocalConstants.StaffSocialInsurancePaidStatus.PAID.value;

    return null;
  }

  pagingStaffSocialInsurance = async () => {
    try {
      const payload = {
        ... this.searchObject,
        paidStatus:this.mapTabToStatus (this.searchObject.paidStatus),
        contractOrganizationId:this.searchObject.contractOrganization?.id || null,
        contractOrganization:null
      };

      const data = await pagingStaffSocialInsurance (payload);

      this.listStaffSocialInsurance = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingStaffSocialInsurance ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingStaffSocialInsurance ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleSelectListDelete = (deleteStaffSocialInsurances) => {
    this.listOnDelete = deleteStaffSocialInsurances;
  };

  getById = async (staffSocialInsuranceId) => {
    try {
      const {data} = await getStaffSocialInsuranceById (staffSocialInsuranceId);
      this.selectedStaffSocialInsurance = data;
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    this.openConfirmChangeStatus = false;
    this.onChooseStatus = null;
    this.openViewStaffSocialInsurance = false;
    this.openAutoCreateInsuranceTicketPopup = false;
    this.openCreateSingleInsuranceTicketPopup = false;

    this.createInsuranceTicket = null;

    this.pagingStaffSocialInsurance ();
  };

  handleDelete = (salaryItem) => {
    this.selectedStaffSocialInsurance = {... salaryItem};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (staffSocialInsuranceId) => {
    try {
      if (staffSocialInsuranceId) {
        const {data} = await getStaffSocialInsuranceById (staffSocialInsuranceId);
        this.selectedStaffSocialInsurance = {
          ... JSON.parse (JSON.stringify (data)),
        };
      } else {
        this.selectedStaffSocialInsurance = {
          ... new StaffSocialInsurance (),
        };
      }

      this.openCreateEditPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteStaffSocialInsurance (this?.selectedStaffSocialInsurance?.id);
      toast.success (i18n.t ("toast.delete_success"));

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

      // console.log("deleteData", deleteData)
      await deleteMultiple (deleteData);
      toast.success (i18n.t ("toast.delete_success"));

      this.listOnDelete = [];

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  saveStaffSocialInsurance = async (salaryItem) => {
    try {
      const {data} = await saveStaffSocialInsurance (salaryItem);
      toast.success ("Thông tin Đóng bảo hiểm của nhân viên đã được lưu");
      this.handleClose ();

      return data;
    } catch (error) {
      console.error (error);
      // if (error.response.status == 409) {
      //   toast.error("Mã Đóng bảo hiểm đã được sử dụng, vui lòng sử dụng mã Đóng bảo hiểm khác", {
      //     autoClose: 5000,
      //     draggable: false,
      //     limit: 5,
      //   });
      // }
      // else if (error.response.status == 304) {
      //   toast.warning("Đóng bảo hiểm mặc định của hệ thống không được phép chỉnh sửa", {
      //     autoClose: 5000,
      //     draggable: false,
      //     limit: 5,
      //   });
      // }
      // else {
      toast.error (i18n.t ("toast.error"));
      // }

      throw new Error (i18n.t ("toast.error"));
    }
  };

  getPaidStatusName = (status) => {
    return LocalConstants.StaffSocialInsurancePaidStatus.getListData ().find ((i) => i.value == status)?.name;
  };

  // update paid status
  openConfirmChangeStatus = false;
  onChooseStatus = false;

  handleOpenConfirmChangeStatus = (onChooseStatus) => {
    this.openConfirmChangeStatus = true;
    this.onChooseStatus = onChooseStatus;
  };

  handleExportBHXH = async () => {
    try {
      const payload = {
        ... this.searchObject,
        // organizationId: loggedInStaff?.user?.org?.id
      };
      if (!payload?.paidStatus || payload?.paidStatus == 0) payload.paidStatus = null;

      const res = await exportBHXH (payload);
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Dữ liệu BHXH.xlsx");
      toast.success (i18n.t ("general.successExport"));
    } catch (error) {
      console.error ("Error downloading timesheet detail template:", error);
    }
  };

  handleRemoveActionItem = (onRemoveId) => {
    this.listOnDelete = this?.listOnDelete?.filter ((item) => item?.id !== onRemoveId);
  };

  handleConfirmChangeStatus = async () => {
    try {
      if (this?.listOnDelete?.length <= 0) {
        toast.error ("Không có bản ghi nào được chọn");
        this.handleClose ();
        return;
      }

      const payload = {
        chosenRecordIds:this.getSelectedIds (),
        paidStatus:this.onChooseStatus,
      };

      const {data} = await updateStaffSocialInsurancePaidStatus (payload);
      if (!data) throw new Error ("");

      toast.success ("Cập nhật trạng thái thành công!");

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listOnDelete?.forEach (function (candidate) {
      ids.push (candidate?.id);
    });

    return ids;
  };

  handleViewStaffSocialInsurance = async (staffSocialInsuranceId) => {
    try {
      const {data} = await getStaffSocialInsuranceById (staffSocialInsuranceId);
      this.selectedStaffSocialInsurance = _.cloneDeep (data);
      this.openViewStaffSocialInsurance = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };


  handleOpenAutoCreateInsuranceTicketPopup = () => {
    this.createInsuranceTicket = {
      ... this.searchObject
    };
    this.openAutoCreateInsuranceTicketPopup = true;
  }


  handleOpenCreateSingleInsuranceTicketPopup = () => {
    this.createInsuranceTicket = {
      ... this.searchObject
    };
    this.openCreateSingleInsuranceTicketPopup = true;
  }


  handleGenerateSocialInsuranceTicketsForStaffsBySalaryPeriod = async (values) => {
    if (!values?.salaryPeriod?.id) {
      toast.info ("Chưa chọn kỳ lương để tạo phiếu bảo hiểm");
      return;
    }

    toast.info ("Phiếu bảo hiểm đang được tạo, vui lòng đợi", {
      autoClose:5555,
      draggable:false,
      limit:5,
    });

    try {
      const payload = {
        ... values,
        salaryPeriodId:values?.salaryPeriod?.id,

      };

      console.log ("payload", payload);

      const {data} = await generateSocialInsuranceTicketsForStaffsBySalaryPeriod (payload);

      toast.dismiss ();

      toast.success (`Đã tạo thành công ${data?.length} phiếu BHXH`, {
        autoClose:4444
      });

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.dismiss ();
      toast.error ("Có lỗi xảy ra khi tạo phiếu BHXH");
    }
  };

  handleGenerateSingleSocialInsuranceTicket = async (values) => {
    if (!values?.salaryPeriod?.id) {
      toast.info ("Chưa chọn kỳ lương để tạo phiếu bảo hiểm");
      return;
    }

    if (!values?.staff?.id) {
      toast.info ("Chưa chọn nhân viên để tạo phiếu bảo hiểm");
      return;
    }

    toast.info ("Phiếu bảo hiểm đang được tạo, vui lòng đợi", {
      autoClose:5555,
      draggable:false,
      limit:5,
    });

    try {
      const payload = {
        ... values,
        salaryPeriodId:values?.salaryPeriod?.id,

      };

      console.log ("payload", payload);

      const {data} = await generateSingleSocialInsuranceTicket (payload);

      toast.dismiss ();

      toast.success (`Đã tạo thành công phiếu BHXH`);

      this.handleClose ();

    } catch (error) {
      console.error (error);
      toast.dismiss ();
      toast.error ("Có lỗi xảy ra khi tạo phiếu BHXH");
    }
  };


}
