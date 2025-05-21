import { makeAutoObservable } from "mobx";
import {
  pagingStaff,
  getStaff,
  createStaff,
  editStaff,
  deleteStaff,
  uploadImage,
  pagingTimeSheetDetail,
  exportToExcel,
  saveStaffWithoutAccount,
  getAllStaff,
  createUsersForStaff,
  exportStaffLaborReportExcel,
  exportExcelListStaff,
  importExcelListStaff,
  exportExcelListHrIntroduceCost,
  exportExcelTemplateImportStaff,
  checkStaffTaxCode,
  checkStaffHealthInsuranceNumber,
  checkStaffSocialInsuranceNumber,
  dismissStaffPositions,
  generateFixScheduleForChosenStaffs,
  calculateRemaininAnnualLeave,
  importExcelListNewStaff,
  exportLaborManagementBook,
} from "./StaffService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import {
  deleteTimeSheetDetail,
  getTimeSheetDetail,
  searchTimeSheetDate,
} from "../TimeSheetDetails/TimeSheetDetailsService";
import { getFullYear, getMonth } from "app/LocalFunction";
import { saveAs } from "file-saver";
import { SearchObject, SearchStaff } from "app/common/Model/SearchObject/SearchObject";
import history from "../../../history";
import { getRecentStaffWorkingHistory } from "../StaffWorkingHistory/StaffWorkingHistoryService";
import localStorageService from "app/services/localStorageService";
import { SearchObjectStaff } from "app/common/Model/SearchObject/SearchObjectStaff";
import { downloadOrganizationTemplate } from "../Organization/OrganizationService";
import { getByStaffId, pagingPosition, getById } from "../Position/PositionService";
import { sanitizeData } from "../OrganizationDiagram/components/utils";
import { Staff } from "app/common/Model/Staff";

export default class StaffStore {
  staffList = [];
  selectedStaff = null;
  selectedStaffList = [];
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  searchObject = new SearchObjectStaff ();
  searchStaff = new SearchObjectStaff ();

  totalElementsTimeSheet = 0;
  totalPagesTimeSheet = 0;
  pageTimeSheet = 1;
  rowsPerPageTimeSheet = 10;
  timeSheetList = [];
  listTimeSheetMonthByStaff = [];
  listTimeSheetWeeksByStaff = [];
  listTimeSheetDayByStaff = [];
  listWorkingStatus = [];
  loadingInitial = false;
  shouldOpenEditorDialog = false;
  shouldOpenConfirmationDialog = false;
  shouldOpenCreateUserDialog = false;
  shouldOpenConfirmationDeleteListDialog = false;
  shouldOpenImportExcelDialog = false;
  shouldOpenConfirmationDialogTimeSheetDetail = false;
  shouldOpenEditorDialogTimeSheetDetail = false;
  shouldOpenTranserDialog = false;
  shouldOpenCreateUsers = false;
  selectedTimeSheetDetail = null;
  listCreateAbleUsers = [];
  shouldOpenConfirmationCreateUsers = false;
  recentSWH = null;
  selectedPosition = null;
  pageIndexTimeSheet = 1;
  pageSizeTimeSheet = 10;
  opentFormLeavePosition = false;
  openFixShiftDateRangePopup = false;

  // searchStaff = {
  //     tab: null,
  //     civilServantTypeId: null,
  //     employeeStatusId: null,
  //     departmentId: null,
  //     keyword: null,
  //     contractOrganization: null,
  //     position: null,
  //     workOrganization: null,
  //     contractOrganizationId: null,
  //     positionId: null,
  //     workOrganizationId: null,
  // };
  pageStaff = null;
  openPopupConfirmDownloadExcelListStaff = false;
  tabIndexValue = 0;
  shouldOpenFormCreate = false;
  shouldDismissPositions = false;
  staffStatusDismiss = null;
  remainingLeave = null;

  constructor () {
    makeAutoObservable (this);
  }

  resetSearchStaff = async () => {
    this.searchStaff = new SearchObjectStaff ();
    await this.onPagingStaff ();
  };
  setTabIndexValue = (tab) => {
    this.tabIndexValue = tab;
  };

  setShouldOpenFormCreate = async (state) => {
    this.shouldOpenFormCreate = state;
  };

  setShouldDismissPositions = (state) => {
    this.shouldDismissPositions = state;
  };

  setStaffStatusDismiss = (status) => {
    this.staffStatusDismiss = status;
  };

  handleDismissPositions = async () => {
    try {
      const newValue = {
        id:this.selectedStaff?.id || null,
        status:this.staffStatusDismiss,
      };
      await dismissStaffPositions (newValue);
      toast.success ("Đã bãi nhiệm chức vụ của nhân viên thành công");
    } catch (e) {
      console.log (e);
      toast.error ("Có lỗi xảy ra khi bãi nhiện chức vụ của nhân viên");
    }
  };

  getAllStaffsOfProject = async (projectId) => {
    try {
      const searchObj = {
        pageIndex:1,
        pageSize:1000000,
        projectId:projectId,
        includeVoidedInProject:true,
      };

      const {data} = await pagingStaff (searchObj);
      this.staffList = data?.content;
    } catch (error) {
      console.error (error);
      toast.error ("Có lỗi xảy ra khi lấy dữ liệu nhân viên");
    }
  };

  onWorkingStaffList = [];
  onModifierList = [];
  //get all staffs of project who are'nt voided
  getOnWorkingStaffsOfProject = async (projectIdList) => {
    try {
      const searchObj = {
        pageIndex:1,
        pageSize:1000000,
        projectIdList:projectIdList,
        includeVoidedInProject:false,
      };

      //handle for case get staff for none project and all projects
      let isGetAllStaff = false;
      for (let i = 0; i < projectIdList?.length; i++) {
        const projectId = projectIdList[i];

        if (projectId?.includes ("none-project") || projectId?.includes ("all-project")) {
          isGetAllStaff = true;
          break;
        }
      }
      if (isGetAllStaff || projectIdList?.length == 0) searchObj.projectIdList = undefined;

      const {data} = await pagingStaff (searchObj);
      this.onWorkingStaffList = data?.content;
      this.onModifierList = data?.content;
    } catch (error) {
      console.error (error);
      toast.error ("Có lỗi xảy ra khi lấy dữ liệu nhân viên");
    }
  };

  onPagingStaff = async () => {
    try {
      const payload = {
        ... this.searchStaff,
      };
      const res = await pagingStaff (payload);
      this.pageStaff = res.data;
    } catch (e) {
      toast.warning (i18n.t ("toast.error"));
    }
  };

  handleSetUsingStaffSO = (searchStaff) => {
    this.searchStaff = {... searchStaff};
    if (searchStaff?.department) {
      this.searchStaff.departmentId = searchStaff.department.id;
    } else {
      this.searchStaff.departmentId = null;
    }

    if (searchStaff?.organization) {
      this.searchStaff.organizationId = searchStaff.organization.id;
    } else {
      this.searchStaff.organizationId = null;
    }

    if (searchStaff?.position) {
      this.searchStaff.positionId = searchStaff.position.id;
    } else {
      this.searchStaff.positionId = null;
    }

    if (searchStaff?.contractOrganization) {
      this.searchStaff.contractOrganizationId = searchStaff.contractOrganization.id;
    } else {
      this.searchStaff.contractOrganizationId = null;
    }

    if (searchStaff?.workOrganization) {
      this.searchStaff.workOrganizationId = searchStaff.workOrganization.id;
    } else {
      this.searchStaff.workOrganizationId = null;
    }

    if (searchStaff?.positionTitle) {
      this.searchStaff.positionTitleId = searchStaff.positionTitle.id;
    } else {
      this.searchStaff.positionTitleId = null;
    }

    if (searchStaff?.fixShiftWork) {
      this.searchStaff.fixShiftWorkId = searchStaff.fixShiftWork.id;
    } else {
      this.searchStaff.fixShiftWorkId = null;
    }
  };

  onChangeFormSearch = (searchStaff) => {
    const value = SearchObject.checkSearchObject (this.searchStaff, searchStaff);
    this.searchStaff = {... value};
    this.onPagingStaff ();
  };

  handleExportExcelStaff = async () => {
    if (this.pageStaff?.totalElements > 0) {
      try {
        const res = await exportToExcel ({... this.searchStaff, isExportExcel:true});
        toast.success (i18n.t ("general.successExport"));
        let blob = new Blob ([res.data], {
          type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });

        saveAs (blob, "NhanVien.xlsx");
      } finally {
      }
    } else {
      toast.warning (i18n.t ("general.noData"));
    }
  };

  exportStaffLaborReportExcel = async () => {
    if (this.pageStaff?.totalElements > 0) {
      try {
        const res = await exportStaffLaborReportExcel ({... this.searchStaff, isExportExcel:true});
        toast.success (i18n.t ("general.successExport"));
        let blob = new Blob ([res.data], {
          type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });

        saveAs (blob, "BAO_CAO_TINH_HINH_SU_DUNG_LAO_DONG.xlsx");
      } finally {
      }
    } else {
      toast.warning (i18n.t ("general.noData"));
    }
  };


  exportExcelListHrIntroduceCost = async () => {
    if (this.pageStaff?.totalElements > 0) {
      try {
        const res = await exportExcelListHrIntroduceCost (this.searchStaff);

        if (res && res.data) {
          toast.success (i18n.t ("general.successExport"));

          let blob = new Blob ([res.data], {
            type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          });

          saveAs (blob, "DANH_SACH_PHI_GIOI_THIEU_HO_SO.xlsx");
        } else {
          toast.error ("Đã có lỗi xảy ra vui lòng thử lại");
        }
      } catch (error) {
        console.error ("Export Excel Error:", error);
        toast.error ("Đã có lỗi xảy ra vui lòng thử lại");
      }
    } else {
      toast.warning (i18n.t ("general.noData"));
    }
  };

  handleOpentCreateUserForlStaff = () => {
    this.shouldOpenCreateUsers = true;
  };

  handleCreateUsersForStaff = async () => {
    try {
      // console.log("đã vào đây");
      const {data} = await createUsersForStaff (this.selectedStaffList, false);
      console.log (data);
      this.listCreateAbleUsers = data;
      this.shouldOpenConfirmationCreateUsers = true;
      if (this.listCreateAbleUsers?.length === 0) {
        toast.success (`Tất cả nhân viên đã chọn đều đã có tài khoản`);
        this.selectedStaffList = [];
        this.handleClose ();
      }
    } catch (error) {
      console.error (error);
      toast.error ("Có lỗi xảy ra khi tạo tài khoản cho nhân viên");
    }
  };

  handleConfirmCreateUse = async () => {
    try {
      // console.log("đã vào đây");
      const {data} = await createUsersForStaff (this.listCreateAbleUsers, true);
      this.listCreateAbleUsers = data;
      toast.success (`Đã tạo tài khoản cho ${this.listCreateAbleUsers?.length} nhân viên thành công`);
      this.selectedStaffList = [];
      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error ("Có lỗi xảy ra khi tạo tài khoản cho nhân viên");
    }
  };

  resetStore = () => {
    this.searchObject = new SearchObjectStaff ();
    this.searchStaff = new SearchStaff ();
    this.pageStaff = null;
    this.selectedPosition = null;
    this.selectedStaff = null;
  };

  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  updatePageData = async (item) => {
    if (item) {
      this.searchStaff = {
        ... item,
        pageIndex:1,
        pageSize:5,
      };
      await this.onPagingStaff ();
    } else {
      console.log (this.searchStaff);
      await this.onPagingStaff ();
    }
  };

  search = async () => {
    this.loadingInitial = true;
    //console.log("searchObject", this.searchObject);
    await pagingStaff (this.searchObject)
        .then ((response) => {
          this.pageStaff = response.data? (response.data? response.data : []) : [];
          this.staffList = response.data? (response.data.content? response.data.content : []) : [];
          this.totalElements = response.data.totalElements;
        })
        .catch (() => {
          toast.warning (i18n.t ("toast.error"));
        });
    this.setLoadingInitial (false);
  };

  setPageIndex = async (pageIndex) => {
    this.searchStaff.pageIndex = pageIndex;
    await this.updatePageData ();
  };

  setPageSize = async (event) => {
    this.searchStaff.pageSize = event.target.value;
    this.searchStaff.pageIndex = 1;
    await this.updatePageData ();
  };

  handleChangePage = (event, newPage) => {
    this.setPageIndex (newPage);
  };

  handleEditStaff = (id) => {
    this.getStaff (id);
  };

  handleClose = () => {
    this.shouldOpenEditorDialog = false;
    this.shouldOpenConfirmationDialog = false;
    this.shouldOpenConfirmationDeleteListDialog = false;
    this.shouldOpenImportExcelDialog = false;
    this.shouldOpenConfirmationDialogTimeSheetDetail = false;
    this.shouldOpenEditorDialogTimeSheetDetail = false;
    this.shouldOpenTranserDialog = false;
    this.shouldOpenCreateUsers = false;
    this.shouldOpenConfirmationCreateUsers = false;
    this.shouldOpenCreateUserDialog = false;
    this.openPopupConfirmDownloadExcelListStaff = false;
    this.selectedPosition = false;
    this.opentFormLeavePosition = false;
    this.shouldOpenFormCreate = false;
    this.shouldDismissPositions = false;
    this.openFixShiftDateRangePopup = false;

    this.updatePageData ();
  };

  handleOpenFixShiftDateRangePopup = () => {
    this.openFixShiftDateRangePopup = true;
  };

  handeConfirmAssignFixShiftPopup = async (values) => {
    try {
      const staffIdList = this.getSelectedIds ();
      const payload = {
        ... values,
        staffIdList,
      };
      const {data} = await generateFixScheduleForChosenStaffs (payload);

      toast.success ("Đã tạo thành công lịch làm việc cố định cho nhân viên");

      this.handleClose ();

      return data;
    } catch (err) {
      toast.error ("Có lỗi xảy ra khi phân ca làm việc cố định");
      console.error (err); // Hoặc xử lý lỗi theo cách bạn muốn
    }
  };

  handleDelete = (id) => {
    this.getStaff (id).then (() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleCreateUser = (id) => {
    this.getStaff (id).then (() => {
      this.shouldOpenCreateUserDialog = true;
    });
  };

  handleOpenTransferPopup = (id) => {
    this.shouldOpenTranserDialog = true;
  };

  getPositionByStaffId = async (staffId) => {
    try {
      const {data} = await getByStaffId (staffId);
      this.selectedPosition = sanitizeData (data);
      console.log (this.selectedPosition);
      if (!data) {
        const result = await getStaff (staffId);
        this.selectedStaff = result.data;
      } else {
        this.selectedStaff = data.staff;
      }
      this.shouldOpenTranserDialog = true;
      return data;
    } catch (err) {
      toast.error ("Có lỗi xảy ra");
      console.error (err); // Hoặc xử lý lỗi theo cách bạn muốn
    }
  };

  getRecentSWH = async (staffId) => {
    try {
      const res = await getRecentStaffWorkingHistory (staffId);
      this.recentSWH = res.data;
    } catch (error) {
      toast.error ("Có lỗi xảy ra khi xóa nhân viên");
      console.error (error);
    }
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  handleConfirmDelete = async () => {
    try {
      await this.deleteStaff (this.selectedStaff?.id);
    } catch (error) {
      toast.error ("Có lỗi xảy ra khi xóa nhân viên");
      console.error (error);
    }
  };

  handleselectedStaff = (staff) => {
    this.selectedStaff = staff;
  };

  getStaff = async (id) => {
    this.loadingInitial = true;
    if (id != null) {
      try {
        let data = await getStaff (id);
        this.handleSelectStaff (data.data);
        this.setLoadingInitial (false);
        return this.selectedStaff;
      } catch (error) {
        console.log (error);
        toast.warning (i18n.t ("toast.error"));

        this.setLoadingInitial (false);
      }
    } else {
      this.handleSelectStaff (null);

      this.setLoadingInitial (false);
    }
  };

  handleOpentFormLeavePosition = async (staff) => {
    const data = sanitizeData (await this.getStaff (staff?.id));
    if (this.selectedStaff.hasPosition) {
      if (this.selectedStaff.mainPositionId) {
        const {data} = await getById (this.selectedStaff.mainPositionId);
        this.selectedPosition = sanitizeData (data);
      } else {
        const {data} = await getByStaffId (this.selectedStaff.id);
        this.selectedPosition = sanitizeData (data);
      }
    }
    this.opentFormLeavePosition = true;
  };

  handleSelectStaff = (staff) => {
    //Chung chi
    if (staff && staff.personCertificate && staff.personCertificate.length > 0) {
      staff.personCertificate = staff.personCertificate.map ((e) => {
        return {
          ... e,
          issueDate:e.issueDate? new Date (e.issueDate) : null /* Add */,
          graduationYear:e.graduationYear? new Date (e.graduationYear) : null,
        };
      });
    }

    //Qua trinh chuc vu
    if (staff && staff.positions && staff.positions.length > 0) {
      staff.positions = staff.positions.map ((e) => {
        return {
          ... e,
          fromDate:e.fromDate? new Date (e.fromDate) : null,
          toDate:e.toDate? new Date (e.toDate) : null /* add */,
          decisionDate:e.decisionDate? new Date (e.decisionDate) : null,
        };
      });
    }

    //Quan he than nhan
    if (staff && staff.familyRelationships && staff.familyRelationships.length > 0) {
      staff.familyRelationships = staff.familyRelationships.map ((e) => {
        return {... e, birthDate:e.birthDate? new Date (e.birthDate) : null};
      });
    }

    //Qua trinh dao tao
    if (staff && staff.educationHistory && staff.educationHistory.length > 0) {
      staff.educationHistory = staff.educationHistory.map ((e) => {
        return {
          ... e,
          endDate:e.endDate? new Date (e.endDate) : null,
          startDate:e.startDate? new Date (e.startDate) : null /* Add */,
          actualGraduationYear:e.actualGraduationYear? new Date (e.actualGraduationYear) : null,
          returnDate:e.returnDate? new Date (e.returnDate) : null,
          decisionDate:e.decisionDate? new Date (e.decisionDate) : null,
          extendDateByDecision:e.extendDateByDecision? new Date (e.extendDateByDecision) : null,
          extendDecisionDate:e.extendDecisionDate? new Date (e.extendDecisionDate) : null,
        };
      });
    }

    //hop dong
    if (staff && staff.agreements && staff.agreements.length > 0) {
      staff.agreements = staff.agreements.map ((e) => {
        return {
          ... e,
          endDate:e.endDate? new Date (e.endDate) : null,
          startDate:e.startDate? new Date (e.startDate) : null,
          signedDate:e.signedDate? new Date (e.signedDate) : null,
        };
      });
    }

    //qua trinh BHXH
    if (staff && staff.stafInsuranceHistory && staff.stafInsuranceHistory.length > 0) {
      staff.stafInsuranceHistory = staff.stafInsuranceHistory.map ((e) => {
        return {
          ... e,
          endDate:e.endDate? new Date (e.endDate) : null,
          startDate:e.startDate? new Date (e.startDate) : null,
        };
      });
    }

    //qua trinh luong
    if (staff && staff.salaryHistory && staff.salaryHistory.length > 0) {
      staff.salaryHistory = staff.salaryHistory.map ((e) => {
        return {
          ... e,
          decisionDate:e.decisionDate? new Date (e.decisionDate) : null,
        };
      });
    }

    /* Add new - Start*/

    //qua trinh cong tac nuoc ngoai
    if (staff && staff.overseasWorkHistory && staff.overseasWorkHistory.length > 0) {
      staff.overseasWorkHistory = staff.overseasWorkHistory.map ((e) => {
        return {
          ... e,
          startDate:e.startDate? new Date (e.startDate) : null,
          endDate:e.endDate? new Date (e.endDate) : null,
          decisionDate:e.decisionDate? new Date (e.decisionDate) : null,
        };
      });
    }

    //qua trinh cong tac
    if (staff && staff.workingHistory && staff.workingHistory.length > 0) {
      staff.workingHistory = staff.workingHistory.map ((e) => {
        return {
          ... e,
          startDate:e.startDate? new Date (e.startDate) : null,
          endDate:e.endDate? new Date (e.endDate) : null,
        };
      });
    }

    //qua trinh phu cap
    if (staff && staff.allowanceHistory && staff.allowanceHistory.length > 0) {
      staff.allowanceHistory = staff.allowanceHistory.map ((e) => {
        return {
          ... e,
          startDate:e.startDate? new Date (e.startDate) : null,
          endDate:e.endDate? new Date (e.endDate) : null,
        };
      });
    }

    //qua trinh boi duong
    if (staff && staff.trainingHistory && staff.trainingHistory.length > 0) {
      staff.trainingHistory = staff.trainingHistory.map ((e) => {
        return {
          ... e,
          startDate:e.startDate? new Date (e.startDate) : null,
          endDate:e.endDate? new Date (e.endDate) : null,
        };
      });
    }

    //qua trinh khen thuong
    if (staff && staff.rewardHistory && staff.rewardHistory.length > 0) {
      staff.rewardHistory = staff.rewardHistory.map ((e) => {
        return {
          ... e,
          rewardDate:e.rewardDate? new Date (e.rewardDate) : null,
        };
      });
    }

    //qua trinh thai san
    if (staff && staff.maternityHistory && staff.maternityHistory.length > 0) {
      staff.maternityHistory = staff.maternityHistory.map ((e) => {
        return {
          ... e,
          startDate:e.startDate? new Date (e.startDate) : null,
          endDate:e.endDate? new Date (e.endDate) : null,
        };
      });
    }

    //Qua trinh phu cap tham nien nghe giao
    if (staff && staff.allowanceSeniorityHistory && staff.allowanceSeniorityHistory.length > 0) {
      staff.allowanceSeniorityHistory = staff.allowanceSeniorityHistory.map ((e) => {
        return {
          ... e,
          startDate:e.startDate? new Date (e.startDate) : null,
        };
      });
    }

    /* Add new - End */

    this.selectedStaff = {
      ... staff,
      birthDate:staff?.birthDate? new Date (staff?.birthDate) : null,
      idNumberIssueDate:staff?.idNumberIssueDate? new Date (staff?.idNumberIssueDate) : null,
      contractDate:staff?.contractDate? new Date (staff?.contractDate) : null,
      recruitmentDate:staff?.recruitmentDate? new Date (staff?.recruitmentDate) : null,
      dateOfReceivingPosition:staff?.dateOfReceivingPosition? new Date (staff?.dateOfReceivingPosition) : null,
      startDate:staff?.startDate? new Date (staff?.startDate) : null,
      dateOfReceivingAllowance:staff?.dateOfReceivingAllowance
          ? new Date (staff?.dateOfReceivingAllowance)
          : null,
      salaryStartDate:staff?.salaryStartDate? new Date (staff?.salaryStartDate) : null,
    };
  };

  handleSelectListStaff = (staffs) => {
    this.selectedStaffList = staffs;
  };

  getSelectedIds = () => {
    const ids = [];
    this?.selectedStaffList?.forEach (function (item) {
      ids.push (item?.id);
    });

    return ids;
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedStaffList.length; i++) {
      try {
        await deleteStaff (this.selectedStaffList[i].id);
      } catch (error) {
        listAlert.push (this.selectedStaffList[i].name);
        console.log (error);
        console.log (listAlert.toString ());
        toast.warning (i18n.t ("toast.error"));
      }
    }
    this.handleClose ();
    toast.success (i18n.t ("toast.delete_success"));
  };

  createStaff = async (staff) => {
    try {
      staff.displayName = staff?.lastName + (staff?.lastName? " " : "") + staff?.firstName;
      // let reposeCheckIdNumber = await checkIdNumber(staff);
      // if(reposeCheckIdNumber.data){
      //   toast.warning("toast.idNumber_duplicate");
      //   return false;
      // }else{
      if (staff && staff?.file != null) {
        const formData = new FormData ();
        formData.append ("uploadfile", staff?.file);
        let response = await uploadImage (formData);
        console.log (response);
        staff.imagePath = response.data.name;
      }
      await createStaff (staff);
      toast.success (i18n.t ("toast.add_success"));
      this.handleClose ();
      // }
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  editStaff = async (staff) => {
    try {
      staff.displayName = staff.lastName + (staff.lastName? " " : "") + staff.firstName;
      // let reposeCheckIdNumber = await checkIdNumber(staff);
      // if(reposeCheckIdNumber.data){
      //   toast.warning("toast.idNumber_duplicate");
      //   return false;
      //}else{
      if (staff && staff.file != null) {
        const formData = new FormData ();
        formData.append ("uploadfile", staff.file);
        let response = await uploadImage (formData);
        console.log (response);
        staff.imagePath = response.data.name;
      }
      await editStaff (staff);
      toast.success (i18n.t ("toast.update_success"));
      this.handleClose ();
      //}
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  saveStaffWithoutAccount = async (staff) => {
    try {
      if (staff?.taxCode != null && staff?.taxCode?.length > 0) {
        const newValue = {
          taxCode:staff?.taxCode,
          id:staff?.id,
        };
        const responseTaxCode = await checkStaffTaxCode (newValue);
        if (!responseTaxCode?.data) {
          toast.error ("Mã số thuế đã được sử dụng, vui lòng sử dụng mã khác", {
            autoClose:5000,
            draggable:false,
            limit:5,
          });
          return;
        }
      }

      if (staff?.socialInsuranceNumber != null && staff?.socialInsuranceNumber?.length > 0) {
        const newValue = {
          socialInsuranceNumber:staff?.socialInsuranceNumber,
          id:staff?.id,
        };
        const responseSocialInsuranceNumber = await checkStaffSocialInsuranceNumber (newValue);
        if (!responseSocialInsuranceNumber?.data) {
          toast.error ("Mã số BHXH đã được sử dụng, vui lòng sử dụng mã khác", {
            autoClose:5000,
            draggable:false,
            limit:5,
          });
          return;
        }
      }

      if (staff?.healthInsuranceNumber != null && staff?.healthInsuranceNumber?.length > 0) {
        const newValue = {
          healthInsuranceNumber:staff?.healthInsuranceNumber,
          id:staff?.id,
        };
        const responseSocialInsuranceNumber = await checkStaffHealthInsuranceNumber (newValue);
        if (!responseSocialInsuranceNumber?.data) {
          toast.error ("Mã số BHYT đã được sử dụng, vui lòng sử dụng mã khác", {
            autoClose:5000,
            draggable:false,
            limit:5,
          });
          return;
        }
      }

      staff.displayName = staff?.lastName + (staff?.lastName? " " : "") + staff?.firstName;

      if (staff && staff?.file != null) {
        const formData = new FormData ();
        formData.append ("uploadfile", staff?.file);
        const response = await uploadImage (formData);

        staff.imagePath = response.data.name;
      }

      const response = await saveStaffWithoutAccount (staff);
      this.handleSelectStaff (response?.data);
      console.log (response);

      toast.success ("Đã cập nhật thông tin nhân viên");

      return response.data;
      // }
    } catch (error) {
      console.error ("error");
      if (error.response.status == 409) {
        const message = error?.response?.data?.error;
        console.log (error.response);
        toast.error (message || "Mã nhân viên đã được sử dụng, vui lòng sử dụng mã nhân viên khác", {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      } else {
        toast.error ("Có lỗi xảy ra khi lưu dữ liệu nhân viên");
      }
    }
  };

  deleteStaff = async (id) => {
    try {
      await deleteStaff (id);
      toast.success (i18n.t ("toast.delete_success"));
      await this.handleClose ();
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };
  uploadImage = async (file) => {
    try {
      await uploadImage (file);
      toast.success (i18n.t ("toast.delete_success"));
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };
  importExcel = () => {
    this.shouldOpenImportExcelDialog = true;
  };

  search_data_timesheet = async (itemSearch) => {
    let searchObject = {
      ... this.searchObj,
      ... itemSearch,
      pageIndex:this.page,
      pageSize:this.rowsPerPage,
    };

    try {
      let data = await pagingTimeSheetDetail (searchObject);
      this.timeSheetList = data.data?.content? data.data?.content : [];
      this.totalElementsTimeSheet = data.data.totalElements;
      this.totalPagesTimeSheet = data.data.totalPages;

      this.setLoadingInitial (false);
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
      this.setLoadingInitial (false);
    }
  };

  update_PageData = (item) => {
    let neWSearchObject = {
      ... this.searchObj,
    };
    if (item != null) {
      this.pageSheet = 1;
      this.searchObj = {
        ... item,
        pageIndex:1,
        pageSize:10,
      };

      this.search_data_timesheet ();
    } else {
      this.search_data_timesheet (neWSearchObject);
    }
  };
  setRowsPerPage = (event) => {
    this.rowsPerPageSheet = event.target.value;
    this.pageTimeSheet = 1;
    this.search_data_timesheet ();
  };
  handleChangePageTimeSheet = (event, pageIndex) => {
    this.setPage (pageIndex);
  };
  setPage = (pageTimeSheet) => {
    this.pageTimeSheet = pageTimeSheet;
    this.search_data_timesheet ();
  };

  getListTimeSheet = async (objSearch) => {
    const searchObject = {
      ... objSearch,
      pageIndex:this.pageIndexTimeSheet,
      pageSize:this.pageSizeTimeSheet,
    };
    try {
      const response = await pagingTimeSheetDetail (searchObject);
      this.timeSheetList = response.data?.content? response.data?.content : [];
      this.totalElementsTimeSheet = response.data.totalElements;
      this.totalPagesTimeSheet = response.data.totalPages;
    } catch (error) {
      this.timeSheetList = [];
    }
  };

  handleChangePageIndexTimeSheet = (searchObj, pageIndex) => {
    this.pageIndexTimeSheet = pageIndex;
    this.getListTimeSheet (searchObj);
  };

  handleChangePageSizeTimeSheet = (searchObj, pageSize) => {
    this.pageSizeTimeSheet = pageSize;
    this.getListTimeSheet (searchObj);
  };

  handleEditTimeSheetDetail = (id) => {
    this.getTimeSheetDetail (id).then (() => {
      this.shouldOpenEditorDialogTimeSheetDetail = true;
    });
  };

  getTimeSheetDetail = async (id) => {
    if (id != null) {
      try {
        let data = await getTimeSheetDetail (id);
        this.handleSelectTimeSheetDetail (data.data);
      } catch (error) {
        console.log (error);
        toast.warning (i18n.t ("toast.error"));
      }
    } else {
      this.handleSelectTimeSheetDetail (null);
    }
  };

  handleSelectTimeSheetDetail = (timeSheetDetail) => {
    this.selectedTimeSheetDetail = timeSheetDetail;
  };

  handleDeleteTimeSheetDetail = (id) => {
    this.getTimeSheetDetail (id).then (() => {
      this.shouldOpenConfirmationDialogTimeSheetDetail = true;
    });
  };

  handleConfirmDeleteTimeSheetDetail = async () => {
    try {
      await deleteTimeSheetDetail (this.selectedTimeSheetDetail.id);
      toast.success (i18n.t ("toast.delete_success"));
      this.handleClose ();
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  getTimeSheetMonthByStaff = ({staffId, value = new Date ()}) => {
    searchTimeSheetDate ({
      staffId:staffId,
      monthReport:getMonth (value) + 1,
      yearReport:getFullYear (value),
    })
        .then (({data}) => {
          this.listTimeSheetMonthByStaff = data.length > 0? data : [];
          this.isReload = false;
        })
        .catch (() => {
          toast.error ("Đã có lỗi xảy ra!", "Thất bại!");
        });
  };

  getTimeSheetWeeksByStaff = (fromDate, toDate) => {
    searchTimeSheetDate ({fromDate:new Date (fromDate), toDate:new Date (toDate)})
        .then ((response) => {
          this.listTimeSheetWeeksByStaff = response.data;
        })
        .catch (() => {
          toast.error ("Đã có lỗi xảy ra!", "Thất bại!");
        });
  };

  getTimeSheetDayByStaff = (date = new Date ()) => {
    searchTimeSheetDate ({workingDate:date})
        .then ((response) => {
          this.listTimeSheetDayByStaff = response.data;
        })
        .catch (() => {
          toast.error ("Đã có lỗi xảy ra!", "Thất bại!");
        });
  };

  //MAP TAB TITLE IN STAFFPROFILE
  getDetailTabTitle = (tabIndex) => {
    if (tabIndex === 0) return i18n.t ("humanResourcesInformation.personalInformation");
    // if (tabIndex === 1) return i18n.t("humanResourcesInformation.accountInformation");
    if (tabIndex === 1) return i18n.t ("Công cụ/ dụng cụ");
    if (tabIndex === 2) return i18n.t ("humanResourcesInformation.educationHistory");
    if (tabIndex === 3) return i18n.t ("humanResourcesInformation.agreements");
    if (tabIndex === 4) return i18n.t ("humanResourcesInformation.insuranceHistory");
    if (tabIndex === 5) return i18n.t ("humanResourcesInformation.workingHistory");
    if (tabIndex === 6) return i18n.t ("humanResourcesInformation.salaryHistory");
    if (tabIndex === 7) return i18n.t ("humanResourcesInformation.positionHistory");
    // if (tabIndex === 9) return i18n.t("humanResourcesInformation.overseasWorkHistory");
    if (tabIndex === 8) return i18n.t ("humanResourcesInformation.allowanceHistory");
    // if (tabIndex === 11) return i18n.t("humanResourcesInformation.trainingHistory");
    if (tabIndex === 9) return i18n.t ("humanResourcesInformation.rewardHistory");
    // if (tabIndex === 13) return i18n.t("humanResourcesInformation.allowanceSeniorityHistory");
    if (tabIndex === 10) return i18n.t ("humanResourcesInformation.maternityHistory");

    return "";
  };

  getScreenTitle = (tabIndex, preFix = "", subFix = "") => {
    const tabTitle = this.getDetailTabTitle (tabIndex);

    if (!tabTitle) return "";

    return preFix + " " + tabTitle + " " + subFix;
  };
  getAllStaff = async () => {
    try {
      const res = await getAllStaff ();
      this.staffList = res.data;
    } catch (e) {
      toast.warning (i18n.t ("toast.error"));
    }
  };

  handleDownloadExcelListStaff = async () => {
    if (this.pageStaff?.totalElements > 0) {
      try {
        const res = await exportExcelListStaff ({... this.searchStaff});
        toast.success (i18n.t ("general.successExport"));
        let blob = new Blob ([res.data], {
          type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });

        saveAs (blob, "DanhsachNhanVien.xlsx");
      } finally {
      }
    } else {
      toast.warning (i18n.t ("general.noData"));
    }
  };

  uploadFileExcelListStaff = async (event) => {
    const fileInput = event.target;
    const file = fileInput.files[0];
    fileInput.value = null;

    if (!file) return;

    try {
      const res = await importExcelListStaff (file);

      // 🔽 Create and save the Excel result file
      const blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      let filename = "KetQuaNhapDuLieuNhanSu.xlsx";
      const contentDisposition = res.headers["content-disposition"];
      if (contentDisposition) {
        const match = contentDisposition.match (/filename="?([^"]+)"?/);
        if (match && match[1]) {
          filename = decodeURIComponent (match[1]);
        }
      }

      saveAs (blob, filename);

      toast.success ("Nhập excel thành công");

      this.searchStaff = {
        ... this.searchStaff,
        pageIndex:1,
      };
      this.onPagingStaff ();
    } catch (err) {
      console.error (err);
      toast.error ("Nhập excel thất bại");
    } finally {
      this.handleClose ();
    }
  };

  uploadFileExcelListNewStaff = async (event) => {
    const fileInput = event.target;
    const file = fileInput.files[0];
    fileInput.value = null;

    if (!file) return;

    try {
      const res = await importExcelListNewStaff (file);

      // 🔽 Create and save the Excel result file
      const blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      let filename = "KetQuaNhapDuLieuNhanSuMoi.xlsx";
      const contentDisposition = res.headers["content-disposition"];
      if (contentDisposition) {
        const match = contentDisposition.match (/filename="?([^"]+)"?/);
        if (match && match[1]) {
          filename = decodeURIComponent (match[1]);
        }
      }

      saveAs (blob, filename);

      toast.success ("Nhập excel thành công");

      this.searchStaff = {
        ... this.searchStaff,
        pageIndex:1,
      };
      this.onPagingStaff ();
    } catch (err) {
      console.error (err);
      toast.error ("Nhập excel thất bại");
    } finally {
      this.handleClose ();
    }
  };

  openConfirmDownload = (status) => {
    this.openPopupConfirmDownloadExcelListStaff = status;
  };

  handleDownloadTemplateImportStaff = async () => {
    try {
      const res = await exportExcelTemplateImportStaff ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu nhập dữ liệu nhân viên.xlsx");
      toast.success (i18n.t ("Tải mẫu nhập thành công"));
    } catch (error) {
      console.error ("Tải mẫu nhập không thành công", error);
    }
  };

  resetSearchTotalAnnualLeavePoupup = () => {
    this.remainingLeave = null;
  };

  fetchRemainingLeave = async (staffId, year) => {
    try {
      let payload = {
        staffId:staffId,
        year:year,
      };
      const {data} = await calculateRemaininAnnualLeave (payload);
      this.remainingLeave = data;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };
}
