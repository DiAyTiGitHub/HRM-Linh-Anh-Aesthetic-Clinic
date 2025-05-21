import localStorageService from "app/services/localStorageService";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchStaffWorkSchedule } from "app/common/Model/SearchObject/SearchStaffWorkSchedule";
import { exportTimekeepingReportByFitler, getTimekeepingReportByFitler } from "../StaffWorkScheduleCalendar/StaffWorkScheduleCalendarService";
import FileSaver from "file-saver";
import { getSchedulesInDayOfStaff } from "../StaffWorkScheduleV2/StaffWorkScheduleService";

export default class TimekeepingReportStore {
  intactSearchObject = {
    ...new SearchStaffWorkSchedule(),
  };

  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
  totalPages = 0;
  listTimekeepingReport = [];
  totalElements = 0;
  pageIndex = 1;
  pageSize = 10;
  openScheduleInDayPopup = false;
  schedulesInDayList = [];

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listTimekeepingReport = [];
    this.openScheduleInDayPopup = false;
    this.schedulesInDayList = [];

  }

  setPageSize = (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    this.getTimekeepingReportByFitler();
  };

  handleChangePage = (event, newPage) => {
    this.setPage(newPage);
  };

  setPage = (page) => {
    this.searchObject.pageIndex = page;
    this.getTimekeepingReportByFitler();
  };


  handleSetSearchObject = (searchObject) => {
    const mappings = {
      department: "departmentId",
      staff: "staffId",
      organization: "organizationId",
      position: "positionId",
      positionTitle: "positionTitleId"
    };

    Object.keys(mappings).forEach(key => {
      searchObject[mappings[key]] = searchObject[key]?.id ?? null;
    });

    this.searchObject = { ...searchObject };
  };

  getNeedRenderDates = () => {
    const fromDate = this?.searchObject?.fromDate;
    const toDate = this?.searchObject?.toDate;

    const start = new Date(fromDate);
    const end = new Date(toDate);
    const dateArray = [];

    if (isNaN(start) || isNaN(end) || start > end) {
      console.error("Invalid date range");
      return [];
    }

    while (start <= end) {
      dateArray.push(new Date(start)); // Store as Date object
      start.setDate(start.getDate() + 1);
    }

    return dateArray;
  };

  getTimekeepingReportByFitler = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ...this.searchObject,
        // organizationId: loggedInStaff?.user?.org?.id
      };
      this.handleSetSearchObject(payload);
      const { data } = await getTimekeepingReportByFitler(payload);
      this.listTimekeepingReport = data.content;
      this.totalElements = data.totalElements;
      this.totalPages = data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };


  handleExportExcelByFilter = async () => {
    try {
      const searchObject = {
        ...this.searchObject
      };
      this.handleSetSearchObject(searchObject);
      const res = await exportTimekeepingReportByFitler(searchObject);

      if (res && res.data) {
        const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

        FileSaver.saveAs(blob, "BANG_CHAM_CONG.xlsx");
      } else {
        toast.error("Không nhận được dữ liệu từ server.");
      }
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  handleClose = () => {
    this.openScheduleInDayPopup = false;
    this.schedulesInDayList = [];

  }


  handleOpenViewSchedulesInDays = async (staffId, workingDate) => {
    try {
      const payload = {
        staffId,
        workingDate
      };

      const { data } = await getSchedulesInDayOfStaff(payload);

      this.schedulesInDayList = data;

      this.openScheduleInDayPopup = true;
    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi xem ca làm việc trong ngày");
    }
  }




}

