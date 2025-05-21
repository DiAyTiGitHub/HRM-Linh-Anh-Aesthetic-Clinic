import { makeAutoObservable } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "../../../../../../i18n";
import { saveAs } from "file-saver";
import {
    deleteEvaluationItem,
    getEvaluationItem,
    importExcelEvaluationItem,
    pagingEvaluationItems,
    saveEvaluationItem,
    downloadTemplateFileExcel,
    autoGenCode
} from "./EvaluationItemService";
import {HttpStatus} from "../../../../../LocalConstants";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class EvaluationItemStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactEvaluationItem = {
        id: "",
        code: "",
        name: "",
        nameEng: "",
        description: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    evaluationItemList = [];
    selectedEvaluationItem = null;
    selectedEvaluationItemList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetEvaluationItemStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.evaluationItemList = [];
        this.selectedEvaluationItemList = [];
        this.selectedEvaluationItem = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    };

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = { ...searchObject };
    };

    search = async () => {
        this.loadingInitial = true;
        try {
            let { data } = await pagingEvaluationItems(this.searchObject);
            console.log("data", data.data);
            this.evaluationItemList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
            this.setLoadingInitial(false);
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.search();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.search();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleEditEvaluationItem = (id) => {
        this.getEvaluationItem(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };
    downloadTemplateFileExcel = async () => {
        try {
            const res = await downloadTemplateFileExcel();
            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });
            saveAs(blob, "Mẫu nhập dữ liệu tiêu chí đánh giá.xlsx");
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
        }
    };
    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.search();
    };

    handleDelete = (id) => {
        this.getEvaluationItem(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteEvaluationItem(this.selectedEvaluationItem.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (let i = 0; i < this.selectedEvaluationItemList.length; i++) {
            try {
                await deleteEvaluationItem(this.selectedEvaluationItemList[i].id);
            } catch (error) {
                listAlert.push(this.selectedEvaluationItemList[i].name);
                console.error(error);
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedEvaluationItemList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    uploadFileExcel = async (event) => {
        const file = event.target.files[0];
        importExcelEvaluationItem(file)
            .then(() => {
                toast.success("Nhập excel thành công");
                this.searchObject = {
                    ...this.searchObject,
                    pageIndex: 1,
                };
                this.search();
            })
            .catch(() => {
                toast.error("Nhập excel thất bại");
            })
            .finally(() => {
                this.handleClose();
            });
        event.target.value = null;
    };

    getEvaluationItem = async (id) => {
        if (id != null) {
            try {
                let data = await getEvaluationItem(id);
                this.handleSelectEvaluationItem(data.data.data);
            } catch (error) {
                console.error(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectEvaluationItem(null);
        }
    };

    handleSelectEvaluationItem = (evaluationItem) => {
        console.log("evaluationItem", evaluationItem);
        this.selectedEvaluationItem = evaluationItem;
    };

    handleSelectListEvaluationItem = (evaluationItems) => {
        this.selectedEvaluationItemList = evaluationItems;
    };

    createEvaluationItem = async (evaluationItem) => {
        try {
            await saveEvaluationItem(evaluationItem);
            toast.success(i18n.t("toast.add_success"));
            this.handleClose();
            // let response = await checkCode(evaluationItem.id, evaluationItem.code);
            // if (response.data) {
            //     toast.warning(i18n.t("toast.duplicate_code"));
            // } else {
            //     await saveEvaluationItem(evaluationItem);
            //     toast.success(i18n.t("toast.add_success"));
            //     this.handleClose();
            // }
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editEvaluationItem = async (evaluationItem) => {
        try {
            await saveEvaluationItem(evaluationItem);
            toast.success(i18n.t("toast.update_success"));
            this.handleClose();
            // let response = await checkCode(evaluationItem.id, evaluationItem.code);
            // if (response.data) {
            //     toast.warning(i18n.t("toast.duplicate_code"));
            // } else {
            //     await saveEvaluationItem(evaluationItem);
            //     toast.success(i18n.t("toast.update_success"));
            //     this.handleClose();
            // }
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
    autoGenCode = async (configKey) =>{
        const response = await autoGenCode(configKey)
        if(response.status === HttpStatus.OK){
            return response.data;
        }
    }
}
