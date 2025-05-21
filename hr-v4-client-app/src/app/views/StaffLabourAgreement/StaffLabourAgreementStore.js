import { makeAutoObservable } from "mobx";
import {
  checkOverdueContract,
  deleteMultiple,
  deleteStaffLabourAgreement,
  downloadStaffLabourAgreementTemplate,
  exportExcelStaffLabourAgreement,
  exportHDLD,
  getById,
  getLastLabourAgreement,
  importStaffLabourAgreement,
  pagingStaffLabourAgreement,
  saveStaffLabourAgreement
} from "./StaffLabourAgreementService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { HttpStatus } from "../../LocalConstants";
import { saveAs } from "file-saver";
import { importPositionTitle } from "../PositionTitle/PositionTitleService";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});


export default class StaffLabourAgreementStore {
  intactSearchObject = {
    pageIndex: 1,
    pageSize: 10,
    keyword: null,
    staffId: null,
    staff: null,
    fromDate: null,
    toDate: null,
    contractOrganization: null,
    workOrganization: null,
    agreementStatus: 0,
    isOverdueContract: false
  };

  searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

  totalElements = 0;
  totalPages = 0;
  listStaffLabourAgreement = [];
  openCreateEditPopup = false;
  selectedStaffLabourAgreement = null;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  listOnDelete = [];

  searchObjectOfOverdueContract = JSON.parse(JSON.stringify(this.intactSearchObject));

  hasOverdueContract = false;
  openCreateEditPopupOverdueContract = false;
  listOverdueContract = null;
  totalPagesOfOverdueContract = 0;
  totalElementsOfOverdueContract = 0;

  openPreviewPopup = false;

  constructor() {
    makeAutoObservable(this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.listStaffLabourAgreement = [];
    this.openCreateEditPopup = false;
    this.selectedStaffLabourAgreement = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.listOnDelete = [];


    this.searchObjectOfOverdueContract = JSON.parse(JSON.stringify(this.intactSearchObject));
    this.totalElementsOfOverdueContract = 0;
    this.totalPagesOfOverdueContract = 0;

    this.listOverdueContract = null;
    this.totalElementsOfOverdueContract = null;
    this.totalPagesOfOverdueContract = null;
    this.hasOverdueContract = false;
    this.openCreateEditPopupOverdueContract = false;
  };

  handleSetOpenPreviewPopup = (value) => {
    this.openPreviewPopup = value;
  }

  handleSetSearchObject = (searchObject) => {
    if (searchObject.staff == null) {
      searchObject.staffId = null;
    } else {
      searchObject.staffId = searchObject.staff.id;
    }
    this.searchObject = { ...searchObject };
  };

  handleChangePagingStatus = async (status) => {
    this.searchObject = {
      ... this.searchObject,
      agreementStatus: status,
      pageIndex: 1,
    };

    this.listOnDelete = [];
    await this.pagingStaffLabourAgreement()
  };

  pagingStaffLabourAgreement = async () => {
    try {
      const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ... this.searchObject,
        agreementStatus: this.searchObject?.agreementStatus === 0 ? null : this.searchObject?.agreementStatus,
        // organizationId:loggedInStaff?.user?.org?.id ,
      };
      const data = await pagingStaffLabourAgreement(payload);

      this.listStaffLabourAgreement = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;

    await this.pagingStaffLabourAgreement();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;

    await this.pagingStaffLabourAgreement();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex(newPage);
  };

  handleSelectListDelete = (deleteStaffLabourAgreements) => {
    this.listOnDelete = deleteStaffLabourAgreements;
  };

  getById = async (staffLabourAgreementId) => {
    try {
      const { data } = await getById(staffLabourAgreementId);
      this.selectedStaffLabourAgreement = data;
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
    this.openPreviewPopup = false;
    this.setSelectedStaffLabourAgreement();
  };

  handleClosePopupOverdueContract = () => {
    this.openCreateEditPopupOverdueContract = false;
    this.searchObjectOfOverdueContract = {
      ... this.searchObjectOfOverdueContract,
      pageIndex: 1,
    };
    this.handleCheckHasOverdueContract();
  };

  handleDelete = (staffLabourAgreement) => {
    this.selectedStaffLabourAgreement = { ...staffLabourAgreement };
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  initialStaffLabourAgreement = {
    id: null,
    signedDate: null,
    startDate: null,
    endDate: null,
    agreementStatus: "",
    labourAgreementType: null,
    files: [],
    salaryUnit: null,
    salary: null,
    salaryArea: null,
    workingPlace: null,
    workingHourWeekMax: null,
    workingHourWeekMin: null,
    workingHour: null,
    labourAgreementNumber: null,
    contractOrganization: null,
    workOrganization: null,
    staff: null,
    voided: null,
    staffPercentage: null,
    staffInsuranceAmount: null,
    staffCode: null,
    salaryTemplate: null,
    contractType: null,
    attachments: [],
    contractDate: null,
    contractTypeCode: null,
    current: null,
    insuranceSalaryCoefficient: null,
    isCurrent: null,
    orgInsuranceAmount: null,
    orgPercentage: null,
    recruitmentDate: null,

    //bhxh
    socialInsuranceNumber: null,
    hasSocialIns: null,
    salaryInsuranceUnit: null,
    insuranceSalary: null,
    staffSocialInsurancePercentage: null,
    staffHealthInsurancePercentage: null,
    staffUnemploymentInsurancePercentage: null,
    staffTotalInsuranceAmount: null,
    orgSocialInsurancePercentage: null,
    orgHealthInsurancePercentage: null,
    orgUnemploymentInsurancePercentage: null,
    orgTotalInsuranceAmount: null,
    paidStatus: null,
    totalInsuranceAmount: null,
    insuranceStartDate: null,
    insuranceEndDate: null,
  };

  calculateStaffInsuranceAmount = (salaryInsurance, staffPercentage) => {
    if (
      isNaN(salaryInsurance) ||
      isNaN(staffPercentage) ||
      salaryInsurance < 0 ||
      staffPercentage < 0
    ) {
      return 0;
    }
    return (Number.parseFloat(salaryInsurance || 0) * Number.parseFloat(staffPercentage || 0) / 100);
  };

  calculateOrgInsuranceAmount = (salaryInsurance, orgPercentage) => {
    if (
      isNaN(salaryInsurance) ||
      isNaN(orgPercentage) ||
      salaryInsurance < 0 ||
      orgPercentage < 0
    ) {
      return 0;
    }
    return (Number.parseFloat(salaryInsurance || 0) * Number.parseFloat(orgPercentage || 0) / 100);
  };

  calculateUnionDuesAmount = (salaryInsurance, unionDuesPercentage) => {
    if (
      isNaN(salaryInsurance) ||
      isNaN(unionDuesPercentage) ||
      salaryInsurance < 0 ||
      unionDuesPercentage < 0
    ) {
      return 0;
    }
    return (Number.parseFloat(salaryInsurance || 0) * Number.parseFloat(unionDuesPercentage || 0) / 100);
  };

  calculateTotalInsuranceAmount = (orgInsuranceAmount, staffInsuranceAmount, unionDuesAmount) => {
    if (
      isNaN(orgInsuranceAmount) ||
      isNaN(staffInsuranceAmount) ||
      isNaN(unionDuesAmount) ||
      orgInsuranceAmount < 0 ||
      staffInsuranceAmount < 0 ||
      unionDuesAmount < 0
    ) {
      return 0;
    }
    return (Number.parseFloat(orgInsuranceAmount || 0) + Number.parseFloat(staffInsuranceAmount || 0)) + Number.parseFloat(unionDuesAmount || 0)
  };

  handleOpenCreateEdit = async (staffLabourAgreementId) => {
    try {
      if (staffLabourAgreementId) {
        const { data } = await getById(staffLabourAgreementId);
        this.selectedStaffLabourAgreement = data;
      } else {
        this.selectedStaffLabourAgreement = {
          ... this.initialStaffLabourAgreement,
        };
      }
      this.openCreateEditPopup = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  pagingOverdueContract = async () => {
    try {
      //const loggedInStaff = localStorageService.getLoginUser();
      const payload = {
        ... this.searchObjectOfOverdueContract,
        isOverdueContract: true,
        //organizationId: loggedInStaff?.user?.org?.id,
      };
      const data = await pagingStaffLabourAgreement(payload);

      this.listOverdueContract = data.data.content;
      this.totalElementsOfOverdueContract = data.data.totalElements;
      this.totalPagesOfOverdueContract = data.data.totalPages;
      this.openCreateEditPopupOverdueContract = true;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  handleExportHDLD = async (staffLabourAgreementId) => {
    try {
      if (staffLabourAgreementId) {
        // Call the export function and handle the download
        await exportHDLD(staffLabourAgreementId);

        // Handle the file download (if necessary, it may be done in the exportHDLD function itself)
        // If you still need to process the file download logic here, do it after the exportHDLD call
        // Example: You could display a message that the export was successful, but the file download is already handled
        toast.success("Đã tải xuống hợp đồng");  // Assuming you want a success message

      } else {
        // Handle case where the staffLabourAgreementId is not provided
        toast.error(i18n.t("toast.missingId"));
      }
    } catch (error) {
      console.error("Error during export:", error);
      toast.error(i18n.t("toast.error"));
    }
  };


  handleConfirmDelete = async () => {
    try {
      const { data } = await deleteStaffLabourAgreement(this?.selectedStaffLabourAgreement?.id);
      toast.success(i18n.t("toast.delete_success"));

      await this.pagingStaffLabourAgreement();

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

      await this.pagingStaffLabourAgreement();
      this.listOnDelete = [];

      this.handleClose();
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error"));
    }
  };

  saveStaffLabourAgreement = async (staffLabourAgreement) => {
    try {
      const { data } = await saveStaffLabourAgreement(staffLabourAgreement);
      toast.success("Thông tin Hợp đồng lao động đã được lưu");
      this.handleClose();

      return data;
    } catch (error) {
      console.error(error);
      toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập");
    }
  };
  setSelectedStaffLabourAgreement = (data) => {
    this.selectedStaffLabourAgreement = { ... this.initialStaffLabourAgreement, ...data };
  }

  setOpenCreateEditPopup = (value) => {
    this.openCreateEditPopup = value;
  };

  setPageIndexOfOverdueContract = async (page) => {
    this.searchObjectOfOverdueContract.pageIndex = page;
    await this.pagingOverdueContract();
  };

  setPageSizeOfOverdueContract = async (event) => {
    this.searchObjectOfOverdueContract.pageSize = event.target.value;
    this.searchObjectOfOverdueContract.pageIndex = 1;
    await this.pagingOverdueContract();
  };

  handleChangePageOfOverdueContract = async (event, newPage) => {
    await this.setPageIndexOfOverdueContract(newPage);
  };

  setOpenCreateEditPopupOverdueContract = async (value) => {
    this.openCreateEditPopupOverdueContract = value;
  }

  handleCheckHasOverdueContract = async () => {
    const payload = {
      ... this.searchObjectOfOverdueContract,
      isOverdueContract: true,
      //contractPreExpiryDays:
      //organizationId: loggedInStaff?.user?.org?.id,
    };
    const { data } = await checkOverdueContract(payload);
    this.hasOverdueContract = data;
    if (this.hasOverdueContract === true) {
      toast.warning("Cảnh báo có hợp đồng sắp hết hạn");
    }
  };

  handleHasAndPagingOverdueContract = async () => {
    this.handleCheckHasOverdueContract();
    if (this.openCreateEditPopupOverdueContract === true) {
      await this.pagingOverdueContract();
    }
  };
  getLastLabourAgreement = async (staffId) => {
    const response = await getLastLabourAgreement(staffId)
    if (response) {
      if (response.status === HttpStatus.OK) {
        if (response.data.status === HttpStatus.OK) {
          return response.data.data
        } else {
          toast.warning(response.data.message)
        }
      }
    }
  }

  uploadFileExcelStaffLabourAgreement = async (event) => {
    try {
      const fileInput = event.target;
      const file = fileInput.files[0];
      fileInput.value = null;
      await importStaffLabourAgreement(file);

      toast.success("Nhập excel thành công");

      this.searchObject = {
        ... this.searchObject,
        pageIndex: 1
      };
      await this.pagingStaffLabourAgreement()

    } catch (error) {
      console.error(error);

      if (error.response && error.response.status === 409) {
        toast.error("Mã Chức danh đã được sử dụng, vui lòng sử dụng mã Chức danh khác", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      } else if (error.response && typeof error.response.data === "string") {
        toast.error(error.response.data, {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      } else {
        toast.error("Nhập excel thất bại", {
          autoClose: 5000,
          draggable: false,
          limit: 5,
        });
      }
    } finally {
      this.handleClose();
    }
  };
  handleDownloadStaffLabourAgreementTemplate = async () => {
    try {
      const res = await downloadStaffLabourAgreementTemplate();
      let blob = new Blob([res.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs(blob, "Mẫu nhập dữ liệu hợp đồng.xlsx");
      toast.success("Đã tải mẫunhập dữ liệu hợp đồng");
    } catch (error) {
      console.error("Error downloading timesheet detail template:", error);
    }
  };
  handleExportExcelStaffLabourAgreementData = async () => {
    if (this.totalElements > 0) {
      try {
        const res = await exportExcelStaffLabourAgreement({ ... this.searchObject });
        toast.success(i18n.t("general.successExport"));
        let blob = new Blob([res.data], {
          type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });

        saveAs(blob, "Mẫu nhập dữ liệu hợp đồng.xlsx");
      } finally {

      }
    } else {
      toast.warning(i18n.t("general.noData"));
    }
  }
}
