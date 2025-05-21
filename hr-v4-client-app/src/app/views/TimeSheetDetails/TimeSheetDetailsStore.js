import { makeAutoObservable } from "mobx";
import {
  pagingTimeSheetDetail,
  updateStatus,
  totalTimeReport,
  createTimeSheetDetail,
  editTimeSheetDetail,
  deleteTimeSheetDetail,
  getTimeSheetDetail,
  getAllTimeSheetDetail, 
  getTimeSheetByTime,
  searchTimeSheetDate,
  saveTimeSheetDetail,
  autoGenerateTimeSheetDetails
} from "./TimeSheetDetailsService";
import { pagingProject } from "../Project/ProjectService";
import { checkCode, createProject } from "../Project/ProjectService";
import { pagingWorkingStatus } from "../WorkingStatus/WorkingStatusService";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { equalComparisonDate, getFullYear, getMonth } from "app/LocalFunction";
import { getStaff } from "../HumanResourcesInformation/StaffService";
import { searchToList } from "../TimeSheet/TimeSheetService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

const dataDefaultFormTimeSheet = {
  id: null,
  timeSheet: null,
  startTime: null,
  endTime: null,
  duration: "",
  workingItemTitle: "",
  employee: null,
  projectActivity: null,
  project: null,
  description: "",
  approveStatus: 1,
  priority: 4,
  workingStatus: null,
}

export default class TimeSheetDetailsStore {

  timeSheetDetailList = [];
  selectedTimeSheetDetailList = [];
  selectedTimeSheetByStaff = [];
  selectedTimeSheetDetail = null;
  selectedTimeSheetByTime = null;
  currentStaff = null;
  listProject = [];
  selectedProject = null;
  selectedActivityList = [];

  listWorkingStatus = [];
  selectedStaff = null;

  totalElements = 0;
  page = 1;
  ts = 0;
  totalPages = 0;
  rowsPerPage = 10;
  keyword = "";
  loadingInitial = false;
  shouldOpenEditorDialogTimeSheetDetail = false;
  shouldOpenEditorProjectDialog = false;
  shouldOpenTotalTimeReportDialog = false;
  shouldOpenConfirmationDialogTimeSheetDetail = false;
  shouldOpenConfirmationDeleteListDialog = false;
  projectId = null;
  searchObj = {
    pageIndex: 1,
    pageSize: 5,
    staffId: "",
    fromDate: null,
    toDate: null,
    workingStatusId: "",
    priority: null,
    projectActivityId: "",
  };

  listTimeSheetMonth = [];
  listTimeSheetWeeks = [];
  listTimeSheetDay = [];
  dataEditTimeSheet = dataDefaultFormTimeSheet;
  openPopupConfirm = false;
  openFormTimeSheet = false;
  view = 'month';

  currentMonth = new Date();
  valueCurrentMonth = [];
  dateValueMonth = new Date();

  constructor() {
    makeAutoObservable(this);
  }


  setView = view => this.view = view;
  setCurrentMonth = date => this.currentMonth = date;
  setValueCurrentMonth = value => this.valueCurrentMonth = value;
  setDateValueMonth = date => this.dateValueMonth = date;

  getTimeSheetMonth = (staffId) => {
    searchTimeSheetDate({
      staffId: staffId,
      monthReport: getMonth(this.currentMonth) + 1,
      yearReport: getFullYear(this.currentMonth),
    }).then(({ data }) => {
      this.listTimeSheetMonth = Array.isArray(data.items) ? data.items : [];
      this.currentStaff = {
        name: data.staffName,
        id: data.staffId
      }
    }).catch(() => {
      toast.warning(i18n.t("toast.error"))
    })
  }

  getTimeSheetWeeks = (fromDate, toDate, staffId) => {
    searchTimeSheetDate({ staffId: staffId, fromDate: new Date(fromDate), toDate: new Date(toDate) }).then(({ data }) => {
      this.listTimeSheetWeeks = Array.isArray(data.items) ? data.items : [];
      this.currentStaff = {
        name: data.staffName,
        id: data.staffId
      }
    }).catch(() => {
      toast.warning(i18n.t("toast.error"))
    })
  }

  getTimeSheetDay = (date = new Date(), staffId) => {
    searchTimeSheetDate({ staffId: staffId, workingDate: date }).then(({ data }) => {
      this.listTimeSheetDay = Array.isArray(data.items) ? data.items : [];
      this.currentStaff = {
        name: data.staffName,
        id: data.staffId
      }
    }).catch(() => {
      toast.warning(i18n.t("toast.error"))
    })
  }

  handleDeleteTimeSheet = (timeSheet) => {
    this.dataEditTimeSheet = timeSheet;
    this.openPopupConfirm = true;
  }

  handleConfirmDeleteTimeSheet = () => {
    deleteTimeSheetDetail(this.dataEditTimeSheet?.id).then(() => {
      if (equalComparisonDate(this.currentMonth, this.dataEditTimeSheet?.workingDate, 'month')) {
        this.getTimeSheetMonth(this.currentStaff?.id)
      } else if (equalComparisonDate(this.dateValueMonth, this.dataEditTimeSheet?.workingDate)) {
        this.setValueCurrentMonth(this.valueCurrentMonth.filter(e => e?.id === this.dataEditTimeSheet?.id))
      }
      toast.success(i18n.t("toast.delete_success"));
      this.handleClosePopup();
    }).catch(() => toast.warning(i18n.t("toast.error")))
  }

  handleOpenFormTimeSheet = async (idTimeSheet) => {
    if (idTimeSheet) {
      const response = await getTimeSheetDetail(idTimeSheet);
      this.dataEditTimeSheet = response?.data;
    } else {
      this.dataEditTimeSheet = { ...dataDefaultFormTimeSheet, employee: this?.currentStaff };
    }
    this.openFormTimeSheet = true;
  }

  handleClosePopup = (namePopup) => {
    this.openPopupConfirm = false;
    this.openFormTimeSheet = false;
  }

  handleSubmitFormTimeSheet = (values) => {
    const newValues = {
      ...values,
      employee: { id: values?.employee?.id },
      project: { id: values?.project?.id },
      projectActivity: {
        ...values?.projectActivity,
        project: null,
        tableData: null,
      }
    }

    saveTimeSheetDetail(newValues).then(({ data }) => {
      if (equalComparisonDate(this.currentMonth, newValues?.timeSheet?.workingDate, 'month')) {
        this.getTimeSheetMonth(this.currentStaff?.id)
      } else if (equalComparisonDate(this.dateValueMonth, newValues?.timeSheet?.workingDate)) {
        const newValuesDate = this.valueCurrentMonth;
        const newItem = {
          ...data,
          workingDate: data?.timeSheet?.workingDate,
          project: data?.project?.name,
          activity: data?.projectActivity?.name
        }
        if (newValues?.id) {
          const indexItem = newValuesDate.findIndex(e => e?.id === data?.id);
          newValuesDate[indexItem] = newItem;
        } else {
          newValuesDate.push(newItem);
        }
        this.setValueCurrentMonth(newValuesDate)
      }
      toast.success('Thành Công!');
      this.handleClosePopup();
    }).catch(() => {
      toast.warning(i18n.t("toast.error"));
    })
  }
































  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };
  handleChangeProject = (id) => {
    this.projectId = id;
  };
  handleSelectProject = (item) => {
    this.selectedProject = item;
    if (item) {
      this.projectId = item.id;
    } else {
      this.projectId = "";
    }

    this.search_data_detail();
  };

  updateStatus = async (id, workingStatusId) => {
    try {
      await updateStatus(id, workingStatusId);
      this.search_data_detail();
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.load_fail"));
      this.setLoadingInitial(false);
    }
  };

  getProject = async () => {
    this.loadingInitial = true;
    var searchObject = {
      keyword: "",
      pageIndex: 1,
      pageSize: 10000,
    };

    try {
      let data = await pagingProject(searchObject);
      this.listProject = data.data.content;
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.load_fail"));
      this.setLoadingInitial(false);
    }
  };

  createProject = async (project) => {
    try {
      let response = await checkCode(project.id, project.code);
      if (response.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await createProject(project);
        toast.success(i18n.t("toast.add_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.add_fail"));
    }
  };

  getWorkingStatus = async () => {
    this.loadingInitial = true;
    var searchObject = {
      keyword: "",
      pageIndex: 1,
      pageSize: 10000,
    };
    try {
      let data = await pagingWorkingStatus(searchObject);
      this.listWorkingStatus = data.data.content;
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.load_fail"));
      this.setLoadingInitial(false);
    }
  };

  update_PageData_Detail = (item) => {
    this.getProject();
    this.getWorkingStatus();
    let neWSearchObject = {
      ...this.searchObj,
    };
    if (item != null) {
      this.page = 1;
      this.searchObj = {
        ...item,
        pageIndex: 1,
        pageSize: 5,
      };

      this.search_data_detail();
    } else {
      this.search_data_detail(neWSearchObject);
    }
  };

  search_data_detail = async (itemSearch) => {
    this.loadingInitial = true;
    let searchObject = {
      ...this.searchObj,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
      projectId: this.projectId,
    };

    if (itemSearch) {
      searchObject = {
        ...itemSearch,
        pageIndex: this.page,
        pageSize: this.rowsPerPage,
        projectId: this.projectId,
      };
    }
    try {
      let data = await pagingTimeSheetDetail(searchObject);
      this.timeSheetDetailList = data.data?.content ? data.data?.content : [];
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.load_fail"));
      this.setLoadingInitial(false);
    }
  };

  handleSetSearchObject = (item) => {
    this.page = 1;
    this.searchObj = item;
    this.search_data_detail(item);
  };

  setPage = (page) => {
    this.page = page;
    this.search_data_detail();
  };

  setRowsPerPage = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    this.update_PageData_Detail();
  };

  handleChangePage = (event, newPage) => {
    this.setPage(newPage);
  };

  handleEditProject = (id) => {
    this.getProject(id).then(() => {
      this.shouldOpenEditorProjectDialog = true;
    });
  };

  handleTotalTimeReport = async () => {
    this.shouldOpenTotalTimeReportDialog = true;
    var searchObject = {
      keyword: this.keyword,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
    };
    try {
      await totalTimeReport(searchObject);
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning("toast.load_fail");
      this.setLoadingInitial(false);
    }
  };

  handleClose = () => {
    this.shouldOpenEditorDialogTimeSheetDetail = false;
    this.shouldOpenEditorProjectDialog = false;
    this.shouldOpenTotalTimeReportDialog = false;
    this.shouldOpenConfirmationDeleteListDialog = false;
    this.shouldOpenConfirmationDialogTimeSheetDetail = false;
    this.update_PageData_Detail();
  };

  /* TimeSheetDetail */
  getTimeSheetDetail = async (id) => {
    if (id != null) {
      try {
        let data = await getTimeSheetDetail(id);
        this.handleSelectTimeSheetDetail(data.data);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.get_fail"));
      }
    } else {
      this.handleSelectTimeSheetDetail(null);
    }
  };

  handleSelectTimeSheetDetail = (timeSheetDetail) => {
    this.selectedTimeSheetDetail = timeSheetDetail;
  };

  handleSelectListTimeSheetDetail = (timeSheetDetail) => {
    this.selectedTimeSheetDetailList = timeSheetDetail;
  };

  getAllTimeSheetDetail = async (timesheetId) => {
    if (timesheetId != null) {
      try {
        let data = await getAllTimeSheetDetail(timesheetId);
        this.handleSelectTimeSheetDetailList(data.data);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.get_fail"));
      }
    } else {
      this.handleSelectTimeSheetDetailList(null);
    }
  };

  createTimeSheetDetail = async (timeSheetDetail) => {
    try {
      let res = await createTimeSheetDetail(timeSheetDetail);
      toast.success(i18n.t("toast.add_success"));
      this.handleClose();
      return res.data;
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.add_fail"));
    }
  };

  editTimeSheetDetail = async (timeSheetDetail) => {
    try {
      await editTimeSheetDetail(timeSheetDetail);
      toast.success(i18n.t("toast.update_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.update_fail"));
    }
  };

  handleEditTimeSheetDetail = (id) => {
    this.getTimeSheetDetail(id).then(() => {
      this.shouldOpenEditorDialogTimeSheetDetail = true;
    });
  };

  handleDeleteTimeSheetDetail = (id) => {
    this.getTimeSheetDetail(id).then(() => {
      this.shouldOpenConfirmationDialogTimeSheetDetail = true;
    });
  };

  handleConfirmDeleteTimeSheetDetail = async () => {
    try {
      await deleteTimeSheetDetail(this.selectedTimeSheetDetail.id);
      toast.success(i18n.t("toast.delete_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.delete_fail"));
    }
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedTimeSheetDetailList.length; i++) {
      try {
        await deleteTimeSheetDetail(this.selectedTimeSheetDetailList[i].id);
      } catch (error) {
        listAlert.push(this.selectedTimeSheetDetailList[i].name);
        console.log(error);
        console.log(listAlert.toString());
        toast.warning(i18n.t("toast.delete_fail"));
      }
    }
    this.handleClose();
    toast.success(i18n.t("toast.delete_success"));
  };

  getTimeSheetByTime = async (timeKeeping) => {
    try {
      const data = await getTimeSheetByTime(timeKeeping);
      this.handleSelectTimeSheetByTime(data.data);

    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.add_fail"));
      this.handleSelectTimeSheetByTime(null);
    }
  };

  handleSelectTimeSheetByTime = (timesheet) => {
    this.selectedTimeSheetByTime = timesheet;
  };

  getListProjectActivity = async (projectId) => {
    try {
      let data = await searchToList({ projectId: projectId });
      this.selectedActivityList = data.data ? data.data : [];
    } catch (error) {
      console.log(error);
      toast.warning("Bạn chưa chọn dự án");
    }
  };

  handleSelectListActivity(activities) {
    this.selectedActivityList = activities;
  }

  getStaff = async (id) => {
    try {
      let data = await getStaff(id);
      this.handleSelectStaff(data.data);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.get_fail"));
      this.handleSelectStaff(null);
    }
  };

  handleSelectStaff(timesheetStaff) {
    this.selectedStaff = timesheetStaff;
  }



  //GENERATE TIMESHEETDETAIL AUTOMATICALLY
  autoGenerateTimeSheetDetails = async (searchObject) => {
    try {
      const { data } = await autoGenerateTimeSheetDetails(searchObject);

      // console.log("generated data: ", data);
      this.getTimeSheetMonth(searchObject?.staffId);

      toast.success("Nhật kí công việc đã tự động được tạo, kiểm tra lại các thông tin cần thiết!");
    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi tự động tạo nhật kí, vui lòng thử lại sau");
    }
  }
}