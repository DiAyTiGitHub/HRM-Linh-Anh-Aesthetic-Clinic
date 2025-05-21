import {makeAutoObservable} from "mobx";

import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {
    createRecruitment,
    deleteMultipleRecruitment,
    deleteRecruitment,
    getByIdRecruitment,
    pagingRecruitmentExamType,
    updateRecruitment
} from "./RecruitmentExamTypeService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class RecruitmentExamTypeStore {
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
    shouldOpenConfirmationMultiple = false;
    listSelected = [];

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
            this.pagingRecruitmentExamType();
        } else {
            this.pagingRecruitmentExamType();
        }
    };

    pagingRecruitmentExamType = async () => {
        this.loadingInitial = true;
        var searchObject = {};
        searchObject.keyword = this.keyword;
        searchObject.pageIndex = this.page;
        searchObject.pageSize = this.rowsPerPage;

        try {
            const data = await pagingRecruitmentExamType(searchObject);

            console.log("data:", data.data.content);


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
        }
        this.shouldOpenConfirmationMultiple = false;
        this.updatePageData();
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
        if (id != null) {
            try {
                // let data = await getExamCategory(id);
                const data = await getByIdRecruitment(id);

                this.selectedExamCategory = data?.data;
            } catch (error) {
                console.error(error);
                toast.error(i18n.t("toast.error"));

            }
        } else {
            this.selectedExamCategory = null;
        }
    };

    saveExamCategory = async (examCategory) => {
        try {
            await createRecruitment(examCategory);

            this.handleClose();
            toast.success(i18n.t("toast.add_success"));
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã loại kiểm tra đã được sử dụng, vui lòng sử dụng mã loại kiểm tra khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };


    updateExamCategory = async (examCategory) => {
        try {
            await updateRecruitment(examCategory, examCategory?.id);

            this.handleClose();
            toast.success(i18n.t("toast.update_success"));
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã loại kiểm tra đã được sử dụng, vui lòng sử dụng mã loại kiểm tra khác", {
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
            await deleteRecruitment(id);
            toast.success(i18n.t("toast.delete_success"));
            this.pagingRecruitmentExamType()
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };
    // selected list examCategory
    handleSelectedUser = (list) => {
        this.listSelected = list
    }
    handleClearExamCategory = () => {
        this.listSelected = []
    }

    handleOpenConfirm = () => {
        this.shouldOpenConfirmationMultiple = true;
    }

    handleConfirmDeleteMultiple = () => {
        const listId = this.listSelected.map(item => item?.id)

        deleteMultipleRecruitment(listId)
            .then(({data}) => {
                toast.success("Xóa thành công")
                this.listSelected = []
                this.pagingRecruitmentExamType()
            })
    }

    resetForm = () => {
        this.selectedExamCategory = {
            id: null,
            code: null,
            name: null,
            description: null,
        };
    }
}
