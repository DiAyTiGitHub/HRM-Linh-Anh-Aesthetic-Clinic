import { makeAutoObservable } from "mobx";
import {
    pagingStaffSalaryTemplate,
    getStaffSalaryTemplateById,
    saveStaffSalaryTemplate,
    deleteStaffSalaryTemplate,
    deleteMultiple,
    downloadTemplateStaffSalaryTemplate,
    importFileStaffSalaryTemplate,
    findStaffTemplateIdByStaffIdAndTemplateId
} from "./StaffSalaryTemplateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchStaffSalaryTemplate } from "app/common/Model/SearchObject/SearchStaffSalaryTemplate";
import { saveAs } from "file-saver";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffSalaryTemplateStore {
    intactSearchObject = {
        ... new SearchStaffSalaryTemplate(),
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    initialStaffSalaryTemplate = {
        id: null,
        staff: null,
        salaryTemplate: null,
        fromDate: null,
        toDate: null,
        displayValueEqualZero: false,
    };

    staffSalaryTemplateList = [];
    totalElements = 0;
    totalPages = 0;

    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openCreateEditPopup = false;
    listOnDelete = [];

    selectedStaffSalaryTemplate = null;
    selectedStaffSalaryTemplateList = [];
    openViewPopup = false;
    openPopupDownloadTemplate = false;

    handleOpenView = async (staffSalaryTemplateId) => {
        try {
            if (staffSalaryTemplateId) {
                const { data } = await getStaffSalaryTemplateById(staffSalaryTemplateId);
                this.selectedStaffSalaryTemplate = data;
            } else {
                this.selectedStaffSalaryTemplate = {
                    ... this.initialStaffSalaryTemplate,
                };
            }
            this.openViewPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleSetOpenViewPopup = (value) => {
        this.openViewPopup = value;
    }

    handleSetOpenCreateEditPopup = (value) => {
        this.openCreateEditPopup = value;
    }

    handleOpenPopupDownloadTemplate = () => {
        this.openPopupDownloadTemplate = true;
    }

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.staffSalaryTemplateList = [];
        this.openCreateEditPopup = false;
        this.selectedStaffSalaryTemplate = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openViewPopup = false;
        this.openPopupDownloadTemplate = false;
    };

    pagingStaffSalaryTemplate = async () => {
        try {
            const payload = {
                ... this.searchObject
            };
            const data = await pagingStaffSalaryTemplate(payload);
            this.staffSalaryTemplateList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    findStaffTemplateIdByStaffIdAndTemplateId = async (payload) => {
        try {
            const { data } = await findStaffTemplateIdByStaffIdAndTemplateId(payload);

            return data;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingStaffSalaryTemplate();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingStaffSalaryTemplate();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleOpenCreateEdit = async (staffSalaryTemplateId) => {
        try {
            if (staffSalaryTemplateId) {
                const { data } = await getStaffSalaryTemplateById(staffSalaryTemplateId);
                this.selectedStaffSalaryTemplate = data;
            } else {
                this.selectedStaffSalaryTemplate = {
                    ... this.initialStaffSalaryTemplate,
                };
            }
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openViewPopup = false;
        this.openPopupDownloadTemplate = false;
    };

    handleDelete = (staffSalaryTemplate) => {
        this.selectedStaffSalaryTemplate = { ...staffSalaryTemplate };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteStaffSalaryTemplate(this.selectedStaffSalaryTemplate.id);
            toast.success(i18n.t("toast.delete_success"));
            await this.pagingStaffSalaryTemplate();
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listOnDelete?.length; i++) {
                deleteData.push(this?.listOnDelete[i]?.id);
            }
            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffSalaryTemplate();
            this.listOnDelete = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleSelectListDelete = (staffSalaryTemplates) => {
        this.listOnDelete = staffSalaryTemplates;
    };

    saveStaffSalaryTemplate = async (staffSalaryTemplate) => {
        try {
            const { data } = await saveStaffSalaryTemplate(staffSalaryTemplate);
            this.selectedStaffSalaryTemplate = data

        } catch (error) {
            toast.error(i18n.t("toast.error"));
        }
    };

    getStaffSalaryTemplate = async (id) => {
        if (id != null) {
            try {
                const { data } = await getStaffSalaryTemplateById(id);
                this.selectedStaffSalaryTemplate = data;
                this.openCreateEditPopup = true;
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            // this.handleSelectStaffSalaryTemplate(null);
        }
    };

    handleSetSearchObject = (so) => {
        if (so.staff == null) {
            so.staffId = null;
        } else {
            so.staffId = so.staff.id;
        }

        if (so.salaryTemplate == null) {
            so.salaryTemplateId = null;
        } else {
            so.salaryTemplateId = so.salaryTemplate.id;
        }


        this.searchObject = { ...so };
    };

    // handleSelectListDelete = (deleteStaffSalaryTemplate) => {
    //   this.listOnDelete = deleteStaffSalaryTemplate;
    // };

    setOpenCreateEditPopup = (value) => {
        this.openCreateEditPopup = value;
    };

    setSelectedStaffSalaryTemplate = (data) => {
        this.selectedStaffSalaryTemplate = { ... this.initial, ...data };
    }

    setOpenPopupDownloadTemplate = (value) => {
        //console.log("openPopupDownloadTemplate", value);
        this.openPopupDownloadTemplate = value;
    }

    downloadTemplate = async (salaryTemplateId) => {
        try {
            const blob = await downloadTemplateStaffSalaryTemplate(salaryTemplateId);

            const file = new Blob([blob], {
                type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            });

            saveAs(file, 'Mẫu_nhập_bảng_lương_nhân_viên.xlsx');
        } catch (error) {
            console.error('Download failed:', error);
            toast.warning(i18n.t('toast.error'));
        }
    };

    uploadFileStaffSalaryTemplate = async (event) => {
        const file = event.target.files[0];
        importFileStaffSalaryTemplate(file)
            .then(() => {
                toast.success("Nhập excel thành công");
                this.searchObject = {
                    ...this.searchObject,
                    pageIndex: 1,
                };
                this.pagingStaffSalaryTemplate();
            })
            .catch(() => {
                toast.error("Nhập excel thất bại");
            })
            .finally(() => {
                this.handleClose();
            });
        event.target.value = null;
    };
}
