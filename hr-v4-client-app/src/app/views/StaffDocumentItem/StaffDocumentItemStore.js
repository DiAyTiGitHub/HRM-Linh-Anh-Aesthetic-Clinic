import { makeAutoObservable } from "mobx";
import {
  pagingStaffDocumentItem,
  getStaffDocumentItemById,
  saveStaffDocumentItem,
  deleteStaffDocumentItemById,
  deleteMultipleStaffDocumentItem,
  getStaffDocumentItemByTemplateAndStaff,
  saveTemplateAndStaff,
} from "./StaffDocumentItemService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { StaffDocumentItem } from "../../common/Model/HumanResource/StaffDocumentItem";
import { t } from "app/common/CommonFunctions";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class StaffDocumentItemStore {
  intactSearchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    staff:null,
    staffId:null,
    hrDocumentItem:null,
    hrDocumentItemId:null,
    hrDocumentTemplate:null,
    hrDocumentTemplateId:null,
    fromDate:null,
    toDate:null,

  };
  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));

  totalElements = 0;
  totalPages = 0;
  staffDocumentItemList = [];
  documentTemplate = null;
  staffDocumentStatus = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openCreateEditPopup = false;
  listOnDelete = [];
  isAdmin = false;
  selectedStaffDocumentItem = null;
  selectedstaffDocumentItemList = [];
  //listStaffDocumentItem = [];
  currentStaffId = null;
  currentTemplateId = null;
  currentTemplate = null;
  templateData = null;
  selectAll = false;
  selectedRows = [];

  constructor () {
    makeAutoObservable (this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.staffDocumentItemList = [];
    this.staffDocumentStatus = null;
    this.openConfirmDeletePopup = false;
    this.documentTemplate = null;
    this.openCreateEditPopup = false;
    this.selectedStaffDocumentItem = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    //this.listStaffDocumentItem = [];
    this.currentStaffId = null;
    this.currentTemplateId = null;
    this.currentTemplate = null;
    this.selectedRows = [];
    this.templateData = null;
  };

  setSelectedRows = (selectedRows) => {
    this.selectedRows = selectedRows;
  };

  setSelectAll = (selectAll) => {
    this.selectAll = selectAll;
  };

  setStaffId = (staffId) => {
    this.currentStaffId = staffId;
  };

  setTemplateId = (templateId) => {
    this.currentTemplateId = templateId;
  };

  setCurrentTemplate = (templateData) => {
    this.currentTemplate = templateData;
  };
  setStaffDocumentItemList = (staffDocumentItemList) => {
    this.staffDocumentItemList = staffDocumentItemList;
  };
  pagingStaffDocumentItemByStaff = async (dto) => {
    try {
      const payload = {
        ... dto,
      };
      const data = await getStaffDocumentItemByTemplateAndStaff (payload);
      this.templateData = data?.data;
      this.staffDocumentItemList = data?.data?.staffDocumentItems;
      this.documentTemplate = data?.data?.documentTemplate;
      this.staffDocumentStatus = data?.data?.staffDocumentStatus;
      return data.data;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  pagingStaffDocumentItem = async () => {
    try {
      const payload = {
        ... this.searchObject,
        staffId:this.searchObject?.staff?.id || null,
        hrDocumentItemId:this.searchObject?.hrDocumentItem?.id || null,
        hrDocumentTemplateId:this.searchObject?.hrDocumentTemplate?.id || null,
        staff:null,
        hrDocumentItem:null,
        hrDocumentTemplate:null,
      };
      console.log (payload)
      const data = await pagingStaffDocumentItem (payload);
      this.staffDocumentItemList = data?.data?.content || [];
      this.totalElements = data?.data?.totalElements || 0;
      this.totalPages = data?.data?.totalPages || 0;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  saveTemplateAndStaff = async (dto) => {
    return saveTemplateAndStaff (dto)
        .then ((result) => {
          console.log (result?.data?.staffDocumentItems);
          this.selectedstaffDocumentItemList = result?.data?.staffDocumentItems;
          toast.success (t ("Cập nhật thành công"));
          return result;
        })
        .catch ((err) => {
          console.error (err);
          toast.error (t ("toast.error"));
        });
  };

  setPageByStaffIndex = async (page) => {
    this.searchObject.pageIndex = page;
    await this.pagingStaffDocumentItemByStaff ();
  };

  setPageByStaffSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    await this.pagingStaffDocumentItemByStaff ();
  };

  handleChangeByStaffPage = async (event, newPage) => {
    await this.setPageByStaffIndex (newPage);
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;
    await this.pagingStaffDocumentItem ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    await this.pagingStaffDocumentItem ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleOpenCreateEdit = async (staffDocumentItemId) => {
    try {
      if (staffDocumentItemId) {
        const {data} = await getStaffDocumentItemById (staffDocumentItemId);
        this.selectedStaffDocumentItem = data;
      } else {
        this.selectedStaffDocumentItem = new StaffDocumentItem ();
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

  handleDelete = (staffDocumentItem) => {
    this.selectedStaffDocumentItem = {... staffDocumentItem};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleConfirmDeleteByStaff = async () => {
    try {
      const {data} = await deleteStaffDocumentItemById (this.selectedStaffDocumentItem.id);
      toast.success (i18n.t ("toast.delete_success"));
      await this.pagingStaffDocumentItemByStaff ();
      this.handleClose ();

      return data;
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };
  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteStaffDocumentItemById (this.selectedStaffDocumentItem.id);
      toast.success (i18n.t ("toast.delete_success"));
      await this.pagingStaffDocumentItem ();
      this.handleClose ();

      return data;
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteListByStaff = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        deleteData.push (this?.listOnDelete[i]?.id);
      }
      await deleteMultipleStaffDocumentItem (deleteData);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingStaffDocumentItemByStaff ();
      this.listOnDelete = [];

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
      await deleteMultipleStaffDocumentItem (deleteData);
      toast.success (i18n.t ("toast.delete_success"));

      await this.pagingStaffDocumentItem ();
      this.listOnDelete = [];

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleSelectListDelete = (StaffDocumentItem) => {
    this.listOnDelete = StaffDocumentItem;
  };

  saveStaffDocumentItem = async (StaffDocumentItem) => {
    try {
      const {data} = await saveStaffDocumentItem (StaffDocumentItem);
      toast.success ("Thông tin phụ cấp của nhân viên đã được lưu");
      this.handleClose ();

      return data;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
      throw new Error (i18n.t ("toast.error"));
    }
  };

  getStaffDocumentItem = async (id) => {
    if (id != null) {
      try {
        const {data} = await getStaffDocumentItemById (id);
        this.selectedStaffDocumentItem = data;
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

  checkAdmin = () => {
    let roles = localStorageService.getLoginUser ()?.user?.roles?.map ((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
    if (roles.some ((role) => auth.indexOf (role) !== -1)) {
      this.isAdmin = true;
      this.isUser = false;
    } else {
      this.isAdmin = false;
      this.isUser = true;
    }
  };

  setOpenCreateEditPopup = (value) => {
    this.openCreateEditPopup = value;
  };

  setSelectedStaffDocumentItem = (data) => {
    this.selectedStaffDocumentItem = {
      ... new StaffDocumentItem (),
      ... data,
    };
  };
}
