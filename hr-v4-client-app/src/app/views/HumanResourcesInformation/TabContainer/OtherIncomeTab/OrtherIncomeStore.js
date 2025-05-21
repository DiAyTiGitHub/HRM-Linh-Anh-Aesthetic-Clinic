import {makeAutoObservable, runInAction} from "mobx";

import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import {
    deleteMultipleStaffRewardHistory,
    deleteStaffRewardHistoryById,
    getAllStaffRewardHistoryByStaff, getStaffRewardHistoryById, saveStaffRewardHistory, updateStaffRewardHistory
} from "./StaffRewardHistoryService";

export default class StaffRewardHistoryStore {
    staffRewardHistoryList = [];
    staffId = null;
    selectedStaffRewardHistory = null;
    selectedStaffRewardHistoryList = [];
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    initialStaffRewardHistory = {
        rewardDate: null,
        rewardType: null,
        staff: null,
        organization: null,
        department: null,
        file: null
    };

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    getAllStaffRewardHistoryByStaff = async () => {
        this.loadingInitial = true;
        try {
            const res = await getAllStaffRewardHistoryByStaff(this.staffId);
            runInAction(() => {
                this.staffRewardHistoryList = res?.data || [];
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu quá trình khen thưởng!");
            this.setLoadingInitial(false);
        }
    };

    handleEdit = async (id) => {
        if (id?.length > 0) {
            await this.getById(id)
        } else {
            this.selectedStaffRewardHistory = null;
        }
        this.shouldOpenEditorDialog = true;

    };
    setStaffId = (id) => {
        this.staffId = id;
    }
    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.getAllStaffRewardHistoryByStaff();
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
            const res = await deleteStaffRewardHistoryById(this.selectedStaffRewardHistory.id);
            if (res?.data) {
                toast.success("Đã xoá quá trình khen thưởng thành công!");
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
        for (let i = 0; i < this.selectedStaffRewardHistoryList.length; i++) {
            listId.push(this.selectedStaffRewardHistoryList[i]?.id)
        }
        try {
            await deleteMultipleStaffRewardHistory(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListStaffRewardHistory([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };
    handleSelectListStaffRewardHistory = (list) => {
        this.selectedStaffRewardHistoryList = list;
    };
    getById = async (id) => {
        if (id != null) {
            try {
                const data = await getStaffRewardHistoryById(id);
                this.handleSelect(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải quá trình khen thưởng!");
            }
        } else {
            this.handleSelect(null);
        }
    };
    handleSelect = (value) => {
        this.selectedStaffRewardHistory = value;
    };

    saveOrUpdateStaffRewardHistory = async (value) => {
        try {
            let res;
            if (value?.id) {
                res = await updateStaffRewardHistory(value, value?.id)
            } else {
                res = await saveStaffRewardHistory(value)
            }

            if (res?.status === 200) {
                toast.success(value?.id ? "Chỉnh sửa quá trình khen thưởng thành công!" : "Thêm mới quá trình khen thưởng thành công!");
                await this.getAllStaffRewardHistoryByStaff(); // Cập nhật danh sách
                this.handleClose(true); // Đóng form
            }
        } catch (error) {
            console.error(error);
            toast.error(value?.id ? "Có lỗi xảy ra khi chỉnh sửa quá trình khen thưởng!" : "Có lỗi xảy ra khi thêm mới quá trình khen thưởng!");
        }
    }
};