import {makeAutoObservable, runInAction} from "mobx";

import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import {
    deleteMultiplePersonCertificate,
    deletePersonCertificateById,
    getAllPersonCertificateByPerson,
    getPersonCertificateById,
    saveOrUpdatePersonCertificate
} from "./PersonCertificateService";

export default class PersonCertificateStore {
    personCertificateList = [];
    personId = null;
    selectedPersonCertificate = null;
    selectedPersonCertificateList = [];
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    initialPersonCertificate = {
        certificate: null, name: "", level: "", issueDate: null, certificateFile: null, person: null
    };

    constructor() {
        makeAutoObservable(this);
    }

    resetPersonCertificateStore = () => {
        this.personCertificateList = [];
        this.personId = null;
        this.selectedPersonCertificate = null;
        this.selectedPersonCertificateList = [];
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    getAllPersonCertificateByPerson = async () => {
        this.loadingInitial = true;
        try {
            const res = await getAllPersonCertificateByPerson(this.personId);
            runInAction(() => {
                this.personCertificateList = res?.data || [];
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu chứng chỉ, chứng nhận!");
            this.setLoadingInitial(false);
        }
    };

    handleEdit = async (id) => {
        if (id) {
            await this.getById(id)
        } else {
            this.selectedPersonCertificate = null;
        }
        this.shouldOpenEditorDialog = true;

    };
    setPersonId = (id) => {
        this.personId = id;
    }
    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.getAllPersonCertificateByPerson();
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
            const res = await deletePersonCertificateById(this.selectedPersonCertificate.id);
            if (res?.data) {
                toast.success("Đã xoá chứng chỉ, chứng nhận thành công!");
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
        for (let i = 0; i < this.selectedPersonCertificateList.length; i++) {
            listId.push(this.selectedPersonCertificateList[i]?.id)
        }
        try {
            await deleteMultiplePersonCertificate(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListPersonCertificate([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };

    handleSelectListPersonCertificate = (list) => {
        this.selectedPersonCertificateList = list;
    };
    
    getById = async (id) => {
        if (id != null) {
            try {
                const data = await getPersonCertificateById(id);
                this.handleSelect(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin ngân hàng!");
            }
        } else {
            this.handleSelect(null);
        }
    };

    handleSelect = (value) => {
        this.selectedPersonCertificate = value;
    };

    saveOrUpdate = async (value) => {
        try {
            const res = await saveOrUpdatePersonCertificate(value); // Gọi API lưu ngân hàng

            if (res?.status === 200) {
                toast.success(value?.id ? "Chỉnh sửa chứng nhận, chứng chỉ thành công!" : "Thêm mới chứng nhận, chứng chỉ thành công!");
                await this.getAllPersonCertificateByPerson(); // Cập nhật danh sách
                this.handleClose(true); // Đóng form
            }
        } catch (error) {
            console.error(error);
            toast.error(value?.id ? "Có lỗi xảy ra khi chỉnh sửa chứng nhận, chứng chỉ!" : "Có lỗi xảy ra khi thêm mới chứng nhận, chứng chỉ!");
        }
    }
};