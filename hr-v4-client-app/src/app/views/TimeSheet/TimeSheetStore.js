import { makeAutoObservable } from "mobx";
import {
  pagingTimeSheet,
  getTimeSheet,
  createTimeSheet,
  createTimeKeeping,
  editTimeSheet,
  deleteTimeSheet,
  updateStatus,
  addLabelTask,
  getAllLabelByIdProject,
  totalTimeReport,
  editLabelTask,
  deleteLabelTask,
  getAllTimeSheetDetail,
  checkTimeSheetDetail,
  checkTimeKeeping,
  searchToList,
  searchToListPaging,
  getTimeKeepingByMonth,
} from "./TimeSheetService";
import { checkCode, createProject } from "../Project/ProjectService";
import { pagingProject } from "../Project/ProjectService";
import { pagingWorkingStatus } from "../WorkingStatus/WorkingStatusService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { getCurrentStaff } from "../profile/ProfileService";
import { getAllStaff, } from "./../HumanResourcesInformation/StaffService";
toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class TimeSheetStore {
  timeSheetList = [];
  selectedTimeKeepinglListt = null;
  selectedTimeSheeByTime = null;
  listProject = [];
  listWorkingStatus = [];
  ListLabelbyProject = [];
  selectedActivityList = [];
  selectedTimeSheetDetailList = [];
  selectedTimeSheet = null;
  selectedProject = null;
  selectedTimeSheetList = [];
  selectedStaff = null;
  selectedListStaffNotTimeKeeping = [];
  selectedListStaffNotDetail = [];

  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  keyword = "";
  loadingInitial = false;
  shouldOpenEditorDialog = false;
  shouldOpenEditorProjectDialog = false;
  shouldOpenTotalTimeReportDialog = false;
  shouldOpenConfirmationDialog = false;
  shouldOpenConfirmationDeleteListDialog = false;
  shouldOpenEditorTimeKeepingDialog = false;
  shouldOpenPopupListStaffNotDetail = false;
  shouldOpenPopupListStaffNotTimeKeeping = false;
  shouldOpenPopupListStaffNotTimeKeepingIn = false;
  projectId = null;
  searchObj = {
    pageIndex: 1,
    pageSize: 5,
    staffId: "",
    fromDate: null,
    toDate: null,
    workingStatusId: "",
    priority: null,
    workingDate: null,
  };
  constructor() {
    makeAutoObservable(this);
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
      this.projectId = "  ";
    }

    this.search_data();
  };

  updateStatus = async (id, workingStatusId) => {
    try {
      await updateStatus(id, workingStatusId);
      this.search_data();
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

  update_PageData = (item) => {
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

      this.search_data();
    } else {
      this.search_data(neWSearchObject);
    }
  };

  search_data = async (itemSearch) => {
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
      let data = await pagingTimeSheet(searchObject);
      this.timeSheetList = data.data?.content ? data.data?.content : [];
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
    this.search_data(item);
  };

  setPage = (page) => {
    this.page = page;
    this.search_data();
  };

  setRowsPerPage = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    this.update_PageData();
  };

  handleChangePage = (event, newPage) => {
    this.setPage(newPage);
  };

  /* updatePageData - StaffNotTimeKeeping */
  /* Start */
  updatePageData_StaffNotTimeKeeping = (item) => {
    let neWSearchObject = {
      ...this.searchObj,
    };
    if (item != null) {
      this.page = 1;
      this.searchObj = {
        ...item,
        pageIndex: 1,
        pageSize: 10,
      };

      this.searchData_StaffNotTimeKeeping();
    } else {
      this.searchData_StaffNotTimeKeeping(neWSearchObject);
    }
  };

  searchData_StaffNotTimeKeeping = async (itemSearch) => {
    this.loadingInitial = true;
    let searchObject = {
      ...this.searchObj,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
    };

    if (itemSearch) {
      searchObject = {
        ...itemSearch,
        pageIndex: this.page,
        pageSize: this.rowsPerPage,
      };
    }

    try {
      let data = await checkTimeKeeping(searchObject);
      this.selectedListStaffNotTimeKeeping = data.data?.content
        ? data.data?.content
        : [];
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.load_fail"));
      this.setLoadingInitial(false);
    }
  };

  handleSetSearchObject_StaffNotTimeKeeping = (item) => {
    this.page = 1;
    this.searchObj = item;
    this.searchData_StaffNotTimeKeeping(item);
  };

  setPage_StaffNotTimeKeeping = (page) => {
    this.page = page;
    this.searchData_StaffNotTimeKeeping();
  };

  setRowsPerPage_StaffNotTimeKeeping = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    this.updatePageData_StaffNotTimeKeeping();
  };

  handleChangePage_StaffNotTimeKeeping = (event, newPage) => {
    this.setPage_StaffNotTimeKeeping(newPage);
  };

  handleClose_StaffNotTimeKeeping = () => {
    this.shouldOpenPopupListStaffNotTimeKeeping = false;
  };
  handleClose_StaffNotTimeKeepingIn = () => {
    this.shouldOpenPopupListStaffNotTimeKeepingIn = false;
  };

  /* End */

  /* updatePageData - StaffNotTimeSheet */
  /* Start */
  updatePageData_StaffNotTimeSheet = (item) => {
    let neWSearchObject = {
      ...this.searchObj,
    };
    if (item != null) {
      this.page = 1;
      this.searchObj = {
        ...item,
        pageIndex: 1,
        pageSize: 10,
      };

      this.searchData_StaffNotTimeSheet();
    } else {
      this.searchData_StaffNotTimeSheet(neWSearchObject);
    }
  };

  searchData_StaffNotTimeSheet = async (itemSearch) => {
    this.loadingInitial = true;
    let searchObject = {
      ...this.searchObj,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
    };

    if (itemSearch) {
      searchObject = {
        ...itemSearch,
        pageIndex: this.page,
        pageSize: this.rowsPerPage,
      };
    }

    try {
      let data = await checkTimeSheetDetail(searchObject);
      this.selectedListStaffNotDetail = data.data?.content
        ? data.data?.content
        : [];
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.load_fail"));
      this.setLoadingInitial(false);
    }
  };

  handleSetSearchObject_StaffNotTimeSheet = (item) => {
    this.page = 1;
    this.searchObj = item;
    this.searchData_StaffNotTimeSheet(item);
  };

  setPage_StaffNotTimeSheet = (page) => {
    this.page = page;
    this.searchData_StaffNotTimeSheet();
  };

  setRowsPerPage_StaffNotTimeSheet = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    this.updatePageData_StaffNotTimeSheet();
  };

  handleChangePage_StaffNotTimeSheet = (event, newPage) => {
    this.setPage_StaffNotTimeSheet(newPage);
  };

  handleClose_StaffNotTimeTimeSheet = () => {
    this.shouldOpenPopupListStaffNotDetail = false;
    // this.updatePageData_StaffNotTimeSheet();
  };

  /* End */

  handleEditTimeSheet = (id) => {
    this.getTimeSheet(id).then(() => {
      // this.getAllTimeSheetDetail(id);
      this.shouldOpenEditorDialog = true;
    });
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
    this.shouldOpenEditorDialog = false;
    this.shouldOpenEditorProjectDialog = false;
    this.shouldOpenConfirmationDialog = false;
    this.shouldOpenTotalTimeReportDialog = false;
    this.shouldOpenConfirmationDeleteListDialog = false;
    this.shouldOpenEditorTimeKeepingDialog = false;
    this.shouldOpenPopupListStaffNotTimeKeeping = false;
    this.shouldOpenPopupListStaffNotTimeKeepingIn = false;
    this.update_PageData();
  };

  /* TimeSheet */
  getTimeSheet = async (id) => {
    if (id != null) {
      try {
        let data = await getTimeSheet(id);
        this.handleSelectTimeSheet(data.data);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.get_fail"));
      }
    } else {
      this.handleSelectTimeSheet(null);
    }
  };

  createTimeSheet = async (timeSheet) => {
    try {
      await createTimeSheet(timeSheet);
      toast.success(i18n.t("toast.add_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.add_fail"));
    }
  };

  editTimeSheet = async (timeSheet) => {
    try {
      await editTimeSheet(timeSheet);
      toast.success(i18n.t("toast.update_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.update_fail"));
    }
  };

  handleDelete = (id) => {
    this.getTimeSheet(id).then(() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleConfirmDelete = async () => {
    try {
      await deleteTimeSheet(this.selectedTimeSheet.id);
      toast.success(i18n.t("toast.delete_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.delete_fail"));
    }
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedTimeSheetList.length; i++) {
      try {
        await deleteTimeSheet(this.selectedTimeSheetList[i].id);
      } catch (error) {
        listAlert.push(this.selectedTimeSheetList[i].name);
        console.log(error);
        console.log(listAlert.toString());
        toast.warning(i18n.t("toast.delete_fail"));
      }
    }
    this.handleClose();
    toast.success(i18n.t("toast.delete_success"));
  };

  handleSelectTimeSheet = (timeSheet) => {
    this.selectedTimeSheet = timeSheet;
  };

  handleSelectListTimeSheet = (timeSheets) => {
    this.selectedTimeSheetList = timeSheets;
  };

  /* TimeKeeping */
  createTimeKeeping = async (timeKeeping) => {
    try {
      await createTimeKeeping(timeKeeping);
      toast.success(i18n.t("toast.timekeeping_success"));

      this.handleClose();
      this.getTimeKeepingByMonth(timeKeeping.workingDate)
    } catch (error) {
      console.log(error);
      // toast.warning(i18n.t("toast.add_fail"));
      toast.error("Có lỗi xảy ra, vui lòng thử lại sau");
    }
  };

  // getTimeSheetByDate = async (timeKeeping) => {
  //   try {
  //     const data = await getTimeSheetByTime(timeKeeping);
  //     this.handleSelectTimeSheetByTime(data.data);
  //     this.shouldOpenEditorTimeKeepingDialog = true;
  //   } catch (error) {
  //     console.log(error);
  //     toast.warning(i18n.t("toast.add_fail"));
  //     this.handleSelectTimeSheetByTime(null);
  //   }
  // };

  handleOpenEditorTimeKeeping = (timeKeeping) => {
    this.handleSelectTimeSheetByTime(timeKeeping);
    this.shouldOpenEditorTimeKeepingDialog = true;
  };

  handleSelectTimeSheetByTime = (timeKeeping) => {
    this.selectedTimeSheeByTime = timeKeeping;
  };

  handleOpenPopupListStaffNotTimeKeeping = () => {
    this.shouldOpenPopupListStaffNotTimeKeeping = true;
  };
  handleOpenPopupListStaffNotTimeKeepingIn = () => {
    this.shouldOpenPopupListStaffNotTimeKeepingIn = true;
  };

  handleOpenPopupListStaffNotDetail = () => {
    this.shouldOpenPopupListStaffNotDetail = true;
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


  handleSelectTimeSheetDetailList = (timeSheetDetailList) => {
    this.selectedTimeSheetDetailList = timeSheetDetailList;
  };

  /* TimeKeeping */
  getTimeKeepingByMonth = async (month, id ) => {
    if (month !== null) {
      try {
        let data = await getTimeKeepingByMonth({yearReport : new Date(month).getFullYear(), monthReport : new Date(month).getMonth() + 1, staffId : id });
        this.handleSelectTimeKeepingByMonth(data.data);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.get_fail"));
      }
    } else {
      this.handleSelectTimeKeepingByMonth(null);
    }
  };


  handleSelectTimeKeepingByMonth = (timeKeepinglList) => {
    this.selectedTimeKeepinglListt = timeKeepinglList;
  };

  /* Other */
  getListLabelByProjectId = async (projectId) => {
    try {
      let data = await getAllLabelByIdProject(projectId);
      this.ListLabelbyProject = data.data ? data.data : [];
    } catch (error) {
      console.log(error);
      toast.warning("Bạn chưa chọn dự án");
    }
  };

  searchToList = async (searchDto) => {
    try {
      const data = await searchToList(searchDto);
      this.handleSelectListActivity(data.data);

    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.add_fail"));
      this.handleSelectTimeSheetByTime(null);
    }
  };

  updatePageDataListActivity = (item) => {
    let neWSearchObject = {
      ...this.searchObj,
    };
    if (item != null) {
      this.page = 1;
      this.searchObj = {
        ...item,
        pageIndex: 1,
        pageSize: 10,
      };

      this.searchToListByPage();
    } else {
      this.searchToListByPage(neWSearchObject);
    }
  };

  searchToListByPage = async (searchDto) => {
    try {
      const response = await searchToListPaging({
        ...searchDto,
        pageIndex: this.page,
        pageSize: this.rowsPerPage,
        keyword: this.keyword,
      });
      this.selectedActivityList = response.data.content;
      this.totalPages = response.data.totalPages;
      this.totalElements = response.data.totalElements;
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.add_fail"));
      this.handleSelectTimeSheetByTime(null);
    }
  };

  handleChangeSearchToList = (searchDto, values) => {
    this.keyword = values;
    this.pageIndex = 1;
    this.pageSize = 10;
    this.searchToListByPage(searchDto);
  };

  handleChangePageIndexTolist = (searchDto, pageIndex) => {
    this.page = pageIndex;
    this.searchToListByPage(searchDto);
  };

  handleChangePageSizeToList = (searchDto, pageSize) => {
    this.rowsPerPage = pageSize;
    this.searchToListByPage(searchDto);
    this.page = 1;
  };

  handleSelectListActivity(activities) {
    this.selectedActivityList = activities;
  }

  getListProjectActivity = async (projectId) => {
    try {
      let data = await searchToList({ projectId: projectId });
      this.selectedActivityList = data.data ? data.data : [];
    } catch (error) {
      console.log(error);
      toast.warning("Bạn chưa chọn dự án");
    }
  };

  getCurrentStaff = async () => {
    try {
      let data = await getCurrentStaff();
      this.handleSelectStaff(data?.data);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.get_fail"));
      this.handleSelectStaff(null);
    }
  };

  handleSelectStaff(timesheetStaff) {
    this.selectedStaff = timesheetStaff;
  }

  addLabelTask = async (item) => {
    if (item.id) {
      try {
        await editLabelTask(item);
        toast.success(i18n.t("toast.add_success"));
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.add_fail"));
      }
    } else {
      try {
        await addLabelTask(item);
        toast.success(i18n.t("toast.edit_success"));
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.edit_fail"));
      }
    }
  };

  deleteLabelTask = async (item) => {
    try {
      await deleteLabelTask(item);
      toast.success(i18n.t("toast.delete_fail"));
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.delete_success"));
    }
  };
  handlePagingStaff = async () => {
    let data = await getAllStaff();
    return data.data;
  };
  handlePagingProject = async () => {
    let object = { pageIndex: 1, pageSize: 50 };
    let data = await pagingProject(object);
    return data.data.content;
  };
}
