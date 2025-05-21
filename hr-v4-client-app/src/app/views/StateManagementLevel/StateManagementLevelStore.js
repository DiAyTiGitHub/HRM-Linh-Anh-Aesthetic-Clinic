import {makeAutoObservable} from "mobx";
import {
    pagingStateManagementLevels,
    getStateManagementLevel,
    createStateManagementLevel,
    editStateManagementLevel,
    deleteStateManagementLevel,
    checkCode,
} from "./StateManagementLevelService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000, draggable: false, limit: 3,
});

export default class StateManagementLevelStore {
    intactSearchObject = {
        pageIndex: 1, pageSize: 10, keyword: "",
    };
    intactStateManagementLevel = {
        id: "", code: "", name: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    stateManagementLevelList = [];
    selectedStateManagementLevel = null;
    selectedStateManagementLevelList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetStateManagementLevelStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.stateManagementLevelList = [];
        this.selectedStateManagementLevel = null;
        this.selectedStateManagementLevelList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    };


    search = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ...this.searchObject
        };

        try {
            let data = await pagingStateManagementLevels(searchObject);
            this.stateManagementLevelList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
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

    handleEditStateManagementLevel = (id) => {
        this.getStateManagementLevel(id).then(() => {
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
        this.getStateManagementLevel(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteStateManagementLevel(this.selectedStateManagementLevel.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedStateManagementLevelList.length; i++) {
            try {
                await deleteStateManagementLevel(this.selectedStateManagementLevelList[i].id);
            } catch (error) {
                listAlert.push(this.selectedStateManagementLevelList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedStateManagementLevelList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getStateManagementLevel = async (id) => {
        if (id != null) {
            try {
                let data = await getStateManagementLevel(id);
                this.handleSelectStateManagementLevel(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectStateManagementLevel(null);
        }
    };

    handleSelectStateManagementLevel = (stateManagementLevel) => {
        this.selectedStateManagementLevel = stateManagementLevel;
    };

    handleSelectListStateManagementLevel = (stateManagementLevel) => {
        this.selectedStateManagementLevelList = stateManagementLevel;
    };

    createStateManagementLevel = async (stateManagementLevel) => {
        try {
            let response = await checkCode(stateManagementLevel.id, stateManagementLevel.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createStateManagementLevel(stateManagementLevel);
                toast.success("toast.add_success");
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editStateManagementLevel = async (stateManagementLevel) => {
        try {
            let response = await checkCode(stateManagementLevel.id, stateManagementLevel.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editStateManagementLevel(stateManagementLevel);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
