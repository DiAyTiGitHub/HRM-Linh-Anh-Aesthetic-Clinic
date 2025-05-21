import { makeAutoObservable } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "../../../../../../i18n";
import {
    deleteEvaluationItem,
    saveEvaluationItem,
} from "./../EvaluationItem/EvaluationItemService";
import {getEvaluationTemplate, paging, saveEvaluationTemplate} from "./EvaluationTemplateService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class EvaluationTemplateStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactEvaluationTemplate = {
        id: null,
        code: null,
        name: null,
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    evaluationItemList = [];
    selectedEvaluationTemplate = null;
    selectedEvaluationTemplateList = [];
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
        this.selectedEvaluationTemplateList = [];
        this.selectedEvaluationTemplate = null;
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
            let {data} = await paging(this.searchObject);
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

    handleEditEvaluationTemplate = (id) => {
        this.getEvaluationTemplate(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.search();
    };

    handleDelete = (id) => {
        this.getEvaluationTemplate(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteEvaluationItem(this.selectedEvaluationTemplate.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (let i = 0; i < this.selectedEvaluationTemplateList.length; i++) {
            try {
                await deleteEvaluationItem(this.selectedEvaluationTemplateList[i].id);
            } catch (error) {
                listAlert.push(this.selectedEvaluationTemplateList[i].name);
                console.error(error);
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedEvaluationTemplateList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getEvaluationTemplate = async (id) => {
        if (id != null) {
            try {
                let data = await getEvaluationTemplate(id);
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
        this.selectedEvaluationTemplate = evaluationItem;
    };

    handleSelectListEvaluationItem = (evaluationItems) => {
        this.selectedEvaluationTemplateList = evaluationItems;
    };

    createEvaluationTemplate = async (evaluationItem) => {
        try {
            await saveEvaluationTemplate(evaluationItem);
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
}
