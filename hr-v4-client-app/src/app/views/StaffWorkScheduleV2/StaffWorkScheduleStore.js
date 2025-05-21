import { makeAutoObservable } from "mobx";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { StaffWorkSchedule } from "app/common/Model/Timekeeping/StaffWorkSchedule";
import {
  createStaffWorkScheduleList,
  deleteMultiple,
  deleteStaffWorkSchedule,
  getStaffWorkSchedule,
  pagingStaffWorkSchedule,
  saveOneStaffWorkSchedule,
  exportActualTimesheet,
  updateScheduleOTHours,
  recalculateStaffWorkTime,
  getInitialShiftAssignmentForm,
  importStaffWorkSchedule,
  downloadTemplate,
  saveScheduleStatistic,
  getStaffWorkScheduleSummary,
  lockSchedulesMultiple,
  reStatisticSchedule
} from "./StaffWorkScheduleService";
import { SearchStaffWorkSchedule } from "app/common/Model/SearchObject/SearchStaffWorkSchedule";
import LocalConstants, { HttpStatus } from "app/LocalConstants";
import { saveAs } from "file-saver";
import { getCurrentStaff } from "../profile/ProfileService";
import { getStaff } from "../HumanResourcesInformation/StaffService";
import { CreateStaffWorkScheduleList } from "app/common/Model/Timekeeping/CreateStaffWorkScheduleList";
import { isExistLeaveRequestInPeriod } from "../LeaveRequest/LeaveRequestService";
import { findOneByCode } from "../LeaveType/LeaveTypeService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class StaffWorkScheduleStore {
  intactSearchObject = {
    ...new SearchStaffWorkSchedule(),
  };
  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  totalElements = 0;
  totalPages = 0;
  listStaffWorkSchedules = [];
  openCreateEditPopup = false;
  openFormSWSPopup = false;
  selectedStaffWorkSchedule = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openAssignForm = false;
  initialShiftAssignmentForm = { staffs: [] };
  openViewPopup = false;
  isOpenFilter = false;
  listOnDelete = [];
  listExistLeaveRequest = [];
  openViewStatistic = false;
  openEditStatistic = false;
  totalStaffWorkSchedule = null;
  openFormCreateMultipleStaffWorkSchedule = false;
  openConfirmLockSchedulesPopup = false;
  openReStatisticSchedulePopup = false;

  constructor() {
    makeAutoObservable(this);
  }

  handleGetTotalStaffWorkSchedule = async () => {
    try {
      this.handleSetSearchObject(this.searchObject);
      const payload = {
        ... this.searchObject,
        workingStatus: this.mapTabToWorkingStatus(this?.searchObject?.workingStatus),
        // organizationId: loggedInStaff?.user?.org?.id
      };
      const { data } = await getStaffWorkScheduleSummary(payload);
      this.totalStaffWorkSchedule = data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.hasShiftAssignmentPermission = false; // Biến này có thể được sử dụng để kiểm tra quyền phân ca
    this.listStaffWorkSchedules = [];
    this.openAssignForm = false;
    this.openCreateEditPopup = false;
    this.openFormSWSPopup = false;
    this.selectedStaffWorkSchedule = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    this.openViewPopup = false;
    this.listExistLeaveRequest = [];
    this.openViewStatistic = false;
    this.openEditStatistic = false;
    this.totalStaffWorkSchedule = null;
    this.openFormCreateMultipleStaffWorkSchedule = false;
    this.openConfirmLockSchedulesPopup = false;
    this.openReStatisticSchedulePopup = false;
  };

  //lọc theo trạng thái làm việc = thay đổi tab
  handleChangeWorkingStatus = (status) => {
    const so = { ... this.searchObject, workingStatus: status };
    this.searchObject = so;
  };

  setSearchObject = async (searchObj) => {
    this.searchObject = searchObj;
  };

  setIsOpenFilter = (value) => {
    this.isOpenFilter = value;
  };

  handleSetSearchObject = (searchObject) => {
    if (searchObject.shiftWork == null) {
      searchObject.shiftWorkId = null;
    } else {
      searchObject.shiftWorkId = searchObject.shiftWork.id;
    }

    if (searchObject.staff == null) {
      searchObject.staffId = null;
    } else {
      searchObject.staffId = searchObject.staff.id;
    }

    if (searchObject.department == null) {
      searchObject.departmentId = null;
    } else {
      searchObject.departmentId = searchObject.department.id;
    }

    if (searchObject.organization == null) {
      searchObject.organizationId = null;
    } else {
      searchObject.organizationId = searchObject.organization.id;
    }

    if (searchObject.positionTitle == null) {
      searchObject.positionTitleId = null;
    } else {
      searchObject.positionTitleId = searchObject.positionTitle.id;
    }

    this.searchObject = { ...searchObject };
  };

  mapTabToWorkingStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab === 0) return null;
    // tab 1 => Đi làm đủ
    if (tab === 1) return LocalConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.value;
    // tab 2 => Đi thiếu giờ
    if (tab === 2) return LocalConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.value;
    // tab 3 => Không đi làm
    if (tab === 3) return LocalConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.value;

    // // tab 4 => Nghỉ không phép
    // if (tab == 4) return LocalConstants.StaffWorkScheduleWorkingStatus.LEAVE_WITHOUT_PERMISSION.value;
    // // if (tab == 5) return LocalConstants.StaffWorkScheduleWorkingStatus.LATE_FOR_WORK.value;

    return null;
  };

  pagingStaffWorkSchedule = async () => {
    try {
      // const loggedInStaff = localStorageService.getLoginUser();
      this.handleSetSearchObject(this.searchObject);
      const payload = {
        ... this.searchObject,
        workingStatus: this.mapTabToWorkingStatus(this?.searchObject?.workingStatus),
        // organizationId: loggedInStaff?.user?.org?.id
      };

      const data = await pagingStaffWorkSchedule(payload);

      this.listStaffWorkSchedules = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingStaffWorkSchedule();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingStaffWorkSchedule();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteStaffWorkSchedules) => {
    this.listOnDelete = deleteStaffWorkSchedules;
  };

  getStaffWorkSchedule = async (staffWorkScheduleId) => {
    try {
      const { data } = await getStaffWorkSchedule(staffWorkScheduleId);
      this.selectedStaffWorkSchedule = data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleRecalculateStaffWorkTime = async (staffWorkScheduleId) => {
    try {
      const { data } = await recalculateStaffWorkTime(staffWorkScheduleId);
      this.openCreateEditPopup = true;
      this.selectedStaffWorkSchedule = data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleRecalculateStaffWorkTimeNoOpenForm = async (staffWorkScheduleId) => {
    try {
      const { data } = await recalculateStaffWorkTime(staffWorkScheduleId);
      this.selectedStaffWorkSchedule = data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openAssignForm = false;
    this.openFormSWSPopup = false;
    this.listOnDelete = [];
    this.openViewPopup = false;
    this.openViewStatistic = false;
    this.openEditStatistic = false;
    this.openFormCreateMultipleStaffWorkSchedule = false;
    this.openConfirmLockSchedulesPopup = false;
    this.openReStatisticSchedulePopup = false;
  };

  handleOpenFormSWS = async () => {
    try {
      const initialStaff = this.searchObject?.staff;
      console.log("initialStaff", initialStaff);
      // const { data: leaveWithoutPaidType } = await findOneByCode(LocalConstants.LeaveTypeCode.UNPAID_LEAVE.code);
      this.selectedStaffWorkSchedule = {
        ...new StaffWorkSchedule(),
        staff: initialStaff
        // leaveType: leaveWithoutPaidType,
      };

      // console.log("this.selectedStaffWorkSchedule", this.selectedStaffWorkSchedule)

      this.openFormSWSPopup = true;
    } catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi tạo m");
    }
  };

  handleOpenFormCreateMultipleStaffWorkSchedule = (dto) => {
    this.selectedStaffWorkSchedule = {
      ...new StaffWorkSchedule(),
      ...dto,
    };

    // console.log(dto);

    this.openFormCreateMultipleStaffWorkSchedule = true;
  };

  handleDelete = (staffWorkSchedule) => {
    this.selectedStaffWorkSchedule = { ...staffWorkSchedule };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  uploadFileExcel = async (event) => {
    const input = event.target;
    const file = input?.files?.[0];
    if (!file) return;

    try {
      await importStaffWorkSchedule(file);
      toast.success("Nhập Excel thành công");
      await this.pagingStaffWorkSchedule();
    } catch (e) {
      console.error(e);
      const errorMessage = e?.response?.data || "Nhập Excel thất bại";
      toast.error(errorMessage);
    } finally {
      this.handleClose();
      if (input) input.value = null; // An toàn hơn
    }
  };

  handleDownloadTemplate = async () => {
    try {
      const res = await downloadTemplate();
      let blob = new Blob([res.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs(blob, "Mẫu nhập phân ca làm việc.xlsx");
      toast.success(i18n.t("Tải mẫu nhập thành công"));
    } catch (error) {
      console.error("Error downloading timesheet detail template:", error);
    }
  };

  handleOpenView = async (staffWorkScheduleId) => {
    try {
      if (staffWorkScheduleId) {
        const { data } = await getStaffWorkSchedule(staffWorkScheduleId);
        this.selectedStaffWorkSchedule = {
          ...JSON.parse(JSON.stringify(data)),
        };
      } else {
        this.selectedStaffWorkSchedule = {
          ... new StaffWorkSchedule(),
        };
      }

      this.openViewPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };
  handleCloseEditStatistic = () => {
    this.openEditStatistic = false;
  };
  handleOpenEditStatistic = async (staffWorkScheduleId) => {
    try {
      if (staffWorkScheduleId) {
        const { data } = await getStaffWorkSchedule(staffWorkScheduleId);
        this.selectedStaffWorkSchedule = {
          ...JSON.parse(JSON.stringify(data)),
        };
      } else {
        this.selectedStaffWorkSchedule = {
          ... new StaffWorkSchedule(),
        };
      }
      this.openEditStatistic = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleOpenViewStatistic = async (staffWorkScheduleId) => {
    try {
      if (staffWorkScheduleId) {
        const { data } = await getStaffWorkSchedule(staffWorkScheduleId);
        this.selectedStaffWorkSchedule = {
          ...JSON.parse(JSON.stringify(data)),
        };
      } else {
        toast.info("Ca làm việc đang chọn chưa được phân", {
          autoClose: 5000,
        });
        // this.selectedStaffWorkSchedule = {
        //   ... new StaffWorkSchedule(),
        // };
      }

      this.openViewStatistic = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleCloseViewStatisticPopup = () => {
    this.openViewStatistic = false;
  };

  handleOpenCreateEdit = async (staffWorkScheduleId) => {
    try {
      if (staffWorkScheduleId) {
        const { data } = await getStaffWorkSchedule(staffWorkScheduleId);
        this.selectedStaffWorkSchedule = {
          ...JSON.parse(JSON.stringify(data)),
        };
      } else {
        this.selectedStaffWorkSchedule = {
          ... new StaffWorkSchedule(),
        };
      }

      this.openCreateEditPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleConfirmDelete = async () => {
    try {
      //console.log("this?.selectedStaffWorkSchedule?.id", this?.selectedStaffWorkSchedule?.id);
      const { data } = await deleteStaffWorkSchedule(this?.selectedStaffWorkSchedule?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingStaffWorkSchedule();

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        deleteData.push(this?.listOnDelete[i]?.id);
      }

      // console.log("deleteData", deleteData)
      await deleteMultiple(deleteData);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingStaffWorkSchedule();
      this.listOnDelete = [];

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleOpenAssignForm = async () => {
    try {
      const { data } = await getInitialShiftAssignmentForm();
      if (data) {
        this.initialShiftAssignmentForm = {
          ... new CreateStaffWorkScheduleList(),
          ...data,
        };
      } else {
        this.initialShiftAssignmentForm = {
          ... new CreateStaffWorkScheduleList(),
        };
      }

      // console.log(this.initialShiftAssignmentForm);
      this.openAssignForm = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  createStaffWorkScheduleList = async (staffWorkSchedule) => {
    try {
      await createStaffWorkScheduleList(staffWorkSchedule);
      toast.success("Đã phân ca cho nhân viên");
    } catch (error) {
      const errorMsg = error?.response?.data?.error || "Đã xảy ra lỗi không xác định.";
      console.error(error);
      toast.warning(errorMsg);
    }
  };

  saveOneStaffWorkSchedule = async (staffWorkSchedule) => {
    try {
      await saveOneStaffWorkSchedule(staffWorkSchedule);
      toast.success("Thông tin ca làm việc đã được lưu");
    } catch (error) {
      console.error(error);
      if (error.response.status == 409) {
        toast.error("Ca làm việc đã được tạo từ trước", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      } else {
        toast.error(i18n.t("toast.error"));
      }

      throw new Error(i18n.t("toast.error"));
    }
  };
  saveScheduleStatistic = async (staffWorkSchedule) => {
    try {
      const { data } = await saveScheduleStatistic(staffWorkSchedule);
      this.selectedStaffWorkSchedule = {
        ...JSON.parse(JSON.stringify(data)),
      };
      toast.success("Đăng ký lịch làm việc thành công");
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  exportActualTimesheet = async (searchObject) => {
    const payload = { ...searchObject, isExportExcel: true };
    console.log(payload);
    if (payload?.fromDate != null && payload?.toDate != null) {
      try {
        const res = await exportActualTimesheet(payload);

        let blob = new Blob([res.data], {
          type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });
        saveAs(blob, "BANG_CHAM_CONG_THUC_TE.xlsx");
        toast.success(i18n.t("general.successExport"));
      } catch (error) {
        if (error.response && error.response.status === 400) {
          const message = error.response.data?.message || i18n.t("general.noData");
          toast.warning(message);
        } else {
          toast.error(i18n.t("general.errorExport"));
        }
      }
    } else {
      toast.warning("Phải nhập đủ ngày bắt đầu và ngày kết thúc!");
    }
  };

  getStaffWorkScheduleWorkingStatusName = (data) => {
    if (!data) {
      return "Dữ liệu không hợp lệ"; // Giá trị mặc định nếu data bị undefined
    }

    const { workingDate, workingStatus } = data;

    // Nếu workingStatus tồn tại và hợp lệ, trả về tên trạng thái ngay lập tức
    if (workingStatus) {
      const status = LocalConstants.StaffWorkScheduleWorkingStatusFull.getListData().find(
        (i) => i.value === workingStatus
      );
      if (status) {
        return status.name; // Trả về tên trạng thái, ví dụ: "Nghỉ có phép"
      }
      return "Không xác định"; // Nếu workingStatus không khớp với danh sách
    }

    // Nếu không có workingStatus, xử lý dựa trên workingDate
    if (!workingDate) {
      return "Chưa có ngày làm việc";
    }

    const workDate = new Date(workingDate);
    const today = new Date();

    // Kiểm tra ngày làm việc hợp lệ
    if (isNaN(workDate.getTime())) {
      return "Ngày làm việc không hợp lệ";
    }

    if (workDate > today) {
      return "Chưa đến lịch làm việc";
    }

    return "Không đi làm"; // Mặc định khi không có workingStatus và ngày đã qua
  };

  getStaffWorkScheduleWorkingTypeName = (type) => {
    return LocalConstants.StaffWorkScheduleWorkingType.getListData().find((i) => i.value == type)?.name;
  };

  getTotalConfirmedOTHours = (workSchedule) => {
    let totalOTHours = 0.0;
    if (workSchedule?.confirmedOTHoursBeforeShift) {
      totalOTHours += workSchedule?.confirmedOTHoursBeforeShift;
    }
    if (workSchedule?.confirmedOTHoursAfterShift) {
      totalOTHours += workSchedule?.confirmedOTHoursAfterShift;
    }

    return totalOTHours;
  };

  getStaff = async (id) => {
    try {
      const { data } = await getStaff(id);

      const newSearchObject = {
        ...this.searchObject,
        staff: data,
      };

      this.handleSetSearchObject(newSearchObject);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.get_fail"));
      this.handleSelectStaff(null);
    }
  };

  getCurrentStaff = async () => {
    try {
      const { data } = await getCurrentStaff();

      const newSearchObject = {
        ...this.searchObject,
        staff: data,
      };

      this.handleSetSearchObject(newSearchObject);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.get_fail"));
      this.handleSelectStaff(null);
    }
  };

  // handleSetDefaultSearchObject = async () => {
  //   try {
  //     const { data } = await getCurrentStaff();

  //     this.initialShiftAssignmentForm = {
  //       ...this.initialShiftAssignmentForm,
  //       organization: data?.organization,
  //       department: data?.department,
  //       positionTitle: data?.positionTitle,
  //     }
  //   } catch (error) {
  //     console.log(error);
  //     toast.warning(i18n.t("toast.get_fail"));
  //     this.handleSelectStaff(null);
  //   }
  // }

  isExistLeaveRequestInPeriod = async (dto) => {
    const response = await isExistLeaveRequestInPeriod(dto);
    if (response.status === HttpStatus.OK) {
      if (response.data.status === HttpStatus.OK && response.data.data) {
        this.listExistLeaveRequest = response.data.data;
      }
    }
  };


  handleLockSchedules = () => {
    this.openConfirmLockSchedulesPopup = true;
  };

  handleLockSchedulesMultiple = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        deleteData.push(this?.listOnDelete[i]?.id);
      }

      await lockSchedulesMultiple(deleteData);
      toast.success("Đã chốt ca thành công");

      await this.pagingStaffWorkSchedule();
      this.listOnDelete = [];

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };



  handleOpenReStatisticSchedules = () => {
    this.openReStatisticSchedulePopup = true;
  }


  handleReStatisticSchedules = async (values) => {
    toast.info("Vui lòng đợi trong giây lát, kết quả làm việc đang được thống kê lại", {
      autoClose: 55555,
      draggable: false,
      limit: 5,
    });

    try {
      const payload = {
        ...values,
      };
      const { data } = await reStatisticSchedule(payload);

      toast.dismiss();
      toast.success("Đã thống kê lại kết quả làm việc");

      this.handleClose();

      return data;
    } catch (err) {
      toast.error("Có lỗi xảy ra khi thống kê lại kết quả");
      console.error(err); // Hoặc xử lý lỗi theo cách bạn muốn
    }
  }

}
