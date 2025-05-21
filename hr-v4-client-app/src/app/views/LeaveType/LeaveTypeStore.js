import { makeAutoObservable, runInAction } from "mobx";
import {
    deleteLeaveTypeById,
    deleteMultiple,
    getLeaveTypeById,
    pagingLeaveType,
    saveLeaveType,
} from "./LeaveTypeService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "../../../i18n";
import { LeaveType } from "app/common/Model/HumanResource/LeaveType";

export default class LeaveTypeStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        isPaid: null,
        usedForRequest: null, // Có được sử dụng trong yêu cầu n
        keyword: "",
    };


    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    leaveTypeList = [];
    selectedLeaveType = null;
    selectedListLeaveType = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    openViewPopup = false;

    handleOpenView = async (id) => {
        try {
            if (id != null) {
                try {
                    const data = await getLeaveTypeById(id);
                    this.handleSelect(data.data);
                } catch (error) {
                    console.log(error);
                    toast.warning("Không thể tải thông tin loại nghỉ phép!");
                }
            } else {
                this.handleSelect(new LeaveType());
            }
            this.openViewPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    constructor() {
        makeAutoObservable(this);
    }

    resetLeaveTypeStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.leaveTypeList = [];
        this.selectedLeaveType = null;
        this.selectedListLeaveType = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.openViewPopup = false;
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = { ...searchObject };
    };

    pagingLeaveType = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ... this.searchObject
        };

        try {
            const res = await pagingLeaveType(searchObject);
            runInAction(() => {
                this.leaveTypeList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu của loại nghỉ phép!");
            this.setLoadingInitial(false);
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingLeaveType();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingLeaveType();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleEdit = (id) => {
        console.log(id)
        this.getById(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.openViewPopup = false;
        if (state) this.pagingLeaveType();
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
            const res = await deleteLeaveTypeById(this.selectedLeaveType.id);
            if (res?.data) {
                toast.success("Đã xoá loại nghỉ phép thành công!");
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
        for (let i = 0; i < this.selectedListLeaveType.length; i++) {
            listId.push(this.selectedListLeaveType[i]?.id)
        }
        try {
            await deleteMultiple(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListLeaveType([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };
    handleSelectListLeaveType = (list) => {
        this.selectedListLeaveType = list;
    };
    getById = async (id) => {
        if (id != null) {
            try {
                const data = await getLeaveTypeById(id);
                this.handleSelect(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin loại nghỉ phép!");
            }
        } else {
            this.handleSelect(null);
        }
    };
    handleSelect = (values) => {
        this.selectedLeaveType = values;
    };

    saveOrUpdate = async (value) => {
        try {
            const res = await saveLeaveType(value);

            if (res?.status === 200) {
                toast.success(
                    value?.id ? "Chỉnh sửa loại nghỉ phép thành công!" : "Thêm mới loại nghỉ phép thành công!"
                );
                this.handleClose(true); // Đóng form
                return res?.data;
            }
        } catch (error) {
            console.error(error);

            if (error?.response?.status === 409) {
                toast.warning("Mã loại nghỉ phép đã tồn tại! Vui lòng nhập mã khác.");
            } else {
                toast.warning(
                    value?.id
                        ? "Có lỗi xảy ra khi chỉnh sửa loại nghỉ phép!"
                        : "Có lỗi xảy ra khi thêm mới loại nghỉ phép!"
                );
            }
        }
    };
}
