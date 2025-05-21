import {makeAutoObservable} from "mobx";
import i18n from "i18n";
import {
    createPoliticaltheoryLevel,
    deletePoliticaltheoryLevel, editPoliticaltheoryLevel,
    getPoliticaltheoryLevel,
    pagingPoliticaltheoryLevels,
    checkCode
} from "./PoliticaltheoryLevelService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class PoliticalTheoryLevelStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactPoliticalTheory = {
        id: "",
        code: "",
        name: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    politicalTheoryLevelList = [];
    selectedPoliticalTheoryLevel = null;
    selectedPoliticalTheoryLevelList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
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
            let data = await pagingPoliticaltheoryLevels(searchObject);
            this.politicalTheoryLevelList = data.data.content;
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

    handleEditPoliticalTheoryLevel = (id) => {
        this.getPoliticalTheoryLevel(id).then(() => {
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
        this.getPoliticalTheoryLevel(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deletePoliticaltheoryLevel(this.selectedPoliticalTheoryLevel.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedPoliticalTheoryLevelList.length; i++) {
            try {
                await deletePoliticaltheoryLevel(
                    this.selectedPoliticalTheoryLevelList[i].id
                );
            } catch (error) {
                listAlert.push(this.selectedPoliticalTheoryLevelList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedPoliticalTheoryLevelList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getPoliticalTheoryLevel = async (id) => {
        if (id != null) {
            try {
                let data = await getPoliticaltheoryLevel(id);
                this.handleSelectPoliticalTheoryLevel(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectPoliticalTheoryLevel(null);
        }
    };

    handleSelectPoliticalTheoryLevel = (politicalTheoryLevel) => {
        this.selectedPoliticalTheoryLevel = politicalTheoryLevel;
    };

    handleSelectListPoliticalTheoryLevel = (politicalTheoryLevel) => {
        this.selectedPoliticalTheoryLevelList = politicalTheoryLevel;
    };

    createPoliticalTheoryLevel = async (politicalTheoryLevel) => {
        try {
            let response = await checkCode(
                politicalTheoryLevel.id,
                politicalTheoryLevel.code
            );
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createPoliticaltheoryLevel(politicalTheoryLevel);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editPoliticalTheoryLevel = async (politicalTheoryLevel) => {
        try {
            let response = await checkCode(
                politicalTheoryLevel.id,
                politicalTheoryLevel.code
            );
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editPoliticaltheoryLevel(politicalTheoryLevel);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
