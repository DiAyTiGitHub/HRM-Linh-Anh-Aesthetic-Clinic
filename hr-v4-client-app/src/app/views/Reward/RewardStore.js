import {makeAutoObservable} from "mobx";
import {deleteList, deleteReward, getReward, pagingRewards, saveReward,} from "./RewardService";
import {toast} from "react-toastify";
import i18n from "i18n";
import history from "history.js";
import ConstantList from "../../appConfig";


export default class RewardStore {
    examCategoryList = [];
    roles = [];
    selectedExamCategory = {
        id: null,
        code: null,
        name: null,
        description: null,
    };
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    listSelected = [];
    shouldOpenConfirmationMultiple = false;

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    updatePageData = (item) => {
        if (item != null) {
            this.page = 1;
            this.keyword = item.keyword;
            this.pagingRecruitment();
        } else {
            this.pagingRecruitment();
        }
    };

    pagingRecruitment = async () => {
        this.loadingInitial = true;
        var searchObject = {};
        searchObject.keyword = this.keyword;
        searchObject.pageIndex = this.page;
        searchObject.pageSize = this.rowsPerPage;

        try {
            const data = await pagingRewards(searchObject);
            this.examCategoryList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu");
            this.setLoadingInitial(false);
        }
    };

    setPage = (page) => {
        this.page = page;
        this.updatePageData();
    };

    setRowsPerPage = (event) => {
        this.rowsPerPage = event.target.value;
        this.page = 1;
        this.updatePageData();
    };

    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };

    handleOpenForm = async (id) => {
        if (!!id) {
            this.getExamCategory(id).then(() => {
                this.shouldOpenEditorDialog = true;
            });
        } else {
            this.shouldOpenEditorDialog = true;
        }
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.selectedExamCategory = {
            id: null,
            code: null,
            name: null,
            description: null,
        };
        this.shouldOpenConfirmationMultiple = false;
    };

    handleDelete = (id) => {
        this.getExamCategory(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleConfirmDelete = () => {
        this.deleteExamCategory(this.selectedExamCategory.id);
    };

    getExamCategory = async (id) => {
        try {
            const urlParams = new URLSearchParams(window.location.search);
            const idReward = urlParams.get("id") || id;
            const data = await getReward(idReward);

            this.selectedExamCategory = data?.data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveOrUpdate = async (examCategory) => {
        try {
            await saveReward(examCategory);
            if (examCategory?.id) {
                toast.success(i18n.t("toast.update_success"));
            } else {
                toast.success(i18n.t("toast.add_success"));
            }
            this.handleClose();
            // this.pagingRecruitment();
            history.push(
                ConstantList.ROOT_PATH + "category/staff/reward"
            );
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã loại khen thưởng đã được sử dụng, vui lòng sử dụng mã khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };

    deleteExamCategory = async (id) => {
        try {
            await deleteReward(id);
            toast.success(i18n.t("toast.delete_success"));
            this.pagingRecruitment();
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };
    // selected list examCategory
    handleSelectedUser = (list) => {
        this.listSelected = list;
    };
    handleClearExamCategory = () => {
        this.listSelected = [];
    };

    handleOpenConfirm = () => {
        this.shouldOpenConfirmationMultiple = true;
    };

    handleConfirmDeleteMultiple = () => {
        const listId = this.listSelected.map((item) => item?.id);

        deleteList(listId).then(({data}) => {
            toast.success("Xóa thành công");
            this.listSelected = [];
            this.pagingRecruitment();
            this.handleClose();
        });
    };

    resetForm = () => {
        this.selectedExamCategory = {
            id: null,
            code: null,
            name: null,
            description: null,
        };
    };
}
