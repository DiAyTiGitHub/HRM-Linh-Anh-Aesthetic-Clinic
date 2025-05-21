import {makeAutoObservable} from "mobx";
import {
    pagingRecruitment,
    getById,
    saveRecruitment,
    deleteMultiple,
    deleteRecruitment,
} from "./RecruitmentService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import {Recruitment} from "app/common/Model/Recruitment/Recruitment";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class RecruitmentStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        fromDate: null,
        toDate: null,
        departmentId: null,
        positionTitleId: null,
        recruitmentRequestId: null,
        recruitmentPlanId: null,
        department: null,
        recruitmentRequest: null,
        recruitmentPlan: null,
        organization: null,
        positionTitle: null
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listRecruitments = [];
    openCreateEditPopup = false;
    selectedRecruitment = new Recruitment();
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];

    tabIndex = 0;

    setTabIndex = index => {
        this.tabIndex = index;
    }

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listRecruitments = [];
        this.openCreateEditPopup = false;
        this.selectedRecruitment = new Recruitment();
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.tabIndex = 0;
        this.listOnDelete = [];
    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    }

    pagingRecruitment = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
            };
            const data = await pagingRecruitment(payload);

            this.listRecruitments = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingRecruitment();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingRecruitment();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteRecruitments) => {
        this.listOnDelete = deleteRecruitments;
    };

    getById = async (recruitmentId) => {
        try {
            if (!recruitmentId) {
                this.selectedRecruitment = new Recruitment();
                return;
            }

            const {data} = await getById(recruitmentId);
            this.selectedRecruitment = data;
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
    };

    handleDelete = (position) => {
        this.selectedRecruitment = {...position};
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (recruitmentId) => {
        try {
            if (recruitmentId) {
                const {data} = await getById(recruitmentId);
                this.selectedRecruitment = data;
            } else {
                this.selectedRecruitment = {
                    ...new Recruitment()
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
            const {data} = await deleteRecruitment(this?.selectedRecruitment?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingRecruitment();

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

            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingRecruitment();
            this.listOnDelete = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveRecruitment = async (position) => {
        try {
            const {data} = await saveRecruitment(position);
            toast.success("Thông tin Đợt tuyển dụng đã được lưu");
            this.handleClose();

            return data;
        } catch (error) {
            console.error(error);
            // toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");

            if (error?.response?.status == 409) {
                toast.error("Mã đợt tuyển đã được sử dụng, vui lòng sử dụng mã đợt tuyển khác", {
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

}
