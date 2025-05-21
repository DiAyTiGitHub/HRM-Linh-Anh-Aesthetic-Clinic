import {Candidate} from "app/common/Model/Candidate/Candidate";
import {SearchObjectCandidate} from "app/common/Model/SearchObject/SearchObjectCandidate";
import {Staff} from "app/common/Model/Staff";
import LocalConstants, {HttpStatus} from "app/LocalConstants";
import i18n from "i18n";
import {makeAutoObservable} from "mobx";
import {toast} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {importExcelListNewStaff, uploadImage} from "../../HumanResourcesInformation/StaffService";
import {
    distributeCandidatesForFirstRecruitmentRound
} from "../CandidateRecruitmentRound/CandidateRecruitmentRoundService";
import {
    approveCV,
    checkDuplicateCandidate,
    deleteCandidate,
    deleteMultiple,
    downloadCandidateTemplate,
    getById,
    getExistCandidateProfileOfStaff,
    importCandidate,
    pagingCandidates,
    saveCandidate, sendMail,
    updateStatus,
    exportExcelRecruitmentReports,
    autoGenCode, getPreviewMail, sendMailEdit
} from "./CandidateService";
import {saveAs} from "file-saver";

toast.configure({
    autoClose: 5000,
    draggable: false,
    limit: 5,
});

export default class CandidateStore {
    intactSearchObject = {
        ...new SearchObjectCandidate(),
        //trạng thái ứng viên: 1- chưa phê duyệt, 2- đã duyệt, 3- đã từ chối
        //default viewing approval status is 1 = candidates who are not approved yet
        status: 0,
        organization: null,
        positionTitle: null,
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    totalElements = 0;
    totalPages = 0;
    listCandidates = [];
    selectedCandidate = new Candidate();
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    openRecruitmentRound = false;
    openSelectMultiplePopup = false;
    openResetApprovalStatus = false;
    selectedRound = null;
    openApprovePopup = false; // Popup "Đã duyệt"
    openRejectPopup = false; // Popup "Đã từ chối"
    openChooseTemplatePopup = false; // Popup "Đã từ chối"
    openScreenedPassPopup = false; // Popup "Đã sơ lọc"
    openNotScreenedPopup = false; // Popup "Không qua sơ lọc"
    openCreateInterviewsPopup = false; // Popup "Tạo lịch phỏng vấn"
    openCreateCandidateForm = false;
    isOpenFilter = false;
    openPopupExportExcelRecruitmentReports = false;
    tabCU = 0;
    openPopupNextRound = false

    constructor() {
        makeAutoObservable(this);
    }

    handleOpenMul = () => {
        this.openSelectMultiplePopup = true;
    };
    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listCandidates = [];
        this.listCandidateRecruitmentRounds = [];
        this.selectedCandidate = new Staff();
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openResetApprovalStatus = false;
        this.openApprovePopup = false;
        this.openRejectPopup = false;
        this.openChooseTemplatePopup = false;
        this.openScreenedPassPopup = false;
        this.openNotScreenedPopup = false;
        this.openCreateInterviewsPopup = false; // Popup "Tạo lịch phỏng vấn"
        this.selectedRound = null;
        this.openPopupNextRound = false;
        this.tabCU = 0;

        this.openListDuplicate = false;
        this.candidateProfilesOfStaff = [];
        this.openCreateCandidateForm = false;
        this.openPopupExportExcelRecruitmentReports = false;
    };

    setOpenConfirmDeletePopup = (state) => {
        this.openConfirmDeletePopup = state;
    };
    setTabCU = (tabCU) => (this.tabCU = tabCU);

    handleSetSearchObject = (searchObject) => {
        if (searchObject.department == null) {
            searchObject.departmentId = null;
        } else {
            searchObject.departmentId = searchObject.department.id;
        }
        this.searchObject = {...searchObject};
    };

    handleAddPropertiesSearchObj = (searchObject) => {
        this.searchObject = {...this.searchObject, ...searchObject}
    }

    //lọc theo trạng thái hồ sơ = thay đổi tab
    handleChangeApprovalStatus = (status) => {
        const so = {...this.searchObject, status: status};
        this.searchObject = so;
    };

    handleSetSelectedCandidate = (candidate) => {
        this.selectedCandidate = candidate;
    };

    pagingCandidates = async () => {
        try {
            const searchData = {...this?.searchObject};
            if (!searchData?.status || searchData?.status === 0) searchData.status = null;

            const data = await pagingCandidates(searchData);

            this.listCandidates = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingCandidates();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingCandidates();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSetOpenCreateCandidateForm = () => {
        this.openCreateCandidateForm = true;
    };

    handleSelectListDelete = (deleteCandidates) => {
        this.listOnDelete = deleteCandidates;
    };

    handleOpenRecruitmentRound = () => {
        this.openRecruitmentRound = true;
    };

    getById = async (candidateId) => {
        try {
            const {data} = await getById(candidateId);

            this.selectedCandidate = data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleClose = async () => {
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openSelectMultiplePopup = false;
        this.openResetApprovalStatus = false;
        this.openApprovePopup = false;
        this.openRejectPopup = false;
        this.openChooseTemplatePopup = false;
        this.openScreenedPassPopup = false;
        this.openNotScreenedPopup = false;
        this.openCreateInterviewsPopup = false; // Popup "Tạo lịch phỏng vấn"
        this.listOnDelete = [];
        this.openCreateCandidateForm = false;
        this.openRecruitmentRound = false;
        this.openPopupExportExcelRecruitmentReports = false;
        this.openPopupNextRound = false
        await this.pagingCandidates();
    };

    handleDelete = (candidate) => {
        this.selectedCandidate = {...candidate};
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleGetCandidateData = async (candidateId) => {
        try {
            if (candidateId) {
                const {data} = await getById(candidateId);
                data.hrDepartment = data?.recruitment?.hrDepartmentCS;
                console.log("on display data: ", data);
                this.selectedCandidate = data;
                return this.selectedCandidate;
            } else {
                this.selectedCandidate = {
                    ...new Candidate(),
                    submissionDate: new Date(),
                };
                return this.selectedCandidate;
            }
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDelete = async () => {
        try {
            const {data} = await deleteCandidate(this?.selectedCandidate?.id);
            toast.success(i18n.t("toast.delete_success"));

            this.handleClose();
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
            throw new Error(error);
        }
    };
    handleConfirmDeleteNoPaging = async () => {
        try {
            const {data} = await deleteCandidate(this?.selectedCandidate?.id);
            toast.success(i18n.t("toast.delete_success"));

            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
            throw new Error(error);
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listOnDelete?.length; i++) {
                deleteData.push(this?.listOnDelete[i]?.id);
            }

            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));
            this.listOnDelete = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveCandidate = async (candidate) => {
        console.log(candidate)
        try {
            candidate.displayName = candidate?.lastName + (candidate?.lastName ? " " : "") + candidate?.firstName;

            if (candidate && candidate?.file != null) {
                const formData = new FormData();
                formData.append("uploadfile", candidate?.file);
                const response = await uploadImage(formData);

                // console.log("response after save staff: ", response);

                candidate.imagePath = response?.data?.name;
            }

            const {data} = await saveCandidate(candidate);
            toast.success("Thông tin ứng viên đã được lưu");

            return data;
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã ứng viên đã được sử dụng, vui lòng sử dụng mã ứng viên khác", {
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

    getSelectedIds = () => {
        const ids = [];
        this?.listOnDelete?.forEach(function (candidate) {
            ids.push(candidate?.id);
        });

        return ids;
    };

    handleRemoveActionItem = (onRemoveId) => {
        this.listOnDelete = this?.listOnDelete?.filter((item) => item?.id !== onRemoveId);
    };

    handleOpenApprovePopup = () => {
        this.openApprovePopup = true; // Popup "Đã duyệt"
    };

    handleOpenRejectPopup = () => {
        this.openRejectPopup = true; // Popup "Đã từ chối"
    };
    handleOpenChooseTemplatePopup = () => {
        this.openChooseTemplatePopup = true; // Popup "Đã từ chối"
    };
    handleOpenScreenedPassPopup = () => {
        this.openScreenedPassPopup = true; // Popup "Đã sơ lọc"
    };
    handleOpenNotScreenedPopup = () => {
        this.openNotScreenedPopup = true; // Popup "Không qua sơ lọc"
    };
    handleOpenCreateInterviewsPopup = () => {
        this.openCreateInterviewsPopup = true; // Popup "Tạo lịch phỏng vấn"
    };

    handleOpenPopupNextRound = () => {
        this.openPopupNextRound = true;
    };

    handleSelectedRound = (round) => {
        this.selectedRound = round;
    };

    handleSelectedCandidate = (candidate) => {
        this.selectedCandidate = candidate;
    }

    handleConfirmApproveCandidate = async (formValues) => {
        const toastOptions = {
            autoClose: 4444,
        };

        try {
            if (this?.listOnDelete?.length <= 0) {
                toast.error("Không có ứng viên nào được chọn PHÊ DUYỆT");
                this.handleClose();
                return;
            }

            const payload = {
                candidateIds: this.getSelectedIds(),
                status: LocalConstants.CandidateStatus.APPROVED.value,
                interviewDate: formValues?.interviewDate,
            };

            const {data} = await updateStatus(payload);
            if (!data) throw new Error("");

            toast.success("Đã phê duyệt hồ sơ ứng viên!", toastOptions);

            const firstRecruitmentRoundPayload = {
                chosenRecordIds: this.getSelectedIds(),
                // ngày thực tế ứng viên được sắp xếp tham gia vòng tuyển dụng.
                // VD: Vòng thi diễn ra vào 31/12 nhưng 2/1 ứng viên mới vào vòng tuyển => actualTakePlaceDate = 2/1
                actualTakePlaceDate: formValues?.interviewDate,
                // vị trí ngồi dự thi/phỏng vấn
                examPosition: formValues?.examPosition,
            };

            const {data: firstRecruitmentRoundResponse} = await distributeCandidatesForFirstRecruitmentRound(
                firstRecruitmentRoundPayload
            );
            if (!firstRecruitmentRoundResponse) throw new Error("");

            toast.success("Đã phân bổ lịch thi tuyển vòng đầu tiên cho ứng viên!", toastOptions);

            await this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmRejectCandidate = async (formValues) => {
        try {
            if (this?.listOnDelete?.length <= 0) {
                toast.error("Không có ứng viên nào được chọn TỪ CHỐI");
                this.handleClose();
                return;
            }

            const payload = {
                candidateIds: this.getSelectedIds(),
                status: LocalConstants.CandidateStatus.REJECTED.value,
                refusalReason: formValues?.refusalReason,
            };

            const {data} = await updateStatus(payload);
            if (!data) throw new Error("");

            toast.success("Cập nhật trạng thái cho Hồ sơ ứng viên thành công!");

            await this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };
    handleConfirmScreenedPassCandidate = async (values) => {
        try {
            if (this?.listOnDelete?.length <= 0) {
                toast.error("Không có ứng viên nào được chọn XÁC NHÂN QUA SƠ LỌC");
                this.handleClose();
                return;
            }

            const payload = {
                candidateIds: this.getSelectedIds(),
                status: LocalConstants.CandidateStatus.SCREENED_PASS.value,
            };
            const {data} = await updateStatus(payload);
            if (!data) throw new Error("");

            toast.success("Cập nhật trạng thái cho Hồ sơ ứng viên thành công!");

            await this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmNotScreenedCandidate = async (formValues) => {
        try {
            if (this?.listOnDelete?.length <= 0) {
                toast.error("Không có ứng viên nào được chọn KHÔNG QUA SƠ LỌC");
                this.handleClose();
                return;
            }

            const payload = {
                candidateIds: this.getSelectedIds(),
                status: LocalConstants.CandidateStatus.NOT_SCREENED.value,
                refusalReason: formValues?.refusalReason,
            };

            const {data} = await updateStatus(payload);
            if (!data) throw new Error("");

            toast.success("Cập nhật trạng thái cho Hồ sơ ứng viên thành công!");

            await this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleOpenResetApprovalStatus = () => {
        this.openResetApprovalStatus = true;
    };

    handleConfirmResetApprovalStatus = async () => {
        try {
            if (this?.listOnDelete?.length <= 0) {
                toast.error("Không có ứng viên nào được chọn đặt lại trạng thái phê duyệt");
                this.handleClose();
                return;
            }

            const payload = {
                candidateIds: this.getSelectedIds(),
                status: LocalConstants.CandidateStatus.NOT_APPROVED_YET.value,
            };

            const {data} = await updateStatus(payload);
            if (!data) throw new Error("");

            toast.success("Cập nhật trạng thái cho Hồ sơ ứng viên thành công!");

            await this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    getApprovalStatus = (status) => {
        if (!status || status == 1) return "Chưa phê duyệt";
        if (status == 2) return "Đã phê duyệt";
        if (status == 3) return "Đã từ chối";
        return "";
    };

    duplicateResponse = null;

    // check duplicate current candidate with any old candidate or old staff
    checkDuplicateCandidate = async (candidate) => {
        this.duplicateResponse = null;

        try {
            toast.info("Thông tin ứng viên đang được kiểm tra trùng lặp");

            candidate.displayName =
                candidate?.lastName?.trim() + (candidate?.lastName?.trim() ? " " : "") + candidate?.firstName?.trim();

            const {data} = await checkDuplicateCandidate(candidate);

            // const data = {
            //   status: false,
            //   listCandidate: [],
            //   listStaff: [],
            // };

            this.duplicateResponse = data;

            if (data?.status) {
                this?.displayDuplicateRecordMessage();

                this.handleOpenListDuplicate();
            } else {
                toast.success("Không phát hiện bản ghi ứng viên hay nhân viên trùng lặp");
            }

            return data;
        } catch (error) {
            console.error(error);
            // if (error.response.status == 409) {
            //   toast.error("Mã ứng viên đã được sử dụng, vui lòng sử dụng mã ứng viên khác", {
            //     autoClose: 5000,
            //     draggable: false,
            //     limit: 5,
            //   });
            // }
            // else {
            toast.error(i18n.t("toast.error"));
            // }

            return null;
        }
    };

    displayDuplicateRecordMessage = () => {
        const data = this?.duplicateResponse;

        const dupCandidateNumber = data?.listCandidate?.length;
        const dupStaffNumber = data?.listStaff?.length;

        let displayMessage = "Phát hiện ";

        // if (dupCandidateNumber > 0) {
        //   displayMessage += ` ${dupCandidateNumber} ứng viên có thông tin trùng lặp`;
        // }

        if (dupStaffNumber > 0) {
            // if (dupCandidateNumber > 0) {
            //   displayMessage += ", ";
            // }

            displayMessage += ` ${dupStaffNumber} nhân viên có thông tin trùng lặp`;
        }

        toast.warning(displayMessage, {
            autoClose: 5000,
            draggable: false,
            limit: 5,
        });
    };

    openListDuplicate = false;

    handleOpenListDuplicate = () => {
        this.openListDuplicate = true;
    };

    handleCloseListDuplicate = () => {
        this.openListDuplicate = false;
    };

    candidateProfilesOfStaff = [];

    getExistCandidateProfileOfStaff = async (staffId) => {
        try {
            const {data} = await getExistCandidateProfileOfStaff(staffId);

            this.candidateProfilesOfStaff = data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmInterview = async (candidateId) => {
        try {
        } catch (error) {
        }
    };

    setIsOpenFilter = (value) => {
        this.isOpenFilter = value;
    };

    toggleFilterPopup = (value) => {
        this.isOpenFilter = !this.isOpenFilter;
    };

    uploadFileExcel = async (event) => {
        const fileInput = event.target; // Lưu lại trước
        const file = fileInput.files[0];
        let message = "Nhập excel thất bại";

        try {
            const res = await importCandidate(file);

            // 🔽 Create and save the Excel result file
            const blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });

            let filename = "KetQuaNhapDuLieuUngVien.xlsx";
            const contentDisposition = res.headers["content-disposition"];
            if (contentDisposition) {
                const match = contentDisposition.match(/filename="?([^"]+)"?/);
                if (match && match[1]) {
                    filename = decodeURIComponent(match[1]);
                }
            }

            saveAs(blob, filename);

            toast.success("Nhập excel thành công");
            await this.pagingCandidates();
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
            await this.handleClose();
            fileInput.value = null;
        }
    };


    handleDownloadCandidateTemplate = async () => {
        try {
            const res = await downloadCandidateTemplate();
            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });
            saveAs(blob, "Mẫu import hồ sơ ứng viên.xlsx");
            toast.success("Đã tải mẫu import hồ sơ ứng viên thành công");
        } catch (error) {
            toast.error("Tải mẫu import kế hoạch tuyển dụng thất bại");
            console.error(error);
        }
    };

    handleSendMail = async (value) => {
        const payload = {
            ...value,
            templateId: value?.template?.id,
        }
        try {
            const res = await sendMailEdit(payload);
            if (res.status === HttpStatus.OK) {
                if (res.data.status === HttpStatus.OK) {
                    toast.success("Gửi mail thành công")
                } else {
                    toast.error(res.data.message);
                }
            } else {
                toast.error(i18n.t("toast.error"));
            }
        } catch (e) {
            toast.error(i18n.t("toast.error"));
        }

    }

    approveCV = async (value) => {
        const res = await approveCV(value);
        if (res?.status === HttpStatus.OK) {
            if (res.data.status === HttpStatus.OK) {
                toast.success(res.data.message)
            } else {
                toast.error(res.data.message)
            }
        } else {
            toast.error(i18n.t("toast.error"));
        }
    }

    setOpenPopupExportExcelRecruitmentReports = (value) => {
        this.openPopupExportExcelRecruitmentReports = value;
    }

    handleExportExcelRecruitmentReports = async (searchObj) => {
        try {
            const blob = await exportExcelRecruitmentReports(searchObj);

            const file = new Blob([blob], {
                type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            });

            saveAs(file, 'Báo_cáo_tuyển_dụng.xlsx');
        } catch (error) {
            console.error('Download failed:', error);
            toast.warning(i18n.t('toast.error'));
        }
    };

    autoGenCode = async (configKey) => {
        const response = await autoGenCode(configKey)
        if (response.status === HttpStatus.OK) {
            return response.data;
        }
    }
    getPreviewMail = async (candidateId, templateId) => {
        const response = await getPreviewMail({candidateId, templateId})
        if (response.status === HttpStatus.OK) {
            if (response.data.status === HttpStatus.OK)
                return response.data.data;
            else {
                toast.error(response.data.message)
                return null;
            }
        } else{
            toast.error(i18n.t('toast.error'));
            return null;
        }
    }

}
