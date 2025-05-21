import {makeAutoObservable, runInAction} from "mobx";
import {toast} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {
    deleteHrDocumentItemById,
    deleteMultipleHrDocumentItems,
    getHrDocumentItemById,
    pagingHrDocumentItem,
    saveOrUpdateHrDocumentItem
} from "./HrDocumentItemService";
import i18n from "i18n";
import {deleteFamilyRelationship} from "../FamilyRelationship/FamilyRelationshipService";

export default class HRDocumentItemStore {
    hrDocumentItemList = [];
    selectedHrDocumentItem = null;
    selectedListHrDocumentItem = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
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

    updatePageData = (item) => {
        if (item != null) {
            this.page = 1;
            this.keyword = item.keyword;
            this.search();
        } else {
            this.search();
        }
    };
    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };
    search = async () => {
        this.loadingInitial = true;
        const searchObject = {
            keyword: this.keyword, pageIndex: this.page, pageSize: this.rowsPerPage,
        };

        try {
            const res = await pagingHrDocumentItem(searchObject);
            runInAction(() => {
                this.hrDocumentItemList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu!");
            this.setLoadingInitial(false);
        }
    };

    setPage = (page) => {
        this.page = page;
        this.updatePageData();
    };

    setRowsPerPage = (event) => {
        this.rowsPerPage = event.target.value;
        this.page = 1;
        this.updatePageData();
    };

    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };

    handleSetSelectedListHrDocumentItem = (list) => {
        console.log(list)
        this.selectedListHrDocumentItem = list;
    };
    handleConfirmDeleteList = async () => {
        let listId = [];
        for (var i = 0; i < this.selectedListHrDocumentItem.length; i++) {
            listId.push(this.selectedListHrDocumentItem[i]?.id)
        }
        try {
            await deleteMultipleHrDocumentItems(listId)
            toast.success(i18n.t("toast.delete_success"));
            this.handleSetSelectedListHrDocumentItem([])
            this.handleClose(true);
        } catch (error) {
            toast.warning(i18n.t("toast.error"));
            this.handleClose();
        }
    };
    handleEditHrDocumentItem = (id) => {
        this.getHrDocumentItemById(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.updatePageData();
    };

    handleDelete = (id) => {
        this.getHrDocumentItemById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleConfirmDelete = async () => {
        try {
            const res = await deleteHrDocumentItemById(this.selectedHrDocumentItem.id);
            if (res?.data) {
                toast.success("Đã xoá thành công!");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            await this.search();
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };

    getHrDocumentItemById = async (id) => {
        if (id != null) {
            try {
                const data = await getHrDocumentItemById(id);
                this.handleSelectHrDocumentItem(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin!");
            }
        } else {
            this.handleSelectHrDocumentItem(null);
        }
    };

    handleSelectHrDocumentItem = (item) => {
        this.selectedHrDocumentItem = item;
    };

    saveOrUpdateHrDocumentItem = async (item) => {
        try {
            const res = await saveOrUpdateHrDocumentItem(item);
            toast.success(item?.id ? "Chỉnh sửa thành công!" : "Thêm mới thành công!");
            await this.search();

            this.handleClose(true);

            return res?.data;
        } catch (error) {
            console.error(error);
            if (error?.response?.status == 409) {
                if (error?.response?.data?.documentTemplate?.name) {
                    toast.error("Mã tài liệu đã được đã có trong mẫu tài liệu " + error?.response?.data?.documentTemplate?.name + ", vui lòng nhập mã khác", {
                        autoClose: 5000, draggable: false, limit: 5,
                    });
                } else {
                    toast.error("Mã tài liệu đã được sử dụng, vui lòng sử dụng mã tài liệu khác", {
                        autoClose: 5000, draggable: false, limit: 5,
                    });
                }
            } else if (error?.response?.status == 304) {
                toast.warning("Tài liệu mặc định của hệ thống không được phép chỉnh sửa", {
                    autoClose: 5000, draggable: false, limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };

    resetStore = () => {
        this.hrDocumentItemList = [];
        this.selectedHrDocumentItem = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.page = 1;
        this.rowsPerPage = 10;
        this.keyword = "";
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
    };
}
