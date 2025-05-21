import {makeAutoObservable} from "mobx";
import {
    checkCode,
    createSpeciality,
    deleteSpeciality,
    editSpeciality,
    getSpeciality,
    pagingSpecialities,
    autoGenCode
} from "./SpecialityService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {HttpStatus} from "../../LocalConstants";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class SpecialityStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactSpeciality = {
        id: "",
        code: "",
        name: "",
        nameEng: "",
        description: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    specialityList = [];
    selectedSpeciality = null;
    selectedSpecialityList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetSpecialityStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.specialityList = [];
        this.selectedSpeciality = null;
        this.selectedSpecialityList = [];
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
            let data = await pagingSpecialities(searchObject);
            this.specialityList = data.data.content;
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

    handleEditSpeciality = (id) => {
        this.getSpeciality(id).then(() => {
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
        this.getSpeciality(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteSpeciality(this.selectedSpeciality.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedSpecialityList.length; i++) {
            try {
                await deleteSpeciality(this.selectedSpecialityList[i].id);
            } catch (error) {
                listAlert.push(this.selectedSpecialityList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedSpecialityList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getSpeciality = async (id) => {
        if (id != null) {
            try {
                let data = await getSpeciality(id);
                this.handleSelectSpeciality(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectSpeciality(null);
        }
    };

    handleSelectSpeciality = (speciality) => {
        this.selectedSpeciality = speciality;
    };

    handleSelectListSpeciality = (specialitys) => {
        this.selectedSpecialityList = specialitys;
    };

    createSpeciality = async (speciality) => {
        try {
            let response = await checkCode(speciality.id, speciality.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createSpeciality(speciality);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editSpeciality = async (speciality) => {
        try {
            let response = await checkCode(speciality.id, speciality.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editSpeciality(speciality);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
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
