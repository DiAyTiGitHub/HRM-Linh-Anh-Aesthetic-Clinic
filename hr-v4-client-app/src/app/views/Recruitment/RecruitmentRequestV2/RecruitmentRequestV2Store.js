import {makeAutoObservable} from "mobx";
import {
    checkNumberIsWithinHeadcount,
    deleteMultiple,
    deleteRecruitmentRequest,
    downloadRecruitmentRequestTemplate,
    exportExcelByFilter,
    exportWord,
    getById,
    importRecruitmentRequest,
    pagingRecruitmentRequest,
    personInCharge,
    saveRecruitmentRequest,
    updateRequestStatus,
    autoGenCode, changeListStatus
} from "./RecruitmentRequestV2Service";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import {RecruitmentRequest} from "app/common/Model/Recruitment/RecruitmentRequest";
import LocalConstants, {HttpStatus} from "app/LocalConstants";
import {saveAs} from "file-saver";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class RecruitmentRequestStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        status: null,
        department: null,
        positionTitle: null,
        organization: null,
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listRecruitmentRequests = [];
    openCreateEditPopup = false;
    selectedRecruitmentRequest = new RecruitmentRequest();
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openChoicePersonInChargePopup = false;
    listChosen = [];
    currentTab = null;
    // handle for update recruitment request status
    openConfirmUpdateStatusPopup = false;
    onUpdateStatus = null;
    openDepartmentPopup = false;
    openViewPopup = false;
    openConfirmDialog = false;
    isNeedCheck = true;

    constructor() {
        makeAutoObservable(this);
    }

    payload = null;
    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listRecruitmentRequests = [];
        this.openCreateEditPopup = false;
        this.selectedRecruitmentRequest = new RecruitmentRequest();
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listChosen = [];
        this.openConfirmUpdateStatusPopup = false;
        this.openDepartmentPopup = false;
        this.onUpdateStatus = null;
        this.openChoicePersonInChargePopup = false;
        this.currentTab = null;
        this.openViewPopup = false;
        this.openConfirmDialog = false;
        this.isNeedCheck = true;
        this.payload = null;
    };
    exportExcelByFilter = async (searchObject = this.searchObject) => {
        try {
            const payload = {
                ...searchObject,
            };
            await exportExcelByFilter(payload);
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };
    handleOpenView = async (recruitmentRequest) => {
        try {
            if (recruitmentRequest) {
                const {data} = await getById(recruitmentRequest?.id);
                this.selectedRecruitmentRequest = data;
            } else {
                this.selectedRecruitmentRequest = {
                    ...new RecruitmentRequest(),
                };
            }

            this.openViewPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleExportWord = async (id) => {
        try {
            if (id) {
                await exportWord(id);
                toast.success("Đã tải xuống phiếu đề xuất tuyển dụng thành công");
            } else {
                toast.error(i18n.t("toast.error"));
            }
        } catch (error) {
            console.error("Error during export:", error);
            toast.error(i18n.t("toast.error"));
        }
    };
    handleChangePagingStatus = (status) => {
        const so = {...this.searchObject, status: status};
        this.searchObject = so;
    };

    handleSetSearchObject = (searchObject) => {
        if (searchObject.department == null) {
            searchObject.departmentId = null;
        } else {
            searchObject.departmentId = searchObject.department.id;
        }
        this.searchObject = {...searchObject};
    };

    mapTabToStatus = (tab) => {
        if (tab === null) return null;

        switch (tab) {
            case 0:
                return LocalConstants.RecruitmentRequestStatus.CREATED.value;
            case 1:
                return LocalConstants.RecruitmentRequestStatus.SENT.value;
            case 2:
                return LocalConstants.RecruitmentRequestStatus.APPROVED.value;
            case 3:
                return LocalConstants.RecruitmentRequestStatus.REJECTED.value;
            case 4:
                return LocalConstants.RecruitmentRequestStatus.HR_LEADER.value;
            case 5:
                return LocalConstants.RecruitmentRequestStatus.START_RECRUITING.value;
            default:
                return null;
        }
    };
    pagingRecruitmentRequest = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
                status: this.mapTabToStatus(this.searchObject.status),
                department: null,
                positionTitle: null,
                organization: null,
            };
            const data = await pagingRecruitmentRequest(payload);

            this.listRecruitmentRequests = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingRecruitmentRequest();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingRecruitmentRequest();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteRecruitmentRequests) => {
        this.listChosen = deleteRecruitmentRequests;
    };

    getById = async (recruitmentRequestId) => {
        try {
            if (!recruitmentRequestId) {
                this.selectedRecruitmentRequest = new RecruitmentRequest();
                return;
            }

            const {data} = await getById(recruitmentRequestId);
            this.selectedRecruitmentRequest = data;
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmUpdateStatusPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.onUpdateStatus = null;
        this.openChoicePersonInChargePopup = false;
        this.openViewPopup = false;
    };

    handleDelete = (position) => {
        this.selectedRecruitmentRequest = {...position};
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };
    handleOpenChoicePersonInCharge = () => {
        this.openChoicePersonInChargePopup = true;
    };
    handleCloseChoicePersonInCharge = () => {
        this.openChoicePersonInChargePopup = false;
    };
    handleOpenCreateEdit = async (recruitmentRequest) => {
        try {
            if (recruitmentRequest) {
                const {data} = await getById(recruitmentRequest?.id);
                this.selectedRecruitmentRequest = data;
            } else {
                this.selectedRecruitmentRequest = {
                    ...new RecruitmentRequest(),
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
            const {data} = await deleteRecruitmentRequest(this?.selectedRecruitmentRequest?.id || this?.selectedRecruitmentRequest?.recruitmentRequestId);
            toast.success(i18n.t("toast.delete_success"));
            await this.pagingRecruitmentRequest();
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteReport = async () => {
        try {
            const {data} = await deleteRecruitmentRequest(this?.selectedRecruitmentRequest?.id || this?.selectedRecruitmentRequest?.recruitmentRequestId);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listChosen?.length; i++) {
                deleteData.push(this?.listChosen[i]?.id);
            }

            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingRecruitmentRequest();
            this.listChosen = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveRecruitmentRequest = async (values) => {
        try {
            const data = {
                id: values.id,
                code: values.code,
                name: values.name,
                organization: values.organization,
                hrDepartment: values.hrDepartment,
                team: values.team,
                workPlace: values.workPlace,
                proposer: values.proposer,
                proposalDate: values.proposalDate,
                proposalReceiptDate: values.proposalReceiptDate,
                recruitingStartDate: values.recruitingStartDate,
                recruitingEndDate: values.recruitingEndDate,
                recruitmentRequestItems: [
                    {
                        positionTitle: values.positionTitle,
                        announcementQuantity: values.announcementQuantity,
                        inPlanQuantity: values.inPlanQuantity,
                        extraQuantity: values.extraQuantity,
                        professionalLevel: values.professionalLevel,
                        professionalSkills: values.professionalSkills,
                        gender: values.gender,
                        weight: values.weight,
                        height: values.height,
                        yearOfExperience: values.yearOfExperience,
                        otherRequirements: values.otherRequirements,
                        minimumAge: values.minimumAge,
                        maximumAge: values.maximumAge,
                        minimumIncome: values.minimumIncome,
                        maximumIncome: values.maximumIncome,
                        isWithinHeadcount: values.isWithinHeadcount,
                        isReplacementRecruitment: values.isReplacementRecruitment,
                        replacedPerson: values.replacedPerson,
                        workType: values.workType,
                        request: values.request,
                        description: values.description,
                    },
                ],
                positionRequests: values?.positionRequests,
            };
            const response = await saveRecruitmentRequest(data);
            if (response.status === HttpStatus.OK) {
                if (response.data.status === HttpStatus.OK) {
                    this.resetStore()
                    await this.pagingRecruitmentRequest();
                    toast.success("Thông tin Yêu cầu tuyển dụng đã được lưu");
                    this.handleClose();
                } else {
                    toast.warning(response.data.message);
                }
            }
        } catch (error) {
            toast("Có lỗi xảy ra");
        }
    };

    handleRemoveActionItem = (onRemoveId) => {
        this.listChosen = this?.listChosen?.filter((item) => item?.id !== onRemoveId);
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listChosen?.forEach(function (candidate) {
            ids.push(candidate?.id);
        });

        return ids;
    };

    handleOpenConfirmUpdateStatusPopup = (status) => {
        this.onUpdateStatus = status;
        this.openConfirmUpdateStatusPopup = true;
    };

    handleConfirmUpdateStatus = async () => {
        try {
            if (this?.listChosen?.length <= 0) {
                toast.error("Không có yêu cầu tuyển dụng nào được chọn");
                return;
            }

            if (this.onUpdateStatus == null) {
                throw new Error("On update status is invalid");
            }
            const payload = {
                chosenIds: this.getSelectedIds(),
                status: this.onUpdateStatus,
            };

            const response = await updateRequestStatus(payload);
            if (response?.status === HttpStatus.OK) {
                if (response.data.status === HttpStatus.OK) {
                    toast.success(response.data?.message);
                    this.onUpdateStatus = null;
                    this.handleClose();
                    await this.pagingRecruitmentRequest();
                } else {
                    toast.warning(response.data.message);
                }
            } else {
                toast.error(i18n.t("toast.error"));
            }
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handlePersonInCharge = async (dto) => {
        try {
            const {data} = await personInCharge(dto);
            // Kiểm tra status code
            console.log("handlePersonInCharge", data);
            if (data?.status >= 200 && data?.status < 300) {
                toast.success(data?.message || "Cập nhật trạng thái thành công!");
                this.onUpdateStatus = null;
                this.listChosen = [];
                this.handleClose();
                await this.pagingRecruitmentRequest();
            } else if (data.status >= 400 && data.status < 500) {
                toast.error(data?.message || "Lỗi từ phía client (4xx)");
            } else {
                toast.error("Có lỗi xảy ra, vui lòng thử lại sau");
            }
        } catch (error) {
            console.error(error);
            toast.error(error?.response?.data?.message || i18n.t("toast.error"));
        }
    };

    handelOpenDepartmentPopup = (value) => {
        this.openDepartmentPopup = value;
    };
    uploadFileExcel = async (event) => {
        const fileInput = event.target; // Lưu lại trước
        const file = fileInput.files[0];
        let message = "Nhập excel thất bại";

        try {
            await importRecruitmentRequest(file);
            toast.success("Nhập excel thành công");
            this.pagingRecruitmentRequest();
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


    handleDownloadRecruitmentRequestTemplate = async () => {
        try {
            const res = await downloadRecruitmentRequestTemplate();
            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });
            saveAs(blob, "Mẫu import yêu cầu tuyển dụng.xlsx");
            toast.success("Đã tải mẫu import yêu cầu tuyển dụng thành công");
        } catch (error) {
            toast.error("Tải mẫu import yêu cầu tuyển dụng thất bại");
            console.error(error);
        }
    };
    checkNumberIsWithinHeadcount = async (recruitmentRequestId, positionId, number) => {
        try {
            const response = await checkNumberIsWithinHeadcount(recruitmentRequestId, positionId, number);
            if (response.status === HttpStatus.OK) {
                if (response.data.status === HttpStatus.OK) {
                    toast.success(response.data?.message);
                    return response.data.data
                } else {
                    toast.warning(response.data.message);
                }
            }
        } catch (error) {
        }
    }

    handleOpenConfirmDialog = (value) => {
        this.openConfirmDialog = value;
    }

    handleIsNeedCheck = (value) => {
        this.isNeedCheck = value;
    }

    handleSavePayload = (values) => {
        this.payload = values;
    }

    autoGenCode = async (configKey) => {
        const response = await autoGenCode(configKey)
        if (response.status === HttpStatus.OK) {
            return response.data;
        }
    }

    handleStopRecruitment = async (id, status) => {
        const response = await changeListStatus(id, status)
        if (response.status === HttpStatus.OK) {
            this.pagingRecruitmentRequest()
        }
    }
}
