import { makeAutoObservable } from "mobx";
import {
    pagingAbsenceRequest,
    getById,
    saveAbsenceRequest,
    deleteMultiple,
    deleteAbsenceRequest,
    updateApprovalStatus
} from "./AbsenceRequestService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { AbsenceRequest } from "./AbsenceRequest";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class AbsenceRequestStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        approvalStatus: 0,
        chosenIds: []
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listAbsenceRequests = [];
    openCreateEditPopup = false;
    selectedAbsenceRequest = new AbsenceRequest();
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listChosen = [];
    // handle for update recruitment request status
    openConfirmUpdateStatusPopup = false;
    onUpdateStatus = null;

    selectDepartment = null;
    openDepartmentPopup = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listAbsenceRequests = [];
        this.openCreateEditPopup = false;
        this.selectedAbsenceRequest = new AbsenceRequest();
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listChosen = [];
        this.openConfirmUpdateStatusPopup = false;
        this.openDepartmentPopup = false;
        this.onUpdateStatus = null;
    }

    handleChangePagingStatus = (approvalStatus) => {
        const so = { ...this.searchObject, approvalStatus: approvalStatus };
        this.searchObject = so;
    }

    handleSetSearchObject = (searchObject) => {
        // if (searchObject.department == null) {
        //     searchObject.departmentId = null;
        // } else {
        //     searchObject.departmentId = searchObject.department.id;
        // }
        this.searchObject = { ...searchObject };
    }

    mapTabToStatus = (tab) => {
        // tab 0 => Tất cả
        if (tab == 0) return null;
        // tab 1 => Chưa phê duyệt
        if (tab == 1) return LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.value;
        // tab 2 => Đã Phê duyệt
        if (tab == 2) return LocalConstants.AbsenceRequestApprovalStatus.APPROVED.value;
        // tab 3 => Đã từ chối
        if (tab == 3) return LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED.value;

        return null;
    }

    pagingAbsenceRequest = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
                approvalStatus: this.mapTabToStatus(this.searchObject.approvalStatus),
            };
            const data = await pagingAbsenceRequest(payload);

            this.listAbsenceRequests = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingAbsenceRequest();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingAbsenceRequest();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteAbsenceRequests) => {
        this.listChosen = deleteAbsenceRequests;
    };

    getById = async (absenceRequestId) => {
        try {
            if (!absenceRequestId) {
                this.selectedAbsenceRequest = new AbsenceRequest();
                return;
            }
            const { data } = await getById(absenceRequestId);
            this.selectedAbsenceRequest = data;
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
        this.listChosen = [];
    };


    handleDelete = (position) => {
        this.selectedAbsenceRequest = { ...position };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (absenceRequest) => {
        try {
            if (absenceRequest) {
                const { data } = await getById(absenceRequest?.id);
                this.selectedAbsenceRequest = data;
            } else {
                this.selectedAbsenceRequest = {
                    ...new AbsenceRequest()
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
            const { data } = await deleteAbsenceRequest(this?.selectedAbsenceRequest?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingAbsenceRequest();

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

            await this.pagingAbsenceRequest();
            this.listChosen = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveAbsenceRequest = async (position) => {
        try {
            const { data } = await saveAbsenceRequest(position);
            toast.success("Thông tin Yêu cầu đã được lưu");
            this.handleClose();

            return data;
        } catch (error) {
            // console.error(error);
            // // toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
            // if (error.response.status == 409) {
            //     toast.error("Mã yêu cầu đã được sử dụng, vui lòng sử dụng mã yêu cầu khác", {
            //         autoClose: 5000,
            //         draggable: false,
            //         limit: 5,
            //     });
            // } else {
            //     toast.error(i18n.t("toast.error"));
            // }
            // throw new Error(i18n.t("toast.error"));
            // // return null;
            toast.error(i18n.t("toast.error"));
        }
    };

    handleRemoveActionItem = (onRemoveId) => {
        this.listChosen = this?.listChosen?.filter(item => item?.id !== onRemoveId);
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listChosen?.forEach(function (absenceRequest) {
            ids.push(absenceRequest?.id);
        });

        return ids;
    }

    handleOpenConfirmUpdateStatusPopup = (status) => {
        this.onUpdateStatus = status;
        this.openConfirmUpdateStatusPopup = true;
    }

    handleConfirmUpdateStatus = async () => {
        try {
            if (this?.listChosen?.length <= 0) {
                toast.error("Không có yêu cầu nào được chọn");
                return;
            }

            if (this.onUpdateStatus == null) {
                throw new Error("On update status in invalid");
            }
            const payload = {
                chosenIds: this.getSelectedIds(),
                approvalStatus: this.onUpdateStatus,
            };

            const { data } = await updateApprovalStatus(payload);
            if (!data) throw new Error("");

            toast.success("Cập nhật trạng thái thành công!");

            this.onUpdateStatus = null;

            this.handleClose();
            await this.pagingAbsenceRequest();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }

    handelOpenDepartmentPopup = (value) => {
        this.openDepartmentPopup = value;
    }
}
