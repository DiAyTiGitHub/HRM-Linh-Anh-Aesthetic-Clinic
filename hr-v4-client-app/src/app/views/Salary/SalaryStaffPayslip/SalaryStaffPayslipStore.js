import { makeAutoObservable } from "mobx";
import { pagingSalaryStaffPayslip, saveSalaryStaffPayslip, deleteMultiple, deleteSalaryStaffPayslip, updatePaidStatus, getById, handleCalculateSalary, updateSalaryStaff, generate } from "./SalaryStaffPayslipService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { RecruitmentPlan } from "app/common/Model/Recruitment/RecruitmentPlan";
import { SalaryStaffPayslip } from "app/common/Model/Salary/SalaryStaffPayslip";
import { SalaryResult } from "app/common/Model/Salary/SalaryResult";
import {
  downloadSalaryResultStaffItemImportTemplate,
  importSalaryResultStaffItemTemplate,
  viewSalaryResult,
  updateSalaryPayslipsPaidStatus,
} from "../SalaryResultDetail/SalaryResultStaffService";
import FileSaver, { saveAs } from "file-saver";
import { SalaryResultStaff } from "app/common/Model/Salary/SalaryResultStaff";
import { SearchSalaryResultStaff } from "app/common/Model/SearchObject/SearchSalaryResultStaff";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class SalaryStaffPayslipStore {
  intactSearchObject = {
    ... new SearchSalaryResultStaff(),
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
    salaryPeriod: null,
    salaryResult: null,
    // approvalStatus: 0,
    paidStatus: 0,
    staff: null,
    organization: null,
    department: null,
    isPayslip: true
  };

  isUser = false; //tách ra để xử lý bất đồng bộ
  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
  totalElements = 0;
  totalPages = 0;
  listSalaryStaffPayslip = [];
  openCreateEditPopup = false;
  selectedStaffPayslip = null;
  openConfirmDeletePopup = false;
  openSelectSignature = false;
  openConfirmDeleteListPopup = false;
  openPopupSalary = false;
  listOnDelete = [];
  openRecalculatePayslip = false;


  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listSalaryStaffPayslip = [];
    this.openCreateEditPopup = false;
    this.selectedStaffPayslip = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openPopupSalary = false;
    this.listOnDelete = [];

    this.openSelectSignature = false;
    this.onViewSalaryBoard = null;
    this.openViewSalaryBoard = false;
    this.openRecalculatePayslip = false;

  };

  // handleChangeViewApprovalStatus = (status) => {
  //   const so = { ...this.searchObject, approvalStatus: status };
  //   this.searchObject = so;
  // };

  handleChangeViewPaidStatus = (status) => {

    const so = { ...this.searchObject, paidStatus: status };
    this.searchObject = so;
  };


  handleSetSearchObject = (searchObject) => {
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

    if (searchObject.salaryTemplate == null) {
      searchObject.salaryTemplateId = null;
    } else {
      searchObject.salaryTemplateId = searchObject.salaryTemplate.id;
    }

    if (searchObject.salaryPeriod == null) {
      searchObject.salaryPeriodId = null;
    } else {
      searchObject.salaryPeriodId = searchObject.salaryPeriod.id;
    }

    this.searchObject = { ...searchObject };
  };

  mapTabToPaidStatus = (tab) => {
    // tab 0 => Tất cả
    if (tab === 0) return null;
    // tab 1 => Đã chi trả
    if (tab === 1) return LocalConstants.StaffPayslipsPaidStatus.PAID.value;
    // tab 2 => Chưa chi trả
    if (tab === 2) return LocalConstants.StaffPayslipsPaidStatus.UNPAID.value;

    return null;
  }

  pagingSalaryStaffPayslip = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      this.handleSetSearchObject(this.searchObject);

      const payload = {
        ...this.searchObject,
        paidStatus: this.mapTabToPaidStatus(this.searchObject.paidStatus),
        // organizationId: loggedInStaff?.user?.org?.id,
      };

      const data = await pagingSalaryStaffPayslip(payload);

      this.listSalaryStaffPayslip = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingSalaryStaffPayslip();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingSalaryStaffPayslip();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteSalaryStaffPayslips) => {
    this.listOnDelete = deleteSalaryStaffPayslips;
  };

  getById = async (payslipId) => {
    try {
      const { data } = await getById(payslipId);
      this.selectedStaffPayslip = data;
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleCalculateSalary = async (values) => {
    try {
      if (!values?.staff?.id) {
        toast.error("Chưa chọn nhân viên để tính lương");
        return;
      }
      if (!values?.salaryPeriod) {
        toast.error("Chưa chọn mẫu bảng lương");
        return;
      }
      const { data } = await handleCalculateSalary(values);
      this.pagingSalaryStaffPayslip();
      console.log(data);
      toast.success(data);
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleUpdateSalaryStaff = async (values) => {
    try {
      const { data } = await updateSalaryStaff(values);
      this.pagingSalaryStaffPayslip();
      toast.success(data);
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];
    this.openConfirmChangeStatus = false;
    this.onChooseStatus = null;
    this.openPopupSalary = false;
    this.openViewSalaryBoard = false;

    this.openSelectSignature = false;
    this.openRecalculatePayslip = false;

  };

  handleDelete = (salaryItem) => {
    this.selectedStaffPayslip = { ...salaryItem };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleOpenCreateEdit = async (payslipId) => {
    try {
      if (payslipId) {
        const { data } = await getById(payslipId);
        this.selectedStaffPayslip = {
          ...JSON.parse(JSON.stringify(data)),
        };
      } else {
        this.selectedStaffPayslip = {
          ...new SalaryStaffPayslip(),
        };
      }

      this.openCreateEditPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };


  handleOpenRecalculatePayslip = async (payslipId) => {
    try {
      if (payslipId) {
        const { data } = await getById(payslipId);
        this.selectedStaffPayslip = {
          ...JSON.parse(JSON.stringify(data)),
        };
      } else {
        this.selectedStaffPayslip = {
          ...new SalaryResultStaff(),
        };
      }

      this.openRecalculatePayslip = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleCalculatePayslip = async (payslip) => {
    try {
      const { data } = await handleCalculateSalary(payslip);

      this.selectedStaffPayslip = {
        ...JSON.parse(JSON.stringify(data)),
      };

      toast.success("Thông tin Phiếu lương đã được cập nhật");

      return data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteSalaryStaffPayslip(this?.selectedStaffPayslip?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingSalaryStaffPayslip();

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

      await this.pagingSalaryStaffPayslip();
      this.listOnDelete = [];

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveSalaryStaffPayslip = async (salaryItem) => {
    try {
      const { data } = await saveSalaryStaffPayslip(salaryItem);
      toast.success("Thông tin Phiếu lương nhân viên đã được lưu");
      this.handleClose();

      return data;
    } catch (error) {
      console.error(error);
      // if (error.response.status == 409) {
      //   toast.error("Mã Đóng bảo hiểm đã được sử dụng, vui lòng sử dụng mã Đóng bảo hiểm khác", {
      //     autoClose: 5000,
      //     draggable: false,
      //     limit: 5,
      //   });
      // }
      // else if (error.response.status == 304) {
      //   toast.warning("Đóng bảo hiểm mặc định của hệ thống không được phép chỉnh sửa", {
      //     autoClose: 5000,
      //     draggable: false,
      //     limit: 5,
      //   });
      // }
      // else {
      toast.error(i18n.t("toast.error"));
      // }

      throw new Error(i18n.t("toast.error"));
    }
  };

  getApprovalStatusName = (status) => {
    return LocalConstants.SalaryStaffPayslipApprovalStatus.getListData().find((i) => i.value === status)?.name;
  };

  getPaidStatusName = (status) => {
    return LocalConstants.StaffPayslipsPaidStatus.getListData().find((i) => i.value === status)?.name;
  };

  // update approval status
  openConfirmChangeStatus = false;
  onChooseStatus = false;

  handleOpenConfirmChangeStatus = (onChooseStatus) => {
    this.openConfirmChangeStatus = true;
    this.onChooseStatus = onChooseStatus;
  };

  handleOpenPopupSalary = () => {
    this.openPopupSalary = true;
  };

  handleRemoveActionItem = (onRemoveId) => {
    this.listOnDelete = this?.listOnDelete?.filter((item) => item?.id !== onRemoveId);
  };

  handleConfirmChangeStatus = async () => {
    try {
      if (this?.listOnDelete?.length <= 0) {
        toast.error("Không có bản ghi nào được chọn");
        this.handleClose();
        return;
      }

      const payload = {
        chosenPayslipIds: this.getSelectedIds(),
        salaryResultStaffIds: this.getSelectedIds(),
        paidStatus: this.onChooseStatus,
      };

      // const { data } = await updateApprovalStatus(payload);
      const { data } = await updatePaidStatus(payload);
      if (!data) throw new Error("");

      toast.success("Cập nhật trạng thái thành công!");

      await this.pagingSalaryStaffPayslip();

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  getSelectedIds = () => {
    const ids = [];
    this?.listOnDelete?.forEach(function (item) {
      ids.push(item?.id);
    });

    return ids;
  };


  onViewSalaryBoard = null;
  openViewSalaryBoard = false;

  searchPayrollObject = {
    salaryTemplate: null,
    salaryPeriod: null
  };

  handleSetSearchPayrollObject = so => {
    this.searchPayrollObject = so;
  }

  handeOpenViewSalaryBoard = () => {
    this.searchPayrollObject = {
      salaryTemplate: null,
      salaryPeriod: null
    };

    this.openViewSalaryBoard = true;
  }

  viewSalaryBoard = async () => {
    try {
      const { data } = await viewSalaryResult(this.searchPayrollObject);

      this.onViewSalaryBoard = {
        ...JSON.parse(JSON.stringify(data)),
      };
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  downloadSalaryResultStaffItemImportTemplate = async () => {
    try {
      const res = await downloadSalaryResultStaffItemImportTemplate();
      let blob = new Blob([res.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      saveAs(blob, "Mẫu nhập dữ liệu tính lương của nhân viên.xlsx");
    }
    catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
    finally {
    }
  }

  uploadSalaryResultStaffItemImportTemplate = async (event) => {
    try {
      const file = event?.target?.files[0];
      // console.log("file", file);

      const response = await importSalaryResultStaffItemTemplate(file);

      toast.success("Nhập mẫu giá trị lương thành công")

      await this.pagingSalaryStaffPayslip();

      return response;
    }
    catch (error) {
      console.error(error);
      toast.error("Nhập giá trị lương có lỗi, vui lòng thử lại sau")
    }
    finally {
    }
  };


  handleClickPrint = () => {
    this.openSelectSignature = true;
  };

  handleDownloadSlip = async (id, signatureId) => {
    try {
      if (id) {
        const res = await generate(id, signatureId);

        if (res && res.data) {
          const blob = new Blob([res.data], {
            type: "application/pdf",
          });

          FileSaver.saveAs(blob, `phieu-luong-${id}.pdf`);
        } else {
          toast.error("Không nhận được dữ liệu từ server.");
        }
      }
    } catch (error) {
      console.error(error);
      // toast.error(i18n.t("toast.error"));
    }
  };
}
