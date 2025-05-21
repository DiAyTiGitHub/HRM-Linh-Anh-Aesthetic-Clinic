import {
    deleteStaffSignature, generateUniqueSignatureCode,
    getStaffSignatureById,
    pagingStaffSignature,
    saveOrUpdateStaffSignature,
} from "app/services/StaffSignatureService";
import i18n from "i18n";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffSignatureStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        staffId: null,
        fromDate: null,
        toDate: null,
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    initialStaffSignature = {
        id: null,
        staff: null,
        signature: null,
        fromDate: null,
        toDate: null,
    };

    staffSignatureList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;

    openConfirmDeletePopup = false;
    openCreateEditPopup = false;
    listOnDelete = [];

    selectedStaffSignature = null;
    selectedStaffSignatureList = [];
    currentStaffId = null;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.staffSignatureList = [];
        this.openCreateEditPopup = false;
        this.selectedStaffSignature = null;
        this.openConfirmDeletePopup = false;
        this.listOnDelete = [];
    };
    setCurrentStaffId = (staffId) => {
        this.currentStaffId = staffId;
    };

    handleOpenCreateEdit = async (staffSignatureId) => {
        console.log(staffSignatureId);
        try {
            if (staffSignatureId) {
                const {data} = await getStaffSignatureById(staffSignatureId);
                this.selectedStaffSignature = data;
            } else {
                this.selectedStaffSignature = {
                    ...this.initialStaffSignature,
                };
                await this.generateUniqueSignatureCode()
            }
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    pagingStaffSignature = async () => {
        try {
            const payload = {...this.searchObject};
            if (this.currentStaffId) {
                payload.staffId = this.currentStaffId;
            }
            const data = await pagingStaffSignature(payload);
            this.staffSignatureList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingStaffSignature();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingStaffSignature();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;

        this.pagingStaffSignature();
    };

    handleDelete = (staffSignature) => {
        this.selectedStaffSignature = {...staffSignature};
        this.openConfirmDeletePopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteStaffSignature(this.selectedStaffSignature.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleSelectListDelete = (staffSignatures) => {
        this.listOnDelete = staffSignatures;
    };

    saveStaffSignature = async (staffSignature) => {
        saveOrUpdateStaffSignature(staffSignature)
            .then((data) => {
                // Kiểm tra nếu có lỗi từ server (nằm trong note)
                if (data?.data?.description?.startsWith("ERROR:")) {
                    toast.error(data?.data?.description); // Hiển thị lỗi từ server
                    return
                }
                toast.success("Thông tin chữ ký của nhân viên đã được lưu");
                this.handleClose();
                return data; // Trả về dữ liệu để chuỗi promise tiếp tục
            })
            .catch((err) => {
                console.error(err);
                toast.error(i18n.t("toast.error"));
                throw err; // Ném lại lỗi để thông báo lên cấp trên
            });
    };

    getStaffSignature = async (id) => {
        if (id != null) {
            try {
                const {data} = await getStaffSignatureById(id);
                this.selectedStaffSignature = data;
                this.openCreateEditPopup = true;
            } catch (error) {
                console.error(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.selectedStaffSignature = null;
        }
    };

    handleSetSearchObject = (searchObject) => {
        if (!searchObject.staff) {
            searchObject.staffId = null;
        } else {
            searchObject.staffId = searchObject.staff.id;
        }
        this.searchObject = {...searchObject};
    };

    setOpenCreateEditPopup = (value) => {
        this.openCreateEditPopup = value;
    };

    setSelectedStaffSignature = (data) => {
        this.selectedStaffSignature = {...this.initialStaffSignature, ...data};
    };

    generateUniqueSignatureCode = async () => {
        try {
            const {data} = await generateUniqueSignatureCode();
            this.selectedStaffSignature.code = data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }
}
