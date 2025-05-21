import {makeAutoObservable} from "mobx";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "../../../../../i18n";

import {deleted, getById, paging, save, saveEvaluationTemplate} from "./ContentTemplateService";
import {HttpStatus} from "../../../../LocalConstants";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class ContentTemplateStore {
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
    selectedContentTemplate = null;
    selectedContentTemplateList = [];
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
        this.selectedContentTemplateList = [];
        this.selectedContentTemplate = null;
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
        this.searchObject = {...searchObject};
    };

    search = async () => {
        this.loadingInitial = true;
        try {
            let data = await paging(this.searchObject);
            if (data.status === HttpStatus.OK) {
                this.evaluationItemList = data.data.content;
                console.log(this.evaluationItemList)
                this.totalElements = data.data.totalElements;
                this.totalPages = data.data.totalPages;
                this.setLoadingInitial(false);
            } else {
                toast.warning(i18n.t("toast.error"));
            }
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

    handleEditContentTemplate = (id) => {
        this.getContentTemplate(id).then(() => {
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
        this.getContentTemplate(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleted(this.selectedContentTemplate.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (let i = 0; i < this.selectedContentTemplateList.length; i++) {
            try {
                await deleted(this.selectedContentTemplateList[i].id);
            } catch (error) {
                listAlert.push(this.selectedContentTemplateList[i].name);
                console.error(error);
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedContentTemplateList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getContentTemplate = async (id) => {
        if (id != null) {
            try {
                let data = await getById(id);
                console.log(data)
                this.handleSelectEvaluationItem(data.data);
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
        this.selectedContentTemplate = evaluationItem;
    };

    handleSelectListContentTemplateList = (evaluationItems) => {
        this.selectedContentTemplateList = evaluationItems;
    };

    saveContentTemplate = async (contentTemplate) => {
        try {
            await save(contentTemplate);
            toast.success(i18n.t("toast.add_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editEvaluationItem = async (evaluationItem) => {
        try {
            await save(evaluationItem);
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
