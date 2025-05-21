import { makeAutoObservable , runInAction } from "mobx";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import {
    deleteById ,
    deleteMultiple ,
    getById ,
    pagingStaffLeave ,
    saveStaffLeave
} from "./StaffLeaveService";
import { StaffLeave } from "app/common/Model/HumanResource/StaffLeave";

export default class StaffLeaveStore {
    intactSearchObject = {
        staffId:null ,
        pageIndex:1 ,
        pageSize:10 ,
        keyword:"" ,
    };

    staffLeaveList = [];
    selectedStaffLeave = null;
    selectedStaffLeaveList = [];
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };
    handleSetSearchObject = (searchObject) => {
        this.searchObject = {... searchObject};
    };

    handlePagingStaffLeave = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ... this.searchObject
        };
        try {
            const res = await pagingStaffLeave(searchObject);
            runInAction(() => {
                this.staffLeaveList = res?.data?.content || [];
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu lịch sử nghỉ việc!");
            this.setLoadingInitial(false);
        }
    };

    handleEdit = async (id) => {
        if (id?.length > 0) {
            await this.getById(id)
        } else {
            this.selectedStaffLeave = new StaffLeave();
        }
        this.shouldOpenEditorDialog = true;

    };
    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.handlePagingStaffLeave();
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
            const res = await deleteById(this.selectedStaffLeave.id);
            if (res?.data) {
                toast.success("Đã xoá thông tin nghỉ việc thành công!");
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
        for (let i = 0; i < this.selectedStaffLeaveList.length; i++) {
            listId.push(this.selectedStaffLeaveList[i]?.id)
        }
        try {
            await deleteMultiple(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListStaffLeave([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };

    handleSelectListStaffLeave = (list) => {
        this.selectedStaffLeaveList = list;
    };

    getById = async (id) => {
        if (id != null) {
            try {
                const data = await getById(id);
                this.handleSelect(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin nghỉ việc!");
            }
        } else {
            this.handleSelect(null);
        }
    };

    handleSelect = (value) => {
        this.selectedStaffLeave = value;
    };

    saveOrUpdateStaffLeave = async (value) => {
        try {
            const res = await saveStaffLeave(value)
            if (res?.status === 200) {
                toast.success(value?.id ? "Chỉnh sửa thông tin nghỉ việc thành công!" : "Thêm mới thông tin nghỉ việc thành công!");
                await this.handlePagingStaffLeave(); // Cập nhật danh sách
                this.handleClose(true); // Đóng form
            }
        } catch (error) {
            console.error(error);
            toast.error(value?.id ? "Có lỗi xảy ra khi chỉnh sửa thông tin nghỉ việc!" : "Có lỗi xảy ra khi thêm mới thông tin nghỉ việc kỷ luật!");
        }
    }

};