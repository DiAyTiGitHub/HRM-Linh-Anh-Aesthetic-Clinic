import {makeAutoObservable, runInAction} from "mobx";
import {
    pagingHrDocumentTemplate,
    getHrDocumentTemplateById,
    deleteHrDocumentTemplateById,
    saveOrUpdate,
} from "./HrDocumentTemplateService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";

export default class HrDocumentTemplateStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    hrDocumentTemplateList = [];
    selectedHrDocumentTemplate = null;
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    };


    paging = async () => {
        const searchObject = {
            ...this.searchObject
        };

        try {
            const res = await pagingHrDocumentTemplate(searchObject);
            runInAction(() => {
                this.hrDocumentTemplateList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu của KPI!");
            this.setLoadingInitial(false);
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.paging();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.paging();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleEditHrDocumentTemplate = (id) => {
        this.getHrDocumentTemplateById(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.paging();
    };

    handleDelete = (id) => {
        this.getHrDocumentTemplateById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };
    handleConfirmDelete = async () => {
        try {
            const res = await deleteHrDocumentTemplateById(this.selectedHrDocumentTemplate.id);
            if (res?.data) {
                toast.success("Đã xoá mẫu tài liệu nhân viên thành công!");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            await this.paging();
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };

    getHrDocumentTemplateById = async (id) => {
        if (id != null) {
            try {
                const data = await getHrDocumentTemplateById(id);
                this.handleSelectHrDocumentTemplate(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin mẫu tài liệu nhân viên !");
            }
        } else {
            this.handleSelectHrDocumentTemplate(null);
        }
    };
    handleSelectHrDocumentTemplate = (values) => {
        this.selectedHrDocumentTemplate = values;
    };

    saveOrUpdate = async (value) => {
        try {
            const res = await saveOrUpdate(value);
            toast.success(
                value?.id ? "Chỉnh sửa mẫu tài liệu nhân viên thành công!" : "Thêm mới mẫu tài liệu nhân viên thành công!"
            );
            await this.paging();
            this.handleClose(true);
            return res?.data;
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã mẫu tài liệu nhân viên đã được sử dụng, vui lòng sử dụng mẫu tài liệu nhân viên khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.warning("Có lỗi xảy ra khi thêm mới mẫu tài liệu nhân viên!");
            }
        }
    };

    resetHrDocumentTemplateStore = () => {
        this.hrDocumentTemplateList = [];
        this.selectedHrDocumentTemplate = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    };
}
