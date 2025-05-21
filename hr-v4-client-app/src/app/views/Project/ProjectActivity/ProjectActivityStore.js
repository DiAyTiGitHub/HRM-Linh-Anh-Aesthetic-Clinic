import {makeAutoObservable} from "mobx";
import {
    pagingProjectActivity,
    getActivity,
    saveActivity,
    deleteActivity,
    getListByProjectId,
    checkCodeActivity
} from "./ProjectActivityService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {checkCode} from "../ProjectService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

const dataDefaultActivity = {
    id: null,
    code: null,
    name: null,
    description: null,
    project: null,
    child: [],
    parent: null,
    parentId: null,
    startTime: null,
    endTime: null,
    duration: null,
    estimateDuration: null,
};

export default class ProjectActivityStore {
    openCreateEditPopup = false;
    openConfirmDeletePopup = false;
    listActivities = [];
    totalPages = 0;
    pageSize = 10;
    totalElements = 0;
    pageIndex = 1;
    selectedActivity = null;
    keyword = null;

    constructor() {
        makeAutoObservable(this);
    }

    handleOpenActivityPopup = async (activity, isParent, projectId) => {
        try {
            if (activity && activity?.id && !isParent) {
                const {data} = await getActivity(activity?.id);
                this.selectedActivity = data;
            } else {
                this.selectedActivity = {
                    ...dataDefaultActivity,
                    parent: isParent ? activity : null,
                    project: {id: projectId},
                };
            }
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu hoạt động");
        }
    };

    handleDeleteActivity = (activity) => {
        this.selectedActivity = activity;
        this.openConfirmDeletePopup = true;
    };

    handleConfirmDeleteActivity = async (project) => {
        try {
            if (!this.selectedActivity || !this.selectedActivity?.id)
                throw new Error("Selected activity is unavailable");

            const {data} = await deleteActivity(this.selectedActivity?.id);
            toast.success("Hoạt động đã bị xóa!");

            // this.pagingProjectActivity({ projectId: project.id });
            this.getListActivityById({
                projectId: project.id,
                includeAll: true,
            });

            this.openConfirmDeletePopup = false;
        } catch (error) {
            console.error(error);
            toast.error("Xóa hoạt động có lỗi, vui lòng thử lại sau");
        }
    };

    saveActivity = async (formValues) => {
        try {
            if (!formValues) throw new Error("FormValues activity is unavailable");

            let response = await checkCodeActivity(formValues?.id, formValues?.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                const {data} = await saveActivity(formValues);
                toast.success("Đã lưu thông tin hoạt động!");
                this.getListActivityById({
                    projectId: data?.project?.id,
                    includeAll: true,
                });

                this.openCreateEditPopup = false;
            }
        } catch (error) {
            console.error(error);
            toast.error("Lưu thông tin hoạt động có lỗi, vui lòng thử lại sau");
        }
    };

    getListActivityById = async (searchObj) => {
        if (searchObj) {
            try {
                let response = await getListByProjectId(searchObj);
                this.listActivities = response?.data;
                return response.data;
            } catch (error) {
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectListActivity([]);
        }
    };

    pagingProjectActivity = async (item) => {
        try {
            const searchObj = {
                ...item,
                keyword: this?.keyword,
                pageIndex: this?.pageIndex,
                pageSize: this?.pageSize,
            };

            const {data} = await pagingProjectActivity(searchObj);

            this.listActivities = data?.content ? data.content : [];
            this.totalElements = data?.totalElements;
            this.totalPages = data?.totalPages;
        } catch (err) {
            console.error(err);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu, vui lòng thử lại sau");
        }
    };

    setPage = (page) => {
        this.pageIndex = page;
        this.pagingProjectActivity({projectId: this.projectId});
    };

    setRowsPerPage = (event) => {
        this.pageSize = event.target.value;
        this.pageIndex = 1;
        this.pagingProjectActivity({projectId: this.projectId});
    };

    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };

    resetStore = () => {
        this.pageIndex = 1;
        this.pageSize = 10;
        this.totalElements = 0;
        this.totalPages = 0;
        this.projectId = null;
        this.keyword = null;
    };

    projectId = null;

    setProjectId = (projectId) => {
        this.projectId = projectId;
    };

    handleClosePopup = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
    };

    handleSearching = (searchObject) => {
        this.keyword = searchObject?.keyword;
        this.pageIndex = 1;
        this.projectId = searchObject?.projectId;

        this.getListActivityById(searchObject);
    };
}
