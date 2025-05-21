import { makeAutoObservable } from "mobx";
import {
    pagingSalaryTemplates,
    getById,
    saveSalaryTemplate,
    deleteMultiple,
    deleteSalaryTemplate,
    clonSalaryTemplate,
    exportSalaryTemplate,
    downloadTemplateExcel,
    importExcel
} from "./SalaryTemplateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchObject } from "app/common/Model/SearchObject/SearchObject";
import { SalaryTemplate } from "app/common/Model/Salary/SalaryTemplate";
import { chooseTemplateItems } from "../SalaryTemplateCU/SalaryTemplateItemService";
import moment from "moment";
import { SalaryItem } from "../../../common/Model/Salary/SalaryItem";


toast.configure({
    autoClose: 5000,
    draggable: false,
    limit: 5,
});

export default class SalaryTemplateStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 20,
        keyword: null
    }

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    totalElements = 0;
    totalPages = 0;
    listSalaryTemplates = [];
    selectedSalaryTemplate = new SalaryTemplate();
    selectedSalaryTemplateClon = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];

    openResetApprovalStatus = false;
    openApprovePopup = false;
    openRejectPopup = false;

    tabCU = 0;

    openPopupClon = false;
    shouldOpenCreateForm = false;
    openViewPopup = false;

    handleOpenView = async (salaryTemplateId) => {
        try {
            if (salaryTemplateId) {
                const { data } = await getById(salaryTemplateId);
                this.selectedSalaryTemplate = data;
            } else {
                this.selectedSalaryTemplate = {
                    ... new SalaryTemplate(),
                };
            }
            this.openViewPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setOpenPopupClon = () => {
        this.openPopupClon = true;
    }
    setShouldOpenCreateForm = (state) => {
        this.shouldOpenCreateForm = state;
    }


    setClosePopupClon = () => {
        this.openPopupClon = false;
        this.selectedSalaryTemplateClon = null;
    }

    handleOpenPopUpClon = (rowData) => {
        this.setOpenPopupClon(true);
        this.selectedSalaryTemplateClon = {
            ...rowData,
            code: `${rowData.code || ""}_COPY`,
            name: `${rowData.name || ""}_COPY`,
            description: `${rowData.description || ""}_COPY`,
        };
    }


    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listSalaryTemplates = [];
        this.selectedSalaryTemplate = new SalaryTemplate();
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.tabCU = 0;
        this.openViewPopup = false;
    }

    setTabCU = (tabCU) => {
        this.tabCU = tabCU;
    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = { ...searchObject };
    }

    pagingSalaryTemplates = async () => {
        try {
            const searchData = { ... this?.searchObject };

            const data = await pagingSalaryTemplates(searchData);

            this.listSalaryTemplates = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingSalaryTemplates();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingSalaryTemplates();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteSalaryTemplates) => {
        this.listOnDelete = deleteSalaryTemplates;
    };

    getById = async (salaryTemplateId) => {
        try {
            const { data } = await getById(salaryTemplateId);

            this.selectedSalaryTemplate = data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleClose = async () => {
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.shouldOpenCreateForm = false;

        this.listOnDelete = [];
        this.openViewPopup = false;
    };

    handleDelete = (salaryTemplate) => {
        this.selectedSalaryTemplate = { ...salaryTemplate };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleGetSalaryTemplateData = async (salaryTemplateId) => {
        try {
            if (salaryTemplateId) {
                const { data } = await getById(salaryTemplateId);
                this.selectedSalaryTemplate = data;
            } else {
                this.selectedSalaryTemplate = {
                    ... new SalaryTemplate(),
                };
            }

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteSalaryTemplate(this?.selectedSalaryTemplate?.id);
            toast.success(i18n.t("toast.delete_success"));

            this.handleClose();

            await this.pagingSalaryTemplates();
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
            // throw new Error(error);
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
            this.listOnDelete = [];

            this.handleClose();
            await this.pagingSalaryTemplates();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveSalaryTemplate = async (salaryTemplate) => {
        try {
            const { data } = await saveSalaryTemplate(salaryTemplate);
            toast.success("Thông tin mẫu bảng lương đã được lưu");

            return data;
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã mẫu bảng lương đã được sử dụng, vui lòng sử dụng mã mẫu bảng lương khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }

            return null;
        }
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listOnDelete?.forEach(function (salaryTemplate) {
            ids.push(salaryTemplate?.id);
        });

        return ids;
    }

    exportExcel = (values) => {
        exportSalaryTemplate({ ... this.searchObject, ...values })
            .then((res) => {
                const url = window.URL.createObjectURL(new Blob([res.data]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute(
                    "download",
                    "MAU_BANG_LUONG" +
                    moment(new Date()).format("DDMMYYYY-HHmm") +
                    ".xlsx"
                );
                document.body.appendChild(link);
                link.click();
                this.pagingSalaryTemplates()

            })
            .catch((error) => {
                toast.warning("Có lỗi xảy ra, vui lòng thử lại!");
            });
    };

    handleDownloadExcelTemplate = async () => {
        try {
            const response = await downloadTemplateExcel();

            if (response.status === 200) {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute("download", "MAU_BANG_LUONG.xlsx");
                document.body.appendChild(link);
                link.click();
                link.parentNode.removeChild(link);
            }
        } catch (error) {
            console.error("Error downloading template:", error);
        }
    }


    handleImportExcel = async (e) => {
        const input = e.target
        const file = e.target.files[0]
        let formData = new FormData();
        formData.append("file", file); //Lưu ý tên 'file' phải trùng với tham số bên Server side

        try {
            const res = await importExcel(formData)
            const url = window.URL.createObjectURL(res?.data);
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", "KET_QUA_NHAP_MAU_BANG_LUONG" + new Date().getTime() + ".xlsx");
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            this.pagingSalaryTemplates()

            toast.success("Nhập excel thành công")

        } catch (error) {
            toast.error("Nhập excel thất bại")
            console.error(error);
        } finally {
            input.value = '';
        }
    };


    // handle select multiple template items
    chosenItemIds = [];

    setChosenItemIds = ids => {
        this.chosenItemIds = ids;
    }

    handleChooseItem = (salaryItem) => {
        if (!salaryItem?.id) {
            return;
        }

        const data = JSON.parse(JSON.stringify(this.chosenItemIds));

        if (data.includes(salaryItem?.id)) {
            const existedInIndex = data.indexOf(salaryItem?.id);
            if (existedInIndex !== -1) {
                data.splice(existedInIndex, 1);
            }
        } else {
            data.push(salaryItem?.id);
        }

        this.setChosenItemIds(data);
    }

    // handle for choosing multiple salary item
    handleCompleteChooseItems = async payload => {
        try {
            const { data } = await chooseTemplateItems(payload);

            if (!data) throw new Error("Something errored on choosing");

            return data;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("Có lỗi xảy ra khi xác nhận chọn thành phần lương"));
        }
    }


    handleClonSalaryTemplate = async (salaryTemplate) => {
        try {
            const { data } = await clonSalaryTemplate(salaryTemplate);
            console.log('save');
            if (!data) throw new Error("Something errored on choosing");
            return data;
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã mẫu bảng lương đã được sử dụng, vui lòng sử dụng mã mẫu bảng lương khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };
}
