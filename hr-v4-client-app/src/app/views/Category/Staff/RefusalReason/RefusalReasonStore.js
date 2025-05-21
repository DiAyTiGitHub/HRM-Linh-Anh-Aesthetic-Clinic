import {makeAutoObservable} from "mobx";

import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {
    createRefusalReason,
    deleteMultipleRefusalReason,
    deleteRefusalReason,
    getByIdRefusalReason,
    pagingRefusalReason
} from "./RefusalReasonService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class RefusalReasonStore {
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
    shouldOpenConfirmationMultiple = false

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    updatePageData = (item) => {
        console.log("item", item);

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
            const data = await pagingRefusalReason(searchObject);
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
                const data = await getByIdRefusalReason(id);

                this.selectedExamCategory = data?.data;
            } catch (error) {
                console.error(error);
                toast.error(i18n.t("toast.error"));

            }
        } else {
            this.selectedExamCategory = null;
        }
    };

    saveOrUpdate = async (examCategory) => {
        try {
            await createRefusalReason(examCategory);
            if (examCategory?.id) {
                toast.success(i18n.t("toast.update_success"));
            } else {
                toast.success(i18n.t("toast.add_success"));
            }
            this.handleClose();
            this.pagingRecruitment()
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error(i18n.t("toast.duplicate_code"), {
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
            await deleteRefusalReason(id);
            toast.success(i18n.t("toast.delete_success"));
            this.pagingRecruitment()
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

        deleteMultipleRefusalReason(listId)
            .then(({data}) => {
                toast.success("Xóa thành công")
                this.listSelected = []
                this.pagingRecruitment()
                this.handleClose()
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
