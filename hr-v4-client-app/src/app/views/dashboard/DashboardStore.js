import { makeAutoObservable } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import localStorageService from "app/services/localStorageService";
import { toast } from "react-toastify";
import i18n from "i18n";
import {
  getDashboard,
  totalTimeReport,
  totalProjectTimeReport,
  totalProjectTimeReportUser,
  highchartReport,
} from "./DashboardService";
import { findStaffsHaveBirthDayByMonth } from "../HumanResourcesInformation/StaffService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class DashboardStore {
  isAdmin = false;
  isUser = false; //tách ra để xử lý bất đồng bộ
  staffNumber = null;
  projectNumber = null;
  monthTaskNumber = null;

  totalTimeReportList = [];
  totalProjectTimeReport = [];
  horizontalList = [];
  totalProjectTimeReportUserList = [];
  highchartReportList = [];
  searObj = {
    timeReport: 3,
    monthReport: new Date().getMonth() + 1,
    yearReport: new Date().getFullYear(),
    weekReport: null,
  };
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  loadingInitial = false;

  constructor() {
    makeAutoObservable(this);
  }

  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  getDashboard = () => {
    let searchObj = {};
    getDashboard(searchObj).then(({ data }) => {
      this.staffNumber = data.staffNumber;
      this.projectNumber = data.projectNumber;
      this.monthTaskNumber = data.monthTaskNumber;
    });
  };

  checkAdmin = () => {
    let roles =
      localStorageService
        .getLoginUser()
        ?.user?.roles?.map((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN"];

    if (roles.some((role) => auth.indexOf(role) !== -1)) {
      this.isAdmin = true;
      this.isUser = false;
    } else {
      this.isAdmin = false;
      this.isUser = true;
    }
  };

  updatePageData = () => {
    this.checkAdmin();
    this.getDashboard();

    if (this.isAdmin && !this.isUser) {
      this.totalProjectTime(); //k cần truyền đầu vào
    }
  };

  handleUpdateSearchObject = (item) => {
    this.searObj = {
      ...this.searObj,
      ...item,
    };
  };

  handleFilters = (item) => {
    if (item != null) {
      this.handleUpdateSearchObject(item);
    }
    this.getHighchartReport();

    if (this.isAdmin) {
      this.totalTimeReportAdmin();
    } else if (this.isUser) {
      this.totalProjectTimeUser();
    }
  };

  totalTimeReportAdmin = async () => {
    this.loadingInitial = true;

    var searchObject = {
      ...this.searObj,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
    };

    try {
      const data = await totalTimeReport(searchObject);
      this.totalElements = data?.data?.totalElements;
      this.totalPages = data?.data?.totalPages;
      this.totalTimeReportList = data?.data?.content;

      this.setLoadingInitial(false);
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
      this.setLoadingInitial(false);
    }
  };

  totalProjectTime = async () => {
    this.loadingInitial = true;
    try {
      let totalProjectTime = await totalProjectTimeReport();
      this.totalProjectTimeReport = totalProjectTime?.data?.content.sort(
        (a, b) => b.totalHours - a.totalHours
      );
      this.setLoadingInitial(false);
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
      this.setLoadingInitial(false);
    }
  };

  totalProjectTimeUser = async () => {
    this.loadingInitial = true;
    var searchObject = {};
    searchObject.timeReport = this.searObj.timeReport;
    searchObject.monthReport = this.searObj.monthReport;
    searchObject.yearReport = this.searObj.yearReport;
    searchObject.weekReport = this.searObj.weekReport;

    try {
      let data = await totalProjectTimeReportUser(searchObject);
      this.totalProjectTimeReportUserList = data.data.content;
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
      this.setLoadingInitial(false);
    }
  };

  getHighchartReport = async () => {
    this.loadingInitial = true;

    var searchObject = {
      ...this.searObj,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
    };

    if (this.searObj.timeReport === 3) {
      searchObject.timeReport = 2;
      searchObject.yearReport = this.searObj.yearReport;
      searchObject.monthReport = this.searObj.monthReport;
    } else {
      searchObject.timeReport = this.searObj.timeReport;
      searchObject.monthReport = this.searObj.monthReport;
      searchObject.yearReport = this.searObj.yearReport;
      searchObject.weekReport = this.searObj.weekReport;
    }

    try {
      let data = await highchartReport(searchObject);
      this.highchartReportList = data.data.projectTimeWork;
      this.horizontalList = data.data.horizontal;
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
      this.setLoadingInitial(false);
      this.highchartReportList = null;
      this.horizontalList = null;
    }
  };

  setPage = (page) => {
    this.page = page;
    this.totalTimeReportAdmin();
  };

  setRowsPerPage = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    // this.totalTimeReportAdmin();
  };

  handleChangePage = (event, newPage) => {
    this.setPage(newPage);
  };

  roundHour(hour) {
    return Math.round(hour);
  }



  //handle for get staff has birthdate in month
  listStaffHasBirthDayInMonth = [];

  findStaffsHaveBirthDayByMonth = async month => {
    try {
      const { data } = await findStaffsHaveBirthDayByMonth(month);
      // console.log("data", data);
      this.listStaffHasBirthDayInMonth = [...data] || [];
    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi lấy danh sách nhân viên có sinh nhật trong tháng");
    }
  }
}
