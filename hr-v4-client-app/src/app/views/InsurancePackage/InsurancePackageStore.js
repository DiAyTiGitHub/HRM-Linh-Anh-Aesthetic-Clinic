import { makeAutoObservable, runInAction } from "mobx";
import {
    pagingInsurancePackage,
    getInsurancePackageById,
    deleteInsurancePackageById,
    saveOrUpdate,
} from "./InsurancePackageService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";

export default class InsurancePackageStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    insurancePackageList = [];
    selectedInsurancePackage = null;
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
        this.searchObject = { ...searchObject };
    };


    paging = async () => {
        const searchObject = {
            ...this.searchObject
        };

        try {
            const res = await pagingInsurancePackage(searchObject);
            runInAction(() => {
                this.insurancePackageList = res?.data?.content || [];
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

    handleEditInsurancePackage = (id) => {
        this.getInsurancePackageById(id).then(() => {
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
        this.getInsurancePackageById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };
    handleConfirmDelete = async () => {
        try {
            const res = await deleteInsurancePackageById(this.selectedInsurancePackage.id);
            if (res?.data) {
                toast.success("Đã xoá gói bảo hiểm thành công!");
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

    getInsurancePackageById = async (id) => {
        if (id != null) {
            try {
                const data = await getInsurancePackageById(id);
                this.handleSelectInsurancePackage(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin gói bảo hiểm !");
            }
        } else {
            this.handleSelectInsurancePackage(null);
        }
    };
    handleSelectInsurancePackage = (values) => {
        this.selectedInsurancePackage = values;
    };

    saveOrUpdate = async (value) => {
        try {
            const res = await saveOrUpdate(value);
            toast.success(
                value?.id ? "Chỉnh sửa gói bảo hiểm thành công!" : "Thêm mới gói bảo hiểm thành công!"
            );
            await this.paging();
            this.handleClose(true);
            return res?.data;
        } catch (error) {
            console.error(error);
            if (error.response.status === 409) {
                toast.error("Mã gói bảo hiểm đã được sử dụng, vui lòng sử dụng gói bảo hiểm khác", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.warning("Có lỗi xảy ra khi thêm mới gói bảo hiểm!");
            }
        }
    };

    resetInsurancePackageStore = () => {
        this.insurancePackageList = [];
        this.selectedInsurancePackage = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    };
}
