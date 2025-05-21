import {makeAutoObservable, runInAction} from "mobx";
import {
    pagingBank,
    getBankById,
    deleteMultipleBank,
    saveOrUpdateBank,
    deleteBankById
} from "./BankService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";

export default class BankStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    bankList = [];
    selectedBank = null;
    selectedBankList = [];
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

    paging = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ...this.searchObject
        };

        try {
            const res = await pagingBank(searchObject);
            runInAction(() => {
                this.bankList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu của tài khoản ngân hàng!");
            this.setLoadingInitial(false);
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.paging();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.paging();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleEdit = (id) => {
        this.getById(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.paging();
    };

    handleDelete = (id) => {
        this.getById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };
    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };
    handleConfirmDelete = async () => {
        try {
            const res = await deleteBankById(this.selectedBank.id);
            if (res?.data) {
                toast.success("Đã xoá ngân hàng thành công!");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            await this.paging();
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };
    handleConfirmDeleteList = async () => {
        let listId = [];
        for (let i = 0; i < this.selectedBankList.length; i++) {
            listId.push(this.selectedBankList[i]?.id)
        }
        try {
            await deleteMultipleBank(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListBank([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };
    handleSelectListBank = (banks) => {
        this.selectedBankList = banks;
    };
    getById = async (id) => {
        if (id != null) {
            try {
                const data = await getBankById(id);
                this.handleSelect(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin ngân hàng!");
            }
        } else {
            this.handleSelect(null);
        }
    };
    handleSelect = (values) => {
        this.selectedBank = values;
    };

    saveOrUpdate = async (value) => {
        try {
            const res = await saveOrUpdateBank(value); // Gọi API lưu ngân hàng

            if (res?.status === 200) {
                toast.success(
                    value?.id ? "Chỉnh sửa ngân hàng thành công!" : "Thêm mới ngân hàng thành công!"
                );
                await this.paging(); // Cập nhật danh sách
                this.handleClose(true); // Đóng form
                return res?.data;
            }
        } catch (error) {
            console.error(error);

            if (error?.response?.status === 409) {
                toast.warning("Mã ngân hàng đã tồn tại! Vui lòng nhập mã khác.");
            } else {
                toast.warning(
                    value?.id
                        ? "Có lỗi xảy ra khi chỉnh sửa ngân hàng!"
                        : "Có lỗi xảy ra khi thêm mới ngân hàng!"
                );
            }
        }
    };


    resetBankStore = () => {
        this.bankList = [];
        this.selectedBank = null;
        this.selectedBankList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    };
}
