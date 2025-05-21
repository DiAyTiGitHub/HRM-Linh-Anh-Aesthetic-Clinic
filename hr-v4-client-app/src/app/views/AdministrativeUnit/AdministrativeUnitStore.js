import {makeAutoObservable} from "mobx";
import {
    pagingAdministratives,
    getAdministrative,
    createAdministrative,
    editAdministrative,
    deleteAdministrative,
    checkCode,
    importAdministrativeUnit,
    downloadAdministrativeUnitTemplate, exportAdministrativeUnit
} from "./AdministrativeUnitService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import {saveAs} from "file-saver";
import i18n from "i18n";

toast.configure({
    autoClose: 2000, draggable: false, limit: 3,
});

export default class AdministrativeStore {
    administrativeUnitList = [];
    selectedAdministrativeUnit = null;
    selectedAdministrativeUnitList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenImportDialog = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
        level: null,
        parent: null,
        currentProvince: null,
        currentDistrict: null,
        currentWard: null,
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    intactAdministrative = {
        id: "", code: "", level: null, description: "", name: "", parent: null,
    };
    resetAdministrativeStore = () => {
        console.log("resetAdministrativeStore")
        this.administrativeUnitList = [];
        this.selectedAdministrativeUnit = null;
        this.selectedAdministrativeUnitList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.shouldOpenImportDialog = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    };

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    setShouldOpenImportDialog = (state) => {
        this.shouldOpenImportDialog = state;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    };

    search = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ...this.searchObject,
            parentId: this.searchObject.parent?.id,
            provinceId: this.searchObject.currentProvince?.id,
            districtId: this.searchObject.currentDistrict?.id,
            communeId: this.searchObject.currentWard?.id,
        };

        try {
            let data = await pagingAdministratives(searchObject);
            this.administrativeUnitList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning("toast.load_fail");
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

    handleEditAdministrative = (id) => {
        this.getAdministrative(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.shouldOpenImportDialog = false;
        this.search();
    };

    handleDelete = (id) => {
        this.getAdministrative(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            const res = await deleteAdministrative(this.selectedAdministrativeUnit.id);
            if (res?.data) {
                toast.success("Đã xoá bản ghi");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc");
            }
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau");
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedAdministrativeUnitList.length; i++) {
            try {
                await deleteAdministrative(this.selectedAdministrativeUnitList[i].id);
            } catch (error) {
                listAlert.push(this.selectedAdministrativeUnitList[i].name);
                toast.warning("toast.error");
            }
        }
        this.selectedAdministrativeUnitList = [];
        this.handleClose();
        toast.success("Đã xóa thành công!");
    };

    getAdministrative = async (id) => {
        if (id != null) {
            try {
                let data = await getAdministrative(id);
                this.handleSelectAdministrative(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("toast.get_fail");
            }
        } else {
            this.handleSelectAdministrative(null);
        }
    };

    handleSelectAdministrative = (administrative) => {
        this.selectedAdministrativeUnit = administrative;
    };

    handleSelectListAdministrative = (administratives) => {
        this.selectedAdministrativeUnitList = administratives;
    };

    createAdministrative = async (administrative) => {
        try {
            let response = await checkCode(administrative);
            if (response.data) {
                toast.warning("Mã đã được sử dụng!");
            } else {
                await createAdministrative(administrative);
                toast.success("Thêm mới thành công!");
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning("Có lỗi xảy ra khi thêm mới!");
        }
    };

    editAdministrative = async (administrative) => {
        console.log("administrative", administrative)
        try {
            let response = await checkCode(administrative);
            if (response.data) {
                toast.warning("Mã đã được sử dụng!");
            } else {
                await editAdministrative(administrative);
                toast.success("Cập nhật thành công!");
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning("Có lỗi xảy ra khi cập nhật!");
        }
    };
    resetSearchObject = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    };

    preHandleSubmitData = (values) => {
        return {
            ...values, code: values?.code?.trim(), name: values?.name?.trim(), description: values?.description?.trim(),
        };
    };


    uploadFileExcel = async (event) => {
        const file = event.target.files[0];
        importAdministrativeUnit(file).then(() => {
            toast.success("Nhập excel thành công")
            this.searchObject = {
                ...this.searchObject,
                pageIndex: 1
            }
            this.search();
        }).catch(() => {
            toast.error("Nhập excel thất bại")
        }).finally(() => {
            this.handleClose();
        })
        event.target.value = null;
    };

    handleDownloadAdministrativeUnitTemplate = async () => {
        try {
            const res = await downloadAdministrativeUnitTemplate();
            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });
            saveAs(blob, "Mẫu nhập dữ liệu đơn vị hành chính.xlsx");
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
        }
    };
    handleExportAdministrativeUnit = async () => {
        try {
            const searchObject = {
                ...this.searchObject,
                parentId: this.searchObject.parent?.id,
                provinceId: this.searchObject.currentProvince?.id,
                districtId: this.searchObject.currentDistrict?.id,
                communeId: this.searchObject.currentWard?.id,
            };
            const res = await exportAdministrativeUnit(searchObject);
            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });
            saveAs(blob, "Dữ liệu đơn vị hành chính.xlsx");
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
        }
    };

}
