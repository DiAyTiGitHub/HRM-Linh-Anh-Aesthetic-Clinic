import { makeAutoObservable } from "mobx";
import {
    pagingPosition,
    getById,
    savePosition,
    deleteMultiple,
    deletePosition,
    importPosition,
    downloadPositionTemplate,
    removeStaffFromPosition,
    assignPositionsForStaff,
    fetchTransferPosition,
    fetchTransferStaff,
    exportExcelPositionData,
    importPositionRelationShip,
    downloadPositionRelationshipTemplate,
    getByStaffId,
    autoGenCode
} from "./PositionService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchObjectPosition } from "app/common/Model/SearchObject/SearchObjectPosition";
import { Position } from "app/common/Model/HumanResource/Position";
import { saveAs } from "file-saver";
import { fr } from "date-fns/locale";
import { SalaryPeriod } from "../../common/Model/Salary/SalaryPeriod";
import {HttpStatus} from "../../LocalConstants";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class PositionStore {
    intactSearchObject = {
        ...new SearchObjectPosition(),
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listPosition = [];
    openCreateEditPopup = false;
    selectedPosition = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    openSelectMultiplePopup = false;
    openConfirmAssignPopup = false;
    openConfirmRemoveFromPosPopup = false;
    opentFormLeavePosition = false;
    opentFormTransfer = false;
    opentStaffFormTransfer = false;
    currentPositions = [];
    openViewPopup = false;

    handleOpenView = async (positionId) => {
        try {
            if (positionId) {
                const { data } = await getById(positionId);
                this.selectedPosition = data;
            } else {
                this.selectedPosition = {
                    ...new Position(),
                };
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

    setCurrentPositions = (list) => {
        this.currentPositions = list;
    };
    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openSelectMultiplePopup = false;
        this.openConfirmRemoveFromPosPopup = false;
        this.opentFormTransfer = false;
        this.opentStaffFormTransfer = false;
        this.openViewPopup = false;
    };

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listPosition = [];
        this.openCreateEditPopup = false;
        this.selectedPosition = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openSelectMultiplePopup = false;
        this.openConfirmAssignPopup = false;
        this.openViewPopup = false;
    };

    fetchPositions = async (staffId) => {
        if (!staffId) return;

        try {
            const payload = {
                staff: {
                    id: staffId,
                },
                staffId: staffId,
                pageIndex: 1,
                pageSize: 99999,
            };

            const { data } = await pagingPosition(payload);

            this.setCurrentPositions(data?.content);
        } catch (error) {
            toast.error("Có dữ liệu xảy ra khi lấy dữ liệu vị trí của nhân viên");
            console.error(error);
        }
    };

    uploadFileExcel = async (event) => {
        try {
            const fileInput = event.target;
            const file = fileInput.files[0];
            fileInput.value = null; // Xóa giá trị input để cho phép chọn lại cùng một file

            await importPosition(file);

            toast.success("Nhập excel thành công");

            this.searchObject = {
                ...this.searchObject,
                pageIndex: 1,
            };

            await this.pagingPosition();
        } catch (error) {
            console.error(error);

            if (error.response && error.response.status === 409) {
                toast.error("Mã vị trí đã tồn tại, vui lòng chọn mã khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error("Nhập excel thất bại");
            }
        } finally {
            this.handleClose();
        }
    };

    handleDownloadPositionTemplate = async () => {
        try {
            const res = await downloadPositionTemplate();
            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });
            saveAs(blob, "Mẫu nhập dữ liệu vị trí.xlsx");
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
        }
    };

    handleDownloadPositionRelationshipTemplate = async () => {
        try {
            const res = await downloadPositionRelationshipTemplate();
            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });
            saveAs(blob, "Mẫu nhập dữ liệu quan hệ vị trí.xlsx");
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
        }
    };

    updatePageData = (item) => {
        if (item != null) {
            this.handleSetSearchObject(item);
            this.pagingPosition();
        } else {
            this.pagingPosition();
        }
    };

    handleSetSearchObject = (searchObject) => {
        if (searchObject.organization == null) {
            searchObject.organizationId = null;
        } else {
            searchObject.organizationId = searchObject.organization.id;
        }

        if (searchObject.staff == null) {
            searchObject.staffId = null;
        } else {
            searchObject.staffId = searchObject.staff.id;
        }

        if (searchObject.department == null) {
            searchObject.departmentId = null;
        } else {
            searchObject.departmentId = searchObject.department.id;
        }

        if (searchObject.rankTitle == null) {
            searchObject.rankTitleId = null;
        } else {
            searchObject.rankTitleId = searchObject.rankTitle.id;
        }

        if (searchObject.positionTitle == null) {
            searchObject.positionTitleId = null;
        } else {
            searchObject.positionTitleId = searchObject.positionTitle.id;
        }

        this.searchObject = { ...searchObject };
    };

    pagingPosition = async () => {
        try {
            //const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
            };
            const data = await pagingPosition(payload);

            this.listPosition = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            return data.data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingPosition();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingPosition();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deletePositions) => {
        this.listOnDelete = deletePositions;
    };

    getById = async (positionId) => {
        try {
            const { data } = await getById(positionId);
            this.selectedPosition = data;
            this.openCreateEditPopup = true;
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    getByStaffId = async (staffId) => {
        try {
            const { data } = await getByStaffId(staffId);
            this.selectedPosition = data;
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleDelete = (position) => {
        this.selectedPosition = { ...position };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleTransfer = () => {
        this.opentFormTransfer = true;
    };

    handleTransferStaff = (position) => {
        this.selectedPosition = { ...position };
        this.opentStaffFormTransfer = true;
    };

    handleOpenCreateEdit = async (positionId) => {
        try {
            if (positionId) {
                const { data } = await getById(positionId);
                this.selectedPosition = data;
            } else {
                this.selectedPosition = {
                    ...new Position(),
                };
            }

            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleOpenCreateFormWithStaff = (staff) => {
        this.selectedPosition = { ...new Position(), staff: staff };
        this.openCreateEditPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deletePosition(this?.selectedPosition?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingPosition();

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listOnDelete?.length; i++) {
                deleteData.push(this?.listOnDelete[i]?.id);
            }

            // console.log("deleteData", deleteData)
            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingPosition();
            this.listOnDelete = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmTransfer = async (department) => {
        if (department == null || this.listOnDelete?.length === 0) {
            toast.error("Chưa chọn phòng ban hoặc chưa chọn vị trí để chuyển");
            return;
        }
        const dto = {
            department: {
                id: department.id,
            },
            positions: [...this?.listOnDelete],
        };

        await fetchTransferPosition(dto)
            .then((result) => {
                console.log(result);
            })
            .then(() => {
                this.pagingPosition();
                this.handleClose();
            })
            .catch((err) => {
                toast.error(i18n.t("toast.error"));
            });
    };

    handleConfirmStaffTransfer = async (fromPosition, toPosition, note) => {
        if (toPosition == null || fromPosition == null) {
            toast.error("Chưa chọn vị trí đích hoặc vị trí nguồn");
            return;
        }

        const dto = {
            fromPosition: {
                id: fromPosition.id,
            },
            toPosition: {
                id: toPosition.id,
            },
            note,
        };

        await fetchTransferStaff(dto)
            .then((result) => {
                console.log(result);
            })
            .then(() => {
                this.pagingPosition();
                this.handleClose();
            })
            .catch((err) => {
                toast.error(i18n.t("toast.error"));
            });
    };

    savePosition = async (position) => {
        try {
            let { directManagerPosition, indirectManagerPosition } = position;
            const { data } = await savePosition(position);
            toast.success("Thông tin Vị trí đã được lưu");
            this.handleClose();
            return data;
            // } catch (error) {
            //     console.error(error);
            //     toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
            // }
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã vị trí đã được sử dụng, vui lòng sử dụng mã vị trí khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };

    getPositionStatus = (status) => {
        if (status == 0) return "Không được sử dụng";
        if (status == 1) return "Đang được sử dụng";
    };

    handleOpenSelectMultiplePopup = (currentPositions) => {
        this.handleSelectListDelete(currentPositions || []);

        this.openSelectMultiplePopup = true;
    };

    handleOpenConfirmRemoveFromPosPopup = (position) => {
        this.selectedPosition = {
            ...position,
        };
        this.openConfirmRemoveFromPosPopup = true;
    };

    handleRemoveStaffFromPosition = async () => {
        try {
            const { data } = await removeStaffFromPosition(this?.selectedPosition?.id);
            toast.success(i18n.t("toast.delete_success"));

            this.handleClose();

            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleOpenConfirmAssignPopup = () => {
        this.openConfirmAssignPopup = true;
    };

    handleMergeSearchObject = (newSearch) => {
        this.searchObject = {
            ...this.searchObject,
            ...newSearch,
        };
    };

    handleCloseConfirmAssignPopup = () => {
        this.openConfirmAssignPopup = false;
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listOnDelete?.forEach(function (item) {
            ids.push(item?.id);
        });

        return ids;
    };

    handleAssignPositionsForStaff = async (staffId) => {
        try {
            const chosenIds = this.getSelectedIds();
            const payload = {
                staffId: staffId,
                chosenIds: chosenIds,
            };

            const { data } = await assignPositionsForStaff(payload);

            toast.success("Đã gán nhân viên vào các vị trí");

            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handlExportExcelPositionData = async () => {
        if (this.totalElements > 0) {
            try {
                const res = await exportExcelPositionData({ ...this.searchObject });
                toast.success(i18n.t("general.successExport"));

                const blob = new Blob([res.data], {
                    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                });
                saveAs(blob, "DuLieuChucVu.xlsx");
            } catch (error) {
                toast.warning(i18n.t("toast.error"));
                console.error("Export error:", error);
            } finally {
                // Code xử lý sau khi xong (nếu cần)
            }
        } else {
            toast.warning(i18n.t("general.noData"));
        }
    };

    uploadPositionRelationShipFileExcel = async (event) => {
        const file = event.target.files[0];
        importPositionRelationShip(file)
            .then(() => {
                toast.success("Nhập excel thành công");
                this.searchObject = {
                    ...this.searchObject,
                    pageIndex: 1,
                };
                this.pagingPosition();
            })
            .catch(() => {
                toast.error("Nhập excel thất bại");
            })
            .finally(() => {
                this.handleClose();
            });
        event.target.value = null;
    };

    autoGenCode = async (configKey) =>{
        const response = await autoGenCode(configKey)
        if(response.status === HttpStatus.OK){
            return response.data;
        }
    }
}
