import {makeAutoObservable, runInAction} from "mobx";

import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import {
    deleteById,
    deleteMultiple,
    getById,
    pagingStaffDisciplineHistory,
    saveStaffDisciplineHistory
} from "./StaffDisciplineHistoryService";

export default class StaffDisciplineHistoryStore {
    staffDisciplineHistoryList = [];
    selectedStaffDisciplineHistory = null;
    selectedStaffDisciplineHistoryList = [];
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    initialStaffDisciplineHistory = {
        disciplineDate: null,
        discipline: null,
        staff: null,
        organization: null,
        department: null,
        file: null
    };
    intactSearchObject = {
        staffId: null,
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };
    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    };
    pagingStaffDisciplineHistory = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ...this.searchObject
        };
        try {

            const res = await pagingStaffDisciplineHistory(searchObject);
            runInAction(() => {
                this.staffDisciplineHistoryList = res?.data?.content || [];
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu quá trình kỷ luật!");
            this.setLoadingInitial(false);
        }
    };

    handleEdit = async (id) => {
        if (id?.length > 0) {
            await this.getById(id)
        } else {
            this.selectedStaffDisciplineHistory = null;
        }
        this.shouldOpenEditorDialog = true;

    };
    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.pagingStaffDisciplineHistory();
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
            const res = await deleteById(this.selectedStaffDisciplineHistory.id);
            if (res?.data) {
                toast.success("Đã xoá quá trình kỷ luật thành công!");
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
        for (let i = 0; i < this.selectedStaffDisciplineHistoryList.length; i++) {
            listId.push(this.selectedStaffDisciplineHistoryList[i]?.id)
        }
        try {
            await deleteMultiple(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListStaffDisciplineHistory([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };
    handleSelectListStaffDisciplineHistory = (list) => {
        this.selectedStaffDisciplineHistoryList = list;
    };
    getById = async (id) => {
        if (id != null) {
            try {
                const data = await getById(id);
                this.handleSelect(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải quá trình kỷ luật!");
            }
        } else {
            this.handleSelect(null);
        }
    };
    handleSelect = (value) => {
        this.selectedStaffDisciplineHistory = value;
    };

    saveOrUpdateStaffDisciplineHistory = async (value) => {
        try {
            const res = await saveStaffDisciplineHistory(value)


            if (res?.status === 200) {
                toast.success(value?.id ? "Chỉnh sửa quá trình kỷ luật thành công!" : "Thêm mới quá trình kỷ luật thành công!");
                await this.pagingStaffDisciplineHistory(); // Cập nhật danh sách
                this.handleClose(true); // Đóng form
            }
        } catch (error) {
            console.error(error);
            toast.error(value?.id ? "Có lỗi xảy ra khi chỉnh sửa quá trình kỷ luật!" : "Có lỗi xảy ra khi thêm mới quá trình kỷ luật!");
        }
    }
};