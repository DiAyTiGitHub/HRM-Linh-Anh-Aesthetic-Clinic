import { makeAutoObservable } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchObject } from "app/common/Model/SearchObject/SearchObject";
import { HrDepartment } from "app/common/Model/HumanResource/HrDepartment";
import {
  deleteDepartment,
  deleteMultiple, downloadDepartmentTemplate,
  exportExcelDepartmentData,
  getDepartment, importDepartment,
  pagingDepartmentHierarchy,
  saveDepartment,
  autoGenCode
} from "../Department/DepartmentService";
import { saveAs } from "file-saver";
import { importRankTitle } from "../RankTitle/RankTitleService";
import { HttpStatus } from "../../LocalConstants";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class DepartmentV2Store {
  intactSearchObject = {
    ... new SearchObject(),
    isManager: false
  };

  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  listDepartment = [];
  totalElements = 0;
  totalPages = 0;

  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openCreateEditPopup = false;
  listOnDelete = [];
  selectedDepartment = null;
  openViewPopup = false;

  handleOpenView = async (departmentId) => {
    try {
      if (departmentId) {
        const { data } = await getDepartment(departmentId);
        this.selectedDepartment = {
          ...data,
        };

      } else {
        this.selectedDepartment = {
          ... new HrDepartment()
        };
      }
      this.openViewPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listDepartment = [];
    this.openCreateEditPopup = false;
    this.selectedDepartment = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    this.openViewPopup = false;
  };
  uploadFileExcel = async (event) => {
    const fileInput = event.target; // Lưu lại trước
    const file = fileInput.files[0];
    let message = "Nhập excel thất bại";

    try {
      await importDepartment(file)
      toast.success("Nhập excel thành công");
      this.searchObject = {
        ... this.searchObject,
        pageIndex: 1
      }
      this.pagingAllDepartment()
    } catch (error) {
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'string') {
          message = data;
        } else if (data.message) {
          message = data.message;
        }
      }
      toast.error(message);
    } finally {
      this.handleClose();
      fileInput.value = null;
    }
  };

  handleDownloadDepartmentTemplate = async () => {
    try {
      const res = await downloadDepartmentTemplate();
      let blob = new Blob([res.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs(blob, "Mẫu nhập dữ liệu phòng ban.xlsx");
      toast.success(i18n.t("general.successExport"));
    } catch (error) {
      console.error("Error downloading timesheet detail template:", error);
    }
  };

  handleExportExcelDepartmentData = async () => {
    if (this.totalElements > 0) {
      try {
        const res = await exportExcelDepartmentData({ ... this.searchObject });
        toast.success(i18n.t("general.successExport"));
        let blob = new Blob([res.data], {
          type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });

        saveAs(blob, "DuLieuPhongBan.xlsx");
      } finally {

      }
    } else {
      toast.warning(i18n.t("general.noData"));
    }
  }

  handleSetSearchObject = (searchObject) => {
    if (searchObject.organization == null) {
      searchObject.organizationId = null;
    } else {
      searchObject.organizationId = searchObject.organization.id;
    }

    if (searchObject.hrDepartmentType == null) {
      searchObject.hrDepartmentTypeId = null;
    } else {
      searchObject.hrDepartmentTypeId = searchObject.hrDepartmentType.id;
    }

    this.searchObject = { ...searchObject };
  };

  convertToTree = (data) => {
    const treeValues = [];

    const itemListClone = data?.data?.content;

    itemListClone.forEach((item) => {
      var items = this.getListItemChild(item).map(child => ({
        ...child,
        organization: item?.organization
      }));
      treeValues.push(...items);
    });

    return treeValues;
  }

  getListItemChild(item) {
    var result = [];
    var root = {};
    root.positionManager = item.positionManager
    root.name = item.name;
    root.code = item.code;
    root.id = item.id;
    root.description = item.description;
    root.displayOrder = item.displayOrder;
    root.foundedDate = item.foundedDate;
    root.parentId = item.parentId;
    root.industryBlock = item.industryBlock;
    root.foundedNumber = item.foundedNumber;
    root.shortName = item.shortName;
    root.sortNumber = item.sortNumber;
    root.hrDepartmentType = item.hrDepartmentType;
    root.func = item.func;
    root.children = item.children;
    result.push(root);
    if (item.children) {
      item.children.forEach((child) => {
        var childs = this.getListItemChild(child);
        result.push(...childs);
      });
    }
    return result;
  }

  pagingAllDepartment = async () => {
    try {
      const payload = {
        ... this.searchObject
      };
      const data = await pagingDepartmentHierarchy(payload);


      const treeValues = this.convertToTree(data);
      this.listDepartment = treeValues;

      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;
    await this.pagingAllDepartment();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    await this.pagingAllDepartment();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleOpenCreateEdit = async (departmentId) => {
    try {
      if (departmentId) {
        const { data } = await getDepartment(departmentId);
        this.selectedDepartment = {
          ...data,
        };

      } else {
        this.selectedDepartment = {
          ... new HrDepartment()
        };
      }
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openViewPopup = false;
  };

  handleDelete = (department) => {
    this.selectedDepartment = { ...department };
    this.openConfirmDeletePopup = true;
  };


  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteDepartment(this?.selectedDepartment?.id);

      toast.success(i18n.t("toast.delete_success"));
      await this.pagingAllDepartment();

      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
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

      await this.pagingSalaryItem();
      this.listOnDelete = [];

      this.handleClose();


    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };


  handleSelectListDelete = (listChosen) => {
    this.listOnDelete = listChosen;
  };
  validateDepartmentBeforeSave = (values) => {
    const currentDepartmentId = values?.id;
    const allDepartments = values?.children || [];

    const invalidChildren = allDepartments.filter(dept => {
      if (!dept.parentId) return false;

      return dept.parentId !== currentDepartmentId;
    });
    if (invalidChildren?.length > 0) {
      invalidChildren.map(dept => {
        toast.warning((dept?.name) + " " + ("đã trực thuộc phòng ban" + " " + (dept?.parent?.name)));
      });
      return false;
    } else {
      return true;
    }
  };
  saveDepartment = async (department) => {
    try {
      if (this.validateDepartmentBeforeSave(department)) {
        const { data } = await saveDepartment(department);
        toast.success("Thông tin phòng ban đã được lưu");
        this.handleClose();
      } else {
        return null;
      }
    } catch (error) {
      console.error(error);
      if (error.response.status == 409) {
        toast.error("Mã phòng ban đã được sử dụng, vui lòng sử dụng mã phòng ban khác", {
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

  getById = async (departmentId) => {
    try {
      if (departmentId) {
        const { data } = await getDepartment(departmentId);

        this.selectedDepartment = data;
      } else {
        this.selectedDepartment = {
          ... new HrDepartment()
        };
      }

      this.openCreateEditPopup = true;
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };
  autoGenCode = async (configKey) => {
    const response = await autoGenCode(configKey)
    if (response.status === HttpStatus.OK) {
      return response.data;
    }
  }
}
