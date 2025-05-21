import {makeAutoObservable} from "mobx";
import {
    checkCode,
    createCountry,
    deleteCountry,
    editCountry,
    getByCodeCountry,
    getCountry,
    pagingCountry,
    uploadFileExcelCountry
} from "./CountryService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000, draggable: false, limit: 3,
});

export default class CountryStore {
    intactSearchObject = {
        pageIndex: 1, pageSize: 10, keyword: "",
    };
    intactCountry = {
        id: "",
        code: "",
        name: "",
        description: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    countryList = [];
    selectedCountry = null;
    selectedCountryList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    shouldOpenImportExcelDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetCountryStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.countryList = [];
        this.selectedCountry = null;
        this.selectedCountryList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.shouldOpenImportExcelDialog = false;
    };

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
            let data = await pagingCountry(searchObject);
            this.countryList = data.data.content;
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
    handleEditCountry = (id) => {
        this.getCountry(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.shouldOpenImportExcelDialog = false;
        this.search();
    };

    handleDelete = (id) => {
        this.getCountry(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = () => {
        this.deleteCountry(this.selectedCountry.id);
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedCountryList.length; i++) {
            try {
                await deleteCountry(this.selectedCountryList[i].id);
            } catch (error) {
                listAlert.push(this.selectedCountryList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        this.selectedCountryList = [];
        toast.success(i18n.t("toast.delete_success"));
    };

    handleSelectCountry = (country) => {
        this.selectedCountry = country;
    };

    handleSelectListCountry = (countrys) => {
        this.selectedCountryList = countrys;
    };

    getCountry = async (id) => {
        if (id != null) {
            try {
                let data = await getCountry(id);
                this.handleSelectCountry(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectCountry(null);
        }
    };
    getByCodeCountry = async (code) => {
        if (code != null) {
            try {
                let response = await getByCodeCountry(code);
                this.selectedCountry = response.data;
            } catch (error) {
                console.log(error);
            }
        } else {
            this.selectedCountry = null;
        }
    };

    createCountry = async (country) => {
        try {
            let responseCheckCode = await checkCode(country);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createCountry(country);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editCountry = async (country) => {
        try {
            let responseCheckCode = await checkCode(country);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editCountry(country);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }

        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    deleteCountry = async (id) => {
        try {
            await deleteCountry(id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    uploadFileExcelCountry = async (event) => {
        const fileInput = event.target;
        const file = fileInput.files[0];
        fileInput.value = null;
        uploadFileExcelCountry(file).then((data) => {
            toast.success("Nhập excel thành công");
        }).then(() => {
            this.search();
        }).catch((err) => {
            toast.error("Nhập excel thất bại");
        }).finally(() => {
            this.handleClose();
        })
    };

    importExcel = () => {
        this.shouldOpenImportExcelDialog = true;
    };
}
