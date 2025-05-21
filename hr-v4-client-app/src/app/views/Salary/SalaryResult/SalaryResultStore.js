import { makeAutoObservable } from "mobx";
import {
    pagingSalaryResult,
    getById,
    saveSalaryResult,
    deleteMultiple,
    deleteSalaryResult,
    saveBoardConfig,
    getExcelSalaryResultBoard,
    getSalaryResultBoard,
    createSalaryBoardByPeriodAndTemplate,
    updateSalaryResultStatusNotApprovedYet,
    updateSalaryResultStatusApproved,
    updateSalaryResultStatusNotApproved, updateSalaryResultStatusLocked,
    exportExcelCommissionPayroll
} from "./SalaryResultService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { SalaryResult } from "app/common/Model/Salary/SalaryResult";
import FileSaver from "file-saver";
import { updateApprovalStatus } from "../SalaryStaffPayslip/SalaryStaffPayslipService";
import LocalConstants from "../../../LocalConstants";
import { saveAs } from "file-saver";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class SalaryResultStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        status: 0,
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    salaryResultStore = false;
    totalElements = 0;
    totalPages = 0;
    listSalaryResults = [];
    openCreateEditPopup = false;
    openFormSelectRows = false;
    selectedSalaryResult = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openExportCMPPopup = false;
    listOnDelete = [];
    selectedColumns = [];
    allColumns = [];
    printData = {};

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = {
            ...JSON.parse(JSON.stringify(this.searchObject))
        };
        this.totalElements = 0;
        this.totalPages = 0;
        this.listSalaryResults = [];
        this.openCreateEditPopup = false;
        this.selectedSalaryResult = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.allColumns = [];
        this.printData = {};
        this.openExportCMPPopup = false;
    };

    openConfirmChangeStatus = false;
    onChooseStatus = 0;

    handleOpenConfirmChangeStatus = (onChooseStatus) => {
        this.openConfirmChangeStatus = true;
        this.onChooseStatus = onChooseStatus;
    };
    getApprovalStatusName = () => {
        return LocalConstants.SalaryStaffPayslipApprovalStatus.getListData().find((i) => i.value == this.onChooseStatus)?.name;
    };

    handleConfirmChangeStatus = async () => {
        try {
            if (this?.listOnDelete?.length <= 0) {
                toast.error("Không có bản ghi nào được chọn");
                this.handleClose();
                return;
            } else {
                // Kiểm tra danh sách bản ghi được chọn
                if (!this.listOnDelete || this.listOnDelete.length <= 0) {
                    toast.error("Không có bản ghi nào được chọn");
                    this.handleClose();
                    return;
                }

                const listId = this.listOnDelete.map(item => item.id);

                let response;
                switch (this.onChooseStatus) {
                    case LocalConstants.SalaryStaffPayslipApprovalStatus.NOT_APPROVED_YET.value:
                        response = await updateSalaryResultStatusNotApprovedYet(listId);
                        break;
                    case LocalConstants.SalaryStaffPayslipApprovalStatus.APPROVED.value:
                        response = await updateSalaryResultStatusApproved(listId);
                        break;
                    case LocalConstants.SalaryStaffPayslipApprovalStatus.NOT_APPROVED.value:
                        response = await updateSalaryResultStatusNotApproved(listId);
                        break;
                    case LocalConstants.SalaryStaffPayslipApprovalStatus.LOCKED.value:
                        response = await updateSalaryResultStatusLocked(listId);
                        break;
                    default:
                        toast.error("Trạng thái không hợp lệ");
                        this.handleClose();
                        return;
                }


                // Kiểm tra kết quả từ API
                if (response && response.data) {
                    // Cập nhật trạng thái thành công
                    toast.success("Cập nhật trạng thái thành công!");

                    // Cập nhật đối tượng tìm kiếm với trạng thái mới
                    this.handleSetSearchObject({
                        ... this.searchObject,
                        status: this.onChooseStatus
                    });

                    // Làm mới danh sách phiếu lương
                    await this.pagingSalaryResult();

                    // Đóng modal
                    this.handleClose();
                } else {
                    // Xử lý trường hợp API trả về không thành công
                    toast.error("Cập nhật trạng thái thất bại");
                }
            }
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleChangeViewByStatus = async (status) => {
        this.searchObject = { ... this.searchObject, status: status, pageIndex: 1 };
        await this.pagingSalaryResult()
    };

    setAllColumns = (list) => {
        this.allColumns = list;
    }
    handleSetSearchObject = (searchObject) => {
        this.searchObject = { ...searchObject };
    };

    pagingSalaryResult = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject,
                organizationId: loggedInStaff?.user?.org?.id,
            };
            const data = await pagingSalaryResult(payload);

            this.listSalaryResults = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    getSalaryResultBoard = async (salaryResultId) => {
        try {
            const { data } = await getSalaryResultBoard(salaryResultId);

            this.printData = data;

            return data;
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu chi tiết bảng lương");
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingSalaryResult();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingSalaryResult();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteSalaryResults) => {
        this.listOnDelete = deleteSalaryResults;
    };

    handleSelectedColumns = (selectedColumns) => {
        this.selectedColumns = selectedColumns;
    };

    getById = async (salaryResultId) => {
        try {
            const { data } = await getById(salaryResultId);
            this.selectedSalaryResult = data;
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
        this.openFormSelectRows = false;
        this.listOnDelete = [];
        this.openConfirmChangeStatus = false;
        this.openExportCMPPopup = false;

    };

    handleDelete = (salaryResult) => {
        this.selectedSalaryResult = { ...salaryResult };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (salaryResultId) => {
        try {
            if (salaryResultId) {
                const { data } = await getById(salaryResultId);
                this.selectedSalaryResult = {
                    ...JSON.parse(JSON.stringify(data)),
                };
            } else {
                this.selectedSalaryResult = {
                    ... new SalaryResult(),
                };
            }

            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteSalaryResult(this?.selectedSalaryResult?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingSalaryResult();

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteOnly = async () => {
        try {
            const { data } = await deleteSalaryResult(this?.selectedSalaryResult?.id);
            toast.success(i18n.t("toast.delete_success"));

            if (!data) throw new Error("Something errored on delete");

            return data;
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

            await this.pagingSalaryResult();
            this.listOnDelete = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveBoardConfig = async (salaryResult) => {
        toast.info("Dữ liệu bảng lương đang được khởi tạo, vui lòng đợi trong giây lát!");

        try {
            const { data } = await saveBoardConfig(salaryResult);
            // const { data } = await saveSalaryResult(salaryResult);
            toast.dismiss();

            toast.success("Dữ liệu của bảng lương đã được tạo theo cấu trúc cài đặt", {
                autoClose: 5000,
                draggable: false,
                limit: 5,
            });
            this.handleClose();

            return data;
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã bảng lương đã được sử dụng, vui lòng sử dụng mã bảng lương khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }

            return null;
        }
    };

    saveSalaryResult = async (salaryResult) => {
        toast.info("Vui lòng đợi trong giây lát!");

        try {
            const { data } = await saveSalaryResult(salaryResult);
            toast.success("Thông tin Bảng lương đã được lưu");
            this.handleClose();

            return data;
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã bảng lương đã được sử dụng, vui lòng sử dụng mã bảng lương khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }

            return null;
        }
    };

    // open preview salaryResult
    isOpenPreview = false;

    handleOpenPreview = () => {
        this.isOpenPreview = true;
    };

    handleClosePreview = () => {
        this.isOpenPreview = false;
    };

    setOpenFormSelectRows = (state) => {
        console.log("state", state);
        this.openFormSelectRows = state;
    };

    handleExportExcelStaff = async (salaryResultId, nameSalaryResult) => {
        if (salaryResultId) {
            try {
                const res = await getExcelSalaryResultBoard(salaryResultId);

                if (res && res.data) {
                    const blob = new Blob([res.data], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });

                    const fileName = nameSalaryResult
                        ? `${nameSalaryResult
                            .trim()
                            .normalize("NFD") // Chuyển ký tự Unicode tổ hợp thành ký tự đơn
                            .replace(/[\u0300-\u036f]/g, "") // Loại bỏ dấu tiếng Việt
                            .replace(/[/\\?%*:|"<>]/g, "") // Loại bỏ các ký tự không hợp lệ
                            .replace(/\s+/g, "_") // Thay khoảng trắng bằng dấu gạch dưới
                        }.xlsx`
                        : res.headers["content-disposition"]
                            ? res.headers["content-disposition"].split("filename=")[1].replace(/"/g, "").trim()
                            : "BangLuong.xlsx";

                    FileSaver.saveAs(blob, fileName);
                } else {
                    toast.error("Không nhận được dữ liệu từ server.");
                }
            } catch (error) {
                toast.error("Có lỗi xảy ra khi xuất Excel.");
                console.error(error);
            }
        } else {
            toast.error(i18n.t("toast.error"));
        }
    };


    createSalaryBoardByPeriodAndTemplate = async (salaryResult) => {
        toast.info("Vui lòng đợi trong giây lát!");

        try {
            const { data } = await createSalaryBoardByPeriodAndTemplate(salaryResult);
            toast.success("Thông tin Bảng lương đã được tạo");
            this.handleClose();

            return data;
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Đã có bảng lương sử dụng cùng mẫu bảng lương và kỳ lương được tính toán, vui lòng kiểm tra lại", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }

            return null;
        }
    };

    handleOpenCMPPopup = () => {
        this.openExportCMPPopup = true;
    };

    // Tải mẫu nhập giá trị lương
    handleExportExcelCommissionPayroll = async (values) => {
        if (!values?.salaryPeriod?.id) {
            toast.info("Chưa chọn kỳ lương để xuất bảng lương hoa hồng");
            return;
        }

        toast.info("Bảng lương hoa hồng đang được tạo, vui lòng đợi", {
            autoClose: 5555,
            draggable: false,
            limit: 5,
        });

        try {
            const payload = {
                ...values,
                salaryPeriodId: values?.salaryPeriod?.id,

            };

            console.log("payload", payload);

            const res = await exportExcelCommissionPayroll(payload);

            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });

            saveAs(blob, `BANG_LUONG_HOA_HONG_KY_LUONG_${values?.salaryPeriod?.code}.xlsx`);

            toast.dismiss();
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
            toast.dismiss();
            toast.error("Có lỗi xảy ra khi xuất bảng lương hoa hồng");
        }
    }
}
