import { makeAutoObservable, runInAction } from "mobx";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import {
    deleteMultipleStaffFamilyRelationship,
    deleteStaffFamilyRelationshipByStaffId,
    getAllStaffFamilyRelationshipByStaffId,
    saveStaffFamilyRelationship,
    updateStaffFamilyRelationship,
    getStaffFamilyRelationshipByStaffId
} from "./StaffFamilyRelationshipService";


export default class StaffFamilyRelationshipStore {
    staffFamilyRelationshipList = [];
    staffId = null;
    selectedStaffFamilyRelationship = null;
    selectedStaffFamilyRelationshipList = [];
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    initialStaffFamilyRelationship = {
        staff: null,
        familyRelationship: null,
        fullName: "",
        birthDate: null,
        profession: null,
        address: "",
        description: "",
        workingPlace: "",
        isDependent: false,
        taxCode: "",
        dependentDeductionFromDate: null,
        dependentDeductionToDate: null,
    };

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    getAllStaffFamilyRelationshipByStaffId = async () => {
        this.loadingInitial = true;
        try {
            const res = await getAllStaffFamilyRelationshipByStaffId(this.staffId);
            runInAction(() => {
                this.staffFamilyRelationshipList = res?.data || [];
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu quan hệ nhân thân!");
            this.setLoadingInitial(false);
        }
    };

    handleStaffFamilyRelationshipEdit = async (id) => {
        if (id) {
            await this.getStaffFamilyRelationshipById(id)
        } else {
            this.selectedStaffFamilyRelationship = null;
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
        if (state) this.getAllStaffFamilyRelationshipByStaffId();
    };

    handleDelete = (id) => {
        this.getStaffFamilyRelationshipById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };
    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };
    handleConfirmDelete = async () => {
        try {
            const res = await deleteStaffFamilyRelationshipByStaffId(this.selectedStaffFamilyRelationship?.id);
            if (res?.data) {
                toast.success("Đã xoá quan hệ nhân thân thành công!");
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
        for (let i = 0; i < this.selectedStaffFamilyRelationshipList.length; i++) {
            listId.push(this.selectedStaffFamilyRelationshipList[i]?.id)
        }
        try {
            await deleteMultipleStaffFamilyRelationship(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSetSelectListStaffFamilyRelationship([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };
    handleSetSelectListStaffFamilyRelationship = (list) => {
        this.selectedStaffFamilyRelationshipList = list;
    };
    getStaffFamilyRelationshipById = async (id) => {
        if (id != null) {
            try {
                const data = await getStaffFamilyRelationshipByStaffId(id);
                this.handleSelectStaffFamilyRelationship(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin ngân hàng!");
            }
        } else {
            this.handleSelectStaffFamilyRelationship(null);
        }
    };
    handleSelectStaffFamilyRelationship = (value) => {
        this.selectedStaffFamilyRelationship = value;
    };

    saveOrUpdate = async (value) => {
        try {
            let res = null;
            if (!value?.id) {
                res = await saveStaffFamilyRelationship(value);
            } else {
                res = await updateStaffFamilyRelationship(value, value?.id);
            }

            if (res?.status === 200) {
                toast.success(value?.id ? "Chỉnh sửa quan hệ nhân thân thành công!" : "Thêm mới quan hệ nhân thân thành công!");
                await this.getAllStaffFamilyRelationshipByStaffId(); // Cập nhật danh sách
                this.handleClose(true); // Đóng form
            }
        } catch (error) {
            console.error(error);
            toast.error(value?.id ? "Có lỗi xảy ra khi chỉnh sửa quan hệ nhân thân!" : "Có lỗi xảy ra khi thêm mới quan hệ nhân thân!");
        }
    }
};