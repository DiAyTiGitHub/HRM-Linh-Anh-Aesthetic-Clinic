import {makeAutoObservable, runInAction} from "mobx";

import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import {
    deleteStaffSalaryHistoryById,
    getAllStaffSalaryHistoryByStaffId,
    getStaffSalaryHistoryById,
    saveOrUpdateStaffSalaryHistory
} from "./StaffSalaryHistoryService";

export default class StaffSalaryHistoryStore {
    staffSalaryHistoryList = [];
    staffId = null;
    selectedStaffSalaryHistory = null;
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

    getAllStaffSalaryHistoryByStaff = async () => {
        this.loadingInitial = true;
        try {
            const res = await getAllStaffSalaryHistoryByStaffId(this.staffId);
            runInAction(() => {
                this.staffSalaryHistoryList = res?.data || [];
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu quá trình lương!");
            this.setLoadingInitial(false);
        }
    };

    handleEdit = async (id) => {
        if (id) {
            await this.getById(id)
        } else {
            this.selectedStaffSalaryHistory = null;
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
        if (state) this.getAllStaffSalaryHistoryByStaff();
    };

    handleDelete = (id) => {
        this.getById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleConfirmDelete = async () => {
        try {
            const res = await deleteStaffSalaryHistoryById(this.selectedPersonCertificate.id);
            if (res?.data) {
                toast.success("Đã xoá quá trình lương thành công!");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };
    getById = async (id) => {
        if (id != null) {
            try {
                const data = await getStaffSalaryHistoryById(id);
                this.handleSelect(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải quá trình lương!");
            }
        } else {
            this.handleSelect(null);
        }
    };
    handleSelect = (value) => {
        this.selectedStaffSalaryHistory = value;
    };

    saveOrUpdateStaffSalaryHistory = async (value) => {
        try {
            const res = await saveOrUpdateStaffSalaryHistory(value);

            if (res?.status === 200) {
                toast.success(value?.id ? "Chỉnh sửa quá trình lương thành công!" : "Thêm mới quá trình lương thành công!");
                await this.getAllPersonCertificateByPerson(); // Cập nhật danh sách
                this.handleClose(true); // Đóng form
            }
        } catch (error) {
            console.error(error);
            toast.error(value?.id ? "Có lỗi xảy ra khi chỉnh sửa quá trình lương!" : "Có lỗi xảy ra khi thêm mới quá trình lương!");
        }
    }
};