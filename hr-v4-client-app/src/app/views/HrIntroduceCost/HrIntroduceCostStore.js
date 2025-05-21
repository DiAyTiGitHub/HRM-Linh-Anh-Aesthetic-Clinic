import { makeAutoObservable } from "mobx";
import {
  deleteHrIntroduceCost, excelIntroduceCost,
  getHrIntroduceCostById,
  pagingHrIntroduceCost,
  saveHrIntroduceCost
} from "./HrIntroduceCostService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { HrIntroduceCost } from "../../common/Model/HumanResource/HrIntroduceCost";
import { SearchHrIntroduceCost } from "app/common/Model/SearchObject/SearchHrIntroduceCost";
import { saveAs } from "file-saver";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class HrIntroduceCostStore {

  intactSearchObject = {
    ... new SearchHrIntroduceCost ()
  };
  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));


  totalElements = 0;
  totalPages = 0;
  hrIntroduceCostList = [];

  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openCreateEditPopup = false;
  listOnDelete = [];
  selectedHrIntroduceCost = null;
  selectedHrIntroduceCostList = [];

  //listHrIntroduceCost = [];

  constructor () {
    makeAutoObservable (this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.hrIntroduceCostList = [];
    this.openCreateEditPopup = false;
    this.selectedHrIntroduceCost = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    //this.listHrIntroduceCost = [];
  };

  pagingHrIntroduceCost = async (staffId) => {
    try {
      const payload = {
        ... this.searchObject,
        staffId:staffId,
        introducedStaffId:this.searchObject?.introducedStaff?.id,
        introducedStaff:null,
        organizationId:this.searchObject?.organization?.id ?? null,
        organization:null,
        departmentId:this.searchObject?.department?.id ?? null,
        department:null,
        positionTitleId:this.searchObject?.positionTitle?.id ?? null,
        positionTitle:null,

        introStaffOrganizationId:this.searchObject?.introStaffOrganization?.id ?? null,
        introStaffOrganization:null,
        introStaffDepartmentId:this.searchObject?.introStaffDepartment?.id ?? null,
        introStaffDepartment:null,
        introStaffPositionTitleId:this.searchObject?.introStaffPositionTitle?.id ?? null,
        introStaffPositionTitle:null,
      };
      const data = await pagingHrIntroduceCost (payload);
      this.hrIntroduceCostList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;
    await this.pagingHrIntroduceCost ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    await this.pagingHrIntroduceCost ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleOpenCreateEdit = async (hrIntroduceCostId) => {
    try {
      if (hrIntroduceCostId) {
        const {data} = await getHrIntroduceCostById (hrIntroduceCostId);
        this.selectedHrIntroduceCost = data;
      } else {
        this.selectedHrIntroduceCost = new HrIntroduceCost ();
      }
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
  };

  handleDelete = (hrIntroduceCost) => {
    this.selectedHrIntroduceCost = {... hrIntroduceCost};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteHrIntroduceCost (this.selectedHrIntroduceCost.id);
      toast.success (i18n.t ("toast.delete_success"));
      await this.pagingHrIntroduceCost ();
      this.handleClose ();

      return data;
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  // handleConfirmDeleteList = async () => {
  //   try {
  //     const deleteData = [];

  //     for (let i = 0; i < this?.listOnDelete?.length; i++) {
  //       deleteData.push(this?.listOnDelete[i]?.id);
  //     }
  //     await deleteMultiple(deleteData);
  //     toast.success(i18n.t("toast.delete_success"));

  //     await this.pagingHrIntroduceCost();
  //     this.listOnDelete = [];

  //     this.handleClose();
  //   } catch (error) {
  //     console.error(error);
  //     toast.error(i18n.t("toast.error"));
  //   }
  // };

  handleSelectListDelete = (hrIntroduceCost) => {
    this.listOnDelete = hrIntroduceCost;
  };

  saveHrIntroduceCost = async (hrIntroduceCost) => {
    try {
      const {data} = await saveHrIntroduceCost (hrIntroduceCost);
      toast.success ("Thông tin phụ cấp của nhân viên đã được lưu");
      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
      throw new Error (i18n.t ("toast.error"));
    }
  };

  getHrIntroduceCost = async (id) => {
    if (id != null) {
      try {
        const {data} = await getHrIntroduceCostById (id);
        this.selectedHrIntroduceCost = data;
        this.openCreateEditPopup = true;
      } catch (error) {
        console.log (error);
        toast.warning (i18n.t ("toast.error"));
      }
    } else {
      this.handleSelectAllowance (null);
    }
  };

  handleSetSearchObject = (searchObject) => {
    if (searchObject.staff == null) {
      searchObject.staffId = null;
    } else {
      searchObject.staffId = searchObject.staff.id;
    }
    this.searchObject = {... searchObject};
  };

  // getListHrIntroduceCostByStaffId = async (staffId) => {
  //   try {
  //     const { data } = await getListHrIntroduceCostByStaffId(staffId);

  //     this.listHrIntroduceCost = data;

  //   } catch (error) {
  //     console.error(error);
  //     toast.error(i18n.t("toast.error"));
  //   }
  // };

  setOpenCreateEditPopup = (value) => {
    this.openCreateEditPopup = value;
  };

  setSelectedHrIntroduceCost = (data) => {
    this.selectedHrIntroduceCost = {
      ... new HrIntroduceCost (), ... data
    };
  };
  handleExcelIntroduceCostData = async () => {
    if (this.totalElements > 0) {
      try {
        const res = await excelIntroduceCost ({... this.searchObject});
        toast.success (i18n.t ("general.successExport"));
        let blob = new Blob ([res.data], {
          type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });

        saveAs (blob, "Tải xuống danh sách thưởng giới thiệu nhân sự.xlsx");
      } finally {

      }
    } else {
      toast.warning (i18n.t ("general.noData"));
    }
  }

}
