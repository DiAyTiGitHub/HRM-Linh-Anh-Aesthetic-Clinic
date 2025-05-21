import { makeAutoObservable, runInAction } from "mobx";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import {
    deleteById,
    deleteMultiple,
    getById,
    pagingStaffInsuranceHistory,
    saveStaffInsuranceHistory
} from "./StaffInsuranceHistoryService";
import { StaffInsuranceHistory } from "app/common/Model/HumanResource/StaffInsuranceHistory";

export default class StaffInsuranceHistoryStore {
    intactSearchObject = {
        staffId: null,
        pageIndex: 1,
        pageSize: 10,
        fromDate: null,
        toDate: null,
        keyword: "",
    };

    staffInsuranceHistoryList = [];
    selectedInsuranceHistory = null;
    selectedInsuranceHistoryList = [];
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
        this.searchObject = { ...searchObject };
    };

    handlepagingStaffInsuranceHistory = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ... this.searchObject
        };
        try {
            const res = await pagingStaffInsuranceHistory(searchObject);
            runInAction(() => {
                this.staffInsuranceHistoryList = res?.data?.content || [];
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu quá trình đóng BHXH!");
            this.setLoadingInitial(false);
        }
    };

    handleEdit = async (id) => {
        if (id?.length > 0) {
            await this.getById(id)
        } else {
            const staff = this?.searchObject?.staff;

            this.selectedInsuranceHistory = {
                ... new StaffInsuranceHistory(),
                staffId: staff?.staffId,
                staff: staff
            };
        }
        this.shouldOpenEditorDialog = true;

    };
    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.handlepagingStaffInsuranceHistory();
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
            const res = await deleteById(this.selectedInsuranceHistory.id);
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
        for (let i = 0; i < this.selectedInsuranceHistoryList.length; i++) {
            listId.push(this.selectedInsuranceHistoryList[i]?.id)
        }
        try {
            await deleteMultiple(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListStaffLeave([]);
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };

    handleSelectListStaffLeave = (list) => {
        this.selectedInsuranceHistoryList = list;
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
        this.selectedInsuranceHistory = value;
    };

    saveStaffInsuranceHistory = async (value) => {
        try {
            const res = await saveStaffInsuranceHistory(value)
            if (res?.status === 200) {
                toast.success("Thông tin đã được cập nhật!");
                await this.handlepagingStaffInsuranceHistory(); // Cập nhật danh sách
                this.handleClose(true); // Đóng form
            }
        } catch (error) {
            console.error(error);
            toast.error("Lưu thông tin có lỗi, vui lòng thử lại sau");
        }
    }

};