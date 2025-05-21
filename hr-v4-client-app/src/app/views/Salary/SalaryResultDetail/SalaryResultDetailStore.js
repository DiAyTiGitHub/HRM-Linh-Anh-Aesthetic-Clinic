import { makeAutoObservable } from "mobx";
import {
  getById,
  getSalaryResultBoard,
  saveSalaryResult,
  deleteSalaryResult,
  searchSalaryResultBoard,
  exportFileImportSalaryValueByFilter,
  importFileSalaryValueByFilter,
  deleteMultiple,
  hasAnyOrphanedPayslips,
  mergeOrphansToSalaryBoard,
  getAllOrphanedPayslips,
  getBasicInfoById
} from "../SalaryResult/SalaryResultService";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SalaryResult } from "app/common/Model/Salary/SalaryResult";

import {
  deleteSalaryResultStaff,
  reCalculateRowByChangingCellValue,
  saveSalaryResultStaff,
  exportPdf,
  exportCalculateSalaryStaffsToExcel

} from "./SalaryResultStaffService";

import { chooseResultItems } from "./SalaryResultItemService";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import FileSaver from "file-saver";
import { saveAs } from "file-saver";
import { pagingSalaryResultStaff } from "../SalaryStaffPayslip/SalaryStaffPayslipService";


toast.configure({
  autoClose: 5000,
  draggable: false,
  limit: 5,
});

export default class SalaryResultDetailStore {
  intactSearchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
    staff: null,
    staffId: null,
    salaryPeriod: null,
    salaryPeriodId: null,
    salaryResult: null,
    salaryResultId: null,
    organization: null,
    organizationId: null,
    department: null,
    departmentId: null,
    positionTitle: null,
    positionTitleId: null,
  };

  totalPages = 0;
  totalElements = 0;
  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  onViewSalaryResult = new SalaryResult();

  openConfirmDeletePopup = false;
  openChooseStaffsPopup = false;
  openConfirmLockPayslipPopup = false;
  openConfirmDownloadPdfPopup = false;
  openConfirmDownloadExcelPopup = false;
  openConfirmDeleteListPopup = false
  orphanedPayslipsCount = 0;
  orphanedPayslipsList = [];
  openViewOrphanedPayslipsPopup = false;

  listSalaryResultStaffs = [];

  tabCU = 0;

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.totalElements = 0;
    this.totalPages = 0;
    this.onViewSalaryResult = new SalaryResult();
    this.openConfirmDeletePopup = false;
    this.tabCU = 0;
    this.orphanedPayslipsCount = 0;
    this.orphanedPayslipsList = [];
    this.openConfirmLockPayslipPopup = false;
    this.openConfirmDownloadPdfPopup = false;
    this.openConfirmDownloadExcelPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openViewOrphanedPayslipsPopup = false;
    this.listSalaryResultStaffs = [];

    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
  }



  getSalaryResultBoard = async (salaryResultId) => {
    try {
      const { data } = await getSalaryResultBoard(salaryResultId);

      this.onViewSalaryResult = data;

      return data;
    } catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi lấy dữ liệu chi tiết bảng lương");
    }
  };



  handleSetSearchObj = (so) => {
    if (so.staff == null) {
      so.staffId = null;
    } else {
      so.staffId = so.staff.id;
    }

    if (so.department == null) {
      so.departmentId = null;
    } else {
      so.departmentId = so.department.id;
    }

    if (so.organization == null) {
      so.organizationId = null;
    } else {
      so.organizationId = so.organization.id;
    }

    if (so.positionTitle == null) {
      so.positionTitleId = null;
    } else {
      so.positionTitleId = so.positionTitle.id;
    }

    const newSO = {
      ...this.searchObject,
      ...so,
    };

    this.searchObject = { ...newSO };
  }

  handleOpenConfirmLockPayslipPopup = () => {
    this.openConfirmLockPayslipPopup = true;
  }

  handleOpenChooseStaffsPopup = () => {
    this.openChooseStaffsPopup = true;
  }

  handleClose = async () => {
    this.openConfirmDeletePopup = false;
    this.openChooseStaffsPopup = false;
    this.openConfirmLockPayslipPopup = false;
    this.openConfirmDownloadPdfPopup = false;
    this.openConfirmDownloadExcelPopup = false;
    this.openConfirmDeleteListPopup = false;

    this.listOnDelete = [];
  };

  handleConfirmDeleteList = async () => {
    try {
      const salaryResultId = this.onViewSalaryResult?.id;
      if (!salaryResultId) {
        toast.error("Không xác định được bảng lương cần xóa");
      }

      await deleteMultiple([salaryResultId]);
      toast.success(i18n.t("toast.delete_success"));
      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleExportPopup = (state) => {
    this.openConfirmDownloadExcelPopup = state;
  }


  handleDelete = (salaryTemplate) => {
    this.onViewSalaryResult = { ...salaryTemplate };
    this.openConfirmDeletePopup = true;
  };

  handleGetSalaryTemplateData = async (salaryResultId) => {
    try {
      if (salaryResultId) {
        const { data } = await getById(salaryResultId);
        this.onViewSalaryResult = data;
      }
      else {
        this.onViewSalaryResult = {
          ...new SalaryResult(),
        };
      }

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteSalaryResult(this?.onViewSalaryResult?.id);
      toast.success(i18n.t("toast.delete_success"));

      this.handleClose();

      await this.pagingSalaryTemplates();
      return data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
      throw new Error(error);
    }
  };

  saveSalaryResult = async (salaryTemplate) => {
    try {
      const { data } = await saveSalaryResult(salaryTemplate);
      toast.success("Thông tin bảng lương đã được lưu");

      return data;
    } catch (error) {
      console.error(error);
      if (error.response.status == 409) {
        toast.error("Mã bảng lương đã được sử dụng, vui lòng sử dụng mã bảng lương khác", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      }
      else {
        toast.error(i18n.t("toast.error"));
      }

      return null;
    }
  };

  saveSalaryResultStaff = async (resultStaff) => {
    try {
      const { data } = await saveSalaryResultStaff(resultStaff);
      toast.success("Thông tin dòng bảng lương đã được lưu");

      return data;
    } catch (error) {
      console.error(error);
      if (error.response.status == 409) {
        toast.error("Mã bảng lương đã được sử dụng, vui lòng sử dụng mã bảng lương khác", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      }
      else {
        toast.error(i18n.t("toast.error"));
      }

      return null;
    }
  };


  selectedResultStaff = null;
  openConfirmDeleteResultStaff = false;

  setSelectedResultStaff = data => {
    this.selectedResultStaff = data;
  }

  handleOpenConfirmDeleteResultStaff = () => {
    this.openConfirmDeleteResultStaff = true;
  }

  handleCloseConfirmDeleteResultStaff = () => {
    this.openConfirmDeleteResultStaff = false;
  }

  handleConfirmDeleteResultStaff = async () => {
    try {
      const { data } = await deleteSalaryResultStaff(this?.selectedResultStaff?.id);
      toast.success("Đã loại bỏ nhân viên được chọn khỏi danh sách tính lương");

      if (!data) throw new Error("Something errored on delete");

      toast.info("Thông tin bảng lương đang được làm mới");

      await this.getSalaryResultBoard(this?.onViewSalaryResult?.id);

      this.handleCloseConfirmDeleteResultStaff();
      return data;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  }

  // handle for choosing multiple salary item
  handleCompleteChooseItems = async payload => {
    try {
      const { data } = await chooseResultItems(payload);

      if (!data) throw new Error("Something errored on choosing");

      return data;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("Có lỗi xảy ra khi xác nhận chọn thành phần lương"));
    }
  }


  handlePdfPopup = (state) => {
    this.openConfirmDownloadPdfPopup = state;

  }

  chosenItemIds = [];

  setChosenItemIds = ids => {
    this.chosenItemIds = ids;
  }

  handleChooseItem = (salaryItem) => {
    if (!salaryItem?.id) {
      return;
    }

    const data = JSON.parse(JSON.stringify(this.chosenItemIds));

    if (data.includes(salaryItem?.id)) {
      const existedInIndex = data.indexOf(salaryItem?.id);
      if (existedInIndex !== -1) {
        data.splice(existedInIndex, 1);
      }
    }
    else {
      data.push(salaryItem?.id);
    }

    this.setChosenItemIds(data);
  }


  reCalculateRowByChangingCellValue = async (changedCellId, reCalculatingRow) => {
    try {
      const payload = {
        changedCellId: changedCellId,
        reCalculatingRow: reCalculatingRow
      };
      const { data } = await reCalculateRowByChangingCellValue(payload);

      if (!data) throw new Error("Something error occured");

      return data;

    } catch (error) {
      console.error(error);
      toast.error(i18n.t("Có lỗi xảy ra khi tính toán lại dòng dữ liệu"));
    }
  }

  handExportPdfCalculateSalaryStaffs = async () => {
    toast.info("Vui lòng đợi, yêu cầu đang được xử lý");
    try {
      const payload = {
        staffs: this.onViewSalaryResult?.staffs,
        salaryPeriodId: this.onViewSalaryResult?.salaryPeriod?.id,
        salaryTemplateId: this.onViewSalaryResult?.salaryTemplate?.id,
        ...this.searchObject,
      };
      const res = await exportPdf(payload);
      this.exportType = null;

      if (res && res.data && res.status === 200) {
        const blob = new Blob([res.data], {
          type: 'application/pdf'
        });
        FileSaver.saveAs(blob, "Bang_Luong.pdf");
        toast.success(i18n.t("general.successExport"));
      } else {
        toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất PDF, vui lòng thử lại sau");
      }
    } catch (err) {
      console.error(err);
      toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất PDF, vui lòng thử lại sau");
    }
    this.handleClose();
  };



  handleImportFileSalaryValueByFilter = async (event) => {

    const fileInput = event.target;
    const file = fileInput.files[0];

    const payload = {
      ...this.searchObject,
    };

    importFileSalaryValueByFilter(file, payload)
      .then(() => {
        this.handleSetSearchObj()
        toast.success("Nhập excel thành công");
      })
      .catch(() => {
        toast.error("Nhập excel thất bại");
      })
      .finally(() => {
        // this.handleClose();
        fileInput.value = null;
      });
  };


  // Tải mẫu nhập giá trị lương
  handleExportFileImportSalaryValueByFilter = async () => {
    try {
      const payload = {
        ...this.searchObject,
        salaryPeriodId: this?.onViewSalaryResult?.salaryPeriod?.id,
        salaryTemplateId: this?.onViewSalaryResult?.salaryTemplate?.id
      };

      console.log("payload", payload);

      const res = await exportFileImportSalaryValueByFilter(payload);

      let blob = new Blob([res.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      saveAs(blob, "Mẫu nhập giá trị lương.xlsx");
      toast.success(i18n.t("general.successExport"));
    } catch (error) {
      console.error("Error downloading timesheet detail template:", error);
    }
  }

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleViewOrphanedPayslipsPopup = async () => {
    try {
      const salaryResultId = this.onViewSalaryResult?.id;

      const { data } = await getAllOrphanedPayslips(salaryResultId);

      this.orphanedPayslipsList = data || [];

      console.log("this.this.orphanedPayslipsList", this.orphanedPayslipsList);

      this.openViewOrphanedPayslipsPopup = true;

    } catch (error) {
      console.error(error);
    }
  }

  handleCloseViewOrphanedPayslip = async () => {
    try {
      const salaryResultId = this.onViewSalaryResult?.id;

      this.orphanedPayslipsList = [];
      await this.hasAnyOrphanedPayslips(salaryResultId);

      this.openViewOrphanedPayslipsPopup = false;
    }
    catch (error) {
      console.error(error);
    }
  }
}
