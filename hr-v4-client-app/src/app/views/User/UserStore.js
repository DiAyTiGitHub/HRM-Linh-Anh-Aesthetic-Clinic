import { makeAutoObservable } from "mobx";

import {
  pagingUsers,
  getUser,
  saveUser,
  deleteUser,
  updateUser,
  resetPassWord,
  pagingUserWithStaff,
  saveUserAndChooseUsingStaff,
  getUserWithUsingStaff
} from "./UserService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { pagingLowerLevelStaff, pagingStaff } from "../HumanResourcesInformation/StaffService";
import localStorageService from "app/services/localStorageService";
import { getHrResourcePlanById } from "../HrResourcePlan/HrResourcePlanService";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class UserStore {
  userList = [];
  roles = [];
  selectedUser = null;
  totalElements = 0;
  totalPages = 0;
  // page = 1;
  // rowsPerPage = 10;
  keyword = "";
  loadingInitial = false;
  shouldOpenEditorDialog = false;
  shouldOpenConfirmationDialog = false;
  listSelected = [];

  intactSearchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    organization:null,
    organizationId:null,
    departmentId:null,
    department:null,
    position:null,
    positionId:null,
    positionTitle:null,
    positionTitleId:null,
    levelNumber:1,

  };
  searchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    organization:null,
    organizationId:null,
    departmentId:null,
    department:null,
    position:null,
    positionId:null,
    positionTitle:null,
    positionTitleId:null
  };
  openViewPopup = false;


  constructor () {
    makeAutoObservable (this);
  }

  handleOpenView = async (id) => {
    try {
      if (id) {
        const {data} = await getUserWithUsingStaff (id);
        this.selectedUser = data;
      } else {
        this.selectedUser = null;
      }
      this.openViewPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  updatePageData = (item) => {
    console.log (item);
    if (item != null) {
      this.keyword = item.keyword;
      this.search ();
    } else {
      this.search ();
    }
  };

  handleSetSearchObject = (searchObject) => {
    this.searchObject = {... searchObject};
  };

  search = async () => {
    this.loadingInitial = true;
    var searchObject = {pageIndex:1, pageSize:10, ... this.searchObject};
    // var searchObject = {
    //   keyword: this.keyword,
    //   pageIndex: this.page,
    //   pageSize: this.rowsPerPage,
    // };
    // searchObject.keyword = this.keyword;
    // searchObject.pageIndex = this.page;
    // searchObject.pageSize = this.rowsPerPage;

    try {
      // const data = await pagingUsers(searchObject);
      const data = await pagingUserWithStaff (searchObject);

      this.userList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

      this.setLoadingInitial (false);
    } catch (error) {
      console.error (error);
      toast.error ("Có lỗi xảy ra khi lấy dữ liệu");
      this.setLoadingInitial (false);
    }
  };

  pagingUserWithStaff = async () => {
    try {
      const payload = {
        ... this.searchObject,
      };
      const data = await pagingUserWithStaff (payload);

      this.userList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPage = (page) => {
    // this.page = page;
    this.searchObject.pageIndex = page;
    // console.log(this.searchObject);
    this.updatePageData ();
  };

  setRowsPerPage = (event) => {
    console.log (event.target.value);
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    // this.rowsPerPage = event.target.value;
    // this.page = 1;
    this.updatePageData ();
  };

  handleChangePage = (event, newPage) => {
    this.setPage (newPage);
  };

  handleEditUser = (id) => {
    this.getUser (id).then (() => {
      this.shouldOpenEditorDialog = true;
    });
  };

  handleClose = () => {
    this.shouldOpenEditorDialog = false;
    this.shouldOpenConfirmationDialog = false;
    this.openViewPopup = false;
    this.updatePageData ();
  };

  handleDelete = (id) => {
    this.getUser (id).then (() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleConfirmDelete = () => {
    this.deleteUser (this.selectedUser.id);
  };

  getUser = async (id) => {
    if (id != null) {
      try {
        // let data = await getUser(id);
        const data = await getUserWithUsingStaff (id);

        this.selectedUser = data?.data;
      } catch (error) {
        console.error (error);
        toast.error (i18n.t ("toast.error"));
      }
    } else {
      this.selectedUser = null;
    }
  };

  saveUser = async (user) => {
    try {
      // await saveUser(user);
      if (!user?.person && user?.staff) {
        user.person = user.staff;
      }
      await saveUserAndChooseUsingStaff (user);

      toast.success (i18n.t ("toast.add_success"));
      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  resetPassWord = async (user) => {
    try {
      await resetPassWord (user);
      toast.success (i18n.t ("toast.change_password_success"));
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.change_password_failure"));
    }
  };

  // updateUser = async (user) => {
  //   try {
  //     await updateUser(user);
  //     toast.success(i18n.t("toast.update_success"));
  //     this.handleClose();
  //   } catch (error) {
  //     console.error(error);
  //     toast.error(i18n.t("toast.error"));
  //   }
  // };

  deleteUser = async (id) => {
    try {
      await deleteUser (id);
      toast.success (i18n.t ("toast.delete_success"));
      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  //SECTION CODE USE FOR CHOOSING STAFF
  usingStaffSO = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    organization:null,
    organizationId:null,
    departmentId:null,
    department:null,
    position:null,
    positionId:null,
    positionTitle:null,
    positionTitleId:null,
    levelNumber:1
  };

  setSharedFilter = (key, value) => {
    this.usingStaffSO[key] = value;
  };


  totalStaffElements = 0;
  totalStaffPages = 0;
  listUsingStaff = [];

  resetUsingStaffSection = () => {
    this.usingStaffSO = {
      pageIndex:1,
      pageSize:10,
      keyword:null,
      organization:null,
      organizationId:null,
      departmentId:null,
      department:null,
      position:null,
      positionId:null,
      positionTitle:null,
      positionTitleId:null,
      levelNumber:1
    };

    this.totalStaffElements = 0;
    this.totalStaffPages = 0;
    this.listUsingStaff = [];
  };

  handleSetUsingStaffSO = (usingStaffSO) => {
    this.usingStaffSO = {... usingStaffSO};
    if (usingStaffSO?.department) {
      this.usingStaffSO.departmentId = usingStaffSO.department.id;
    } else {
      this.usingStaffSO.departmentId = null;
    }

    if (usingStaffSO?.organization) {
      this.usingStaffSO.organizationId = usingStaffSO.organization.id;
    } else {
      this.usingStaffSO.organizationId = null;
    }

    if (usingStaffSO?.positionTitle) {
      this.usingStaffSO.positionTitleId = usingStaffSO.positionTitle.id;
    } else {
      this.usingStaffSO.positionTitleId = null;
    }

    if (usingStaffSO?.position) {
      this.usingStaffSO.positionId = usingStaffSO.position.id;
    } else {
      this.usingStaffSO.positionId = null;
    }
  };

  pagingStaff = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser ();
      let payload = {
        ... this.usingStaffSO,
        // organizationId: this.usingStaffSO?.organization?.id || loggedInStaff?.user?.org?.id,
      };
      const data = await pagingStaff (payload);

      this.listUsingStaff = data.data.content;
      this.totalStaffElements = data.data.totalElements;
      this.totalStaffPages = data.data.totalPages;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.usingStaffSO.pageIndex = page;
    await this.pagingStaff ();
  };
  setPageSize = async (event) => {
    this.usingStaffSO.pageSize = event.target.value;
    this.usingStaffSO.pageIndex = 1;
    await this.pagingStaff ();
  };

  // Select staff
  setRowsPerPageSelectStaff = async (event) => {
    this.usingStaffSO.pageSize = event.target.value;
    this.usingStaffSO.pageIndex = 1;
    await this.pagingStaff ();
  };
  setPageIndexSelectStaff = async (page) => {
    this.usingStaffSO.pageIndex = page;
    await this.pagingStaff ();
  };
  handleChangePageSelectStaff = async (event, newPage) => {
    await this.setPageIndexSelectStaff (newPage);
  };

  //SelectMultipleStaffs
  setRowsPerPageSelectMultipleStaffs = async (event) => {
    this.usingStaffSO.pageSize = event.target.value;
    this.usingStaffSO.pageIndex = 1;
    await this.pagingLowerStaff ();
  };
  setPageIndexSelectMultipleStaffs = async (page) => {
    this.usingStaffSO.pageIndex = page;
    await this.pagingLowerStaff ();
  };
  handleChangePageSelectMultipleStaffs = async (event, newPage) => {
    await this.setPageIndexSelectMultipleStaffs (newPage);
  };

  // selected list user
  handleSelectedUser = (list) => {
    //console.log("listSelected:", list);
    this.listSelected = list;
  };
  handleClearUser = () => {
    this.listSelected = [];
  };

  checkAdmin = () => {
    let roles = localStorageService.getLoginUser ()?.user?.roles?.map ((item) => item.authority) || [];

    let auth = ["ROLE_SUPER_ADMIN", "ROLE_ADMIN", "HR_MANAGER"];
    if (roles.some ((role) => auth.indexOf (role) !== -1)) {
      return true;
    } else {
      return false;
    }
  };

  pagingLowerStaff = async () => {
    try {
      let payload = {
        ... this.usingStaffSO
      };
      const {data} = await pagingLowerLevelStaff (payload);
      this.listUsingStaff = data?.content || [];
      this.totalStaffElements = data?.totalElements || 0;
      this.totalStaffPages = data?.totalPages || 0;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

}
