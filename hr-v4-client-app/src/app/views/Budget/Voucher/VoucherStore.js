import {makeAutoObservable, runInAction} from "mobx";
import {
    pagingVoucher,
    getVoucherById,
    deleteVoucherById,
    saveVoucher, exportVoucher,
    deleteMultiple
} from "./VoucherService"; // Changed import source to VoucherService
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "../../../../i18n";
import FileSaver from "file-saver";

export default class VoucherStore {
    voucherList = [];
    selectedVoucher = null;
    selectedVoucherList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    voucherType = 1;
    searchObject = {
        pageIndex: this.page,
        pageSize: this.rowsPerPage,
        keyword: this.keyword,
        fromDate: null,
        toDate: null,
        voucherType: null,
        budget: null,
    }

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };
    updatePageData = (item) => {
        if (item != null) {
            this.page = 1;
            this.keyword = item.keyword;
            this.search();
        } else {
            this.search();
        }
    };
    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    }
    search = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ...this.searchObject
        };

        try {
            const res = await pagingVoucher(searchObject);
            runInAction(() => {
                this.voucherList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu voucher!");
            this.setLoadingInitial(false);
        }
    };

    setPage = (page) => {
        this.page = page;
        this.updatePageData();
    };

    setRowsPerPage = (event) => {
        this.rowsPerPage = event.target.value;
        this.page = 1;
        this.updatePageData();
    };

    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };

    handleEditVoucher = (id) => { // Updated function name for Voucher
        this.getVoucherById(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleAddVoucher = (type) => {
        this.voucherType = type;
        this.shouldOpenEditorDialog = true;
    }

    handleClose = (updateListOnClose) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.selectedVoucher = null;
        if (updateListOnClose) this.updatePageData();
    };

    handleDelete = (id) => { // Updated function name for Voucher
        this.getVoucherById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            const res = await deleteVoucherById(this.selectedVoucher.id); // Updated function name for Voucher
            if (res?.data) {
                toast.success("Đã xoá voucher thành công!");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };

    handleConfirmDeleteList = async () => {
        let listId = [];
        for (let i = 0; i < this.selectedVoucherList.length; i++) { // Updated list name
            listId.push(this.selectedVoucherList[i]?.id)
        }
        try {
            await deleteMultiple(listId)
            toast.success("Đã xóa thành công!");
            this.handleClose(true);
            this.handleSelectListVoucher([])
        } catch (error) {
            this.handleClose();
            toast.warning("Không thể xóa một số voucher!");
        }
    };

    getVoucherById = async (id) => { // Updated function name for Voucher
        if (id != null) {
            try {
                const data = await getVoucherById(id); // Updated function name for Voucher
                this.handleSelectVoucher(data.data); // Updated function name for Voucher
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin voucher!");
            }
        } else {
            this.handleSelectVoucher(null); // Updated function name for Voucher
        }
    };

    handleSelectVoucher = (voucher) => { // Updated function name for Voucher
        this.selectedVoucher = voucher; // Updated variable name
    };

    handleSelectListVoucher = (vouchers) => { // Updated function name for Voucher
        this.selectedVoucherList = vouchers; // Updated variable name
        console.log(this.selectedVoucherList);
    };

    saveOrUpdateVoucher = async (voucher) => { // Updated function name for Voucher
        try {
            const res = await saveVoucher(voucher); // Updated function name for Voucher
            toast.success(
                voucher?.id ? "Chỉnh sửa voucher thành công!" : "Thêm mới voucher thành công!"
            );
            this.handleClose(true);
            return res?.data;
        } catch (error) {
            console.log(error);
            toast.warning("Có lỗi xảy ra khi thêm mới voucher!");
        }
    };
    handleExportExcelByFilter = async () => {
        try {
            const searchObject = {
                ...this.searchObject
            };
            const res = await exportVoucher(searchObject);

            if (res && res.data) {
                const blob = new Blob([res.data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});

                FileSaver.saveAs(blob, "HOA_DON_THU_CHI.xlsx");
            } else {
                toast.error("Không nhận được dữ liệu từ server.");
            }


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }

    resetVoucherStore = () => {
        this.voucherList = [];
        this.selectedVoucher = null;
        this.selectedVoucherList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.page = 1;
        this.rowsPerPage = 10;
        this.keyword = "";
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.voucherType = 1;
    };
}
