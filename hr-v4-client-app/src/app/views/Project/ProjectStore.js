import {makeAutoObservable} from "mobx";
import {
    pagingProject,
    pagingProjectActivity,
    getListByProjectId,
    getProject,
    createProject,
    createActivity,
    editProject,
    editActivity,
    deleteProject,
    deleteActivity,
    checkCode,
    getAllLabelByIdProject,
    getLabelByIdLabel,
    getActivity,
    getTaskByActivity,
    getActivityByProject,
    saveDataProject,
    checkCodeProject,
    saveActivity,
} from "./ProjectService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {getAllStaff} from "app/views/HumanResourcesInformation/StaffService";
import ConstantList from "../../appConfig";
import history from "../../../history";
import localStorageService from "app/services/localStorageService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export const dataDefaultProject = {
    id: null,
    code: null,
    name: null,
    description: null,
    projectStaff: [],
    labels: null,
};

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

export default class ProjectStore {
    projectList = [];
    activityList = [];
    labelList = []; // list label
    selectedProject = dataDefaultProject;
    selectedLabel = null;
    selectedActivityList = null; // selected projectlistactivity
    selectedActivity = null;
    selectedProjectList = [];
    selectedTaskList = [];
    activityDefault = null;
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteActivityDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    shouldOpenEditorDialogProjectActivity = false;
    shouldOpenAddLabelDialog = false; // mở form label
    shouldOpenEditorLabelDialog = false; // mở form add thêm label
    shouldOpenActivityDetail = false;
    shouldOpenActivityCreateEditPopup = false;
    shouldOpenTaskListByActivity = false;

    listActivity = [];
    dataEditActivity = dataDefaultActivity;
    dataEditProject = dataDefaultProject;
    openPopupConfirmDelete = false;

    isRender = false;

    searchObj = {
        pageIndex: 1,
        pageSize: 10,
        projectId: "",
    };
    searchingActivity = true;

    constructor() {
        makeAutoObservable(this);
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    getDataProject = async (idProject) => {
        let newDataProject = dataDefaultProject;
        if (idProject) {
            try {
                let {data} = await getProject(idProject);
                newDataProject = data;
            } catch (error) {
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.dataEditProject = newDataProject;
    };

    getListActivityById = async (idProject, keyword = null) => {
        if (idProject) {
            try {
                let response = await getListByProjectId({projectId: idProject, keyword: keyword});
                this.listActivity = response?.data;
                return response.data;
            } catch (error) {
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectListActivity([]);
        }
    };

    handleSubmitFormProject = async (values, idProject) => {
        let newValues = values;

        let response = await checkCodeProject(values?.id, values?.code);

        if (response.data) {
            toast.warning(i18n.t("toast.duplicate_code"));
            return;
        }

        if (newValues?.projectStaff && newValues?.projectStaff?.length > 0) {
            newValues.projectStaff = newValues?.projectStaff?.map((staff) => ({
                id: staff?.id,
            }));
        }

        saveDataProject(newValues)
            .then((response) => {
                if (!values?.id) {
                    history.push(
                        ConstantList.ROOT_PATH + `timesheet/project/${response?.data?.id}`
                    );
                    // this.handleTabActivity(1);
                } else {
                    this.getDataProject(response?.data?.id);
                }

                toast.success("Chúc mừng công ty! Dự án mới đã được khởi tạo");
            })
            .catch((err) => {
                toast.error(i18n.t("Có lỗi xảy ra khi tạo dự án"));
                console.error(err);
            });
    };

    handleOpenPopupFormActivity = (activity, isParent, idProject) => {
        if (activity && !isParent) {
            getActivity(activity?.id)
                .then((response) => {
                    this.dataEditActivity = response.data;
                })
                .catch(() => {
                    toast.warning(i18n.t("toast.error"));
                });
        } else {
            this.dataEditActivity = {
                ...dataDefaultActivity,
                parent: isParent ? activity : null,
                project: {id: idProject},
            };
        }
        this.openPopupFormActivity = true;
    };

    handleSubmitFormActivity = async (values) => {
        saveActivity(values)
            .then(() => {
                this.handleClosePopup();
                this.getListActivityById(values?.project?.id);
            })
            .catch(() => toast.warning(i18n.t("toast.error")));
    };

    handleClosePopup = () => {
        this.openPopupFormActivity = false;
        this.openPopupConfirmDelete = false;
    };

    handleDeleteActivity = (values) => {
        this.dataEditActivity = values;
        this.openPopupConfirmDelete = true;
    };

    handleConfirmDeleteActivity = async () => {
        try {
            await deleteActivity(this.dataEditActivity.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClosePopup();
            this.getListActivityById(this.dataEditActivity.project.id);
        } catch (error) {
            toast.warning(i18n.t("toast.error"));
        }
    };

    /* Start -Project */
    isFinished = false;
    startDate = null;
    endDate = null;

    updatePageData = async (item) => {
        if (item != null) {
            this.page = 1;
            this.keyword = item?.keyword;
            this.pageSize = item?.rowsPerPage;
            this.startDate = item?.startDate;
            this.endDate = item?.endDate;
            this.isFinished = item?.isFinished;
        }

        await this.search();
    }

    search = async () => {
        this.loadingInitial = true;
        var searchObject = {
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
            startDate: this.startDate,
            endDate: this.endDate,
            isFinished: this.isFinished

        };

        try {
            let data = await pagingProject(searchObject);
            this.projectList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.load_fail"));
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
    /* End -Project */

    /*Start - Project Activity */
    update_PageData = (item) => {
        if (item != null) {
            this.page = 1;
            this.keyword = item.keyword;
            this.searchActivity();
        } else {
            this.searchActivity();
        }
    };

    searchActivity = async () => {
        this.loadingInitial = true;
        let projectId = this.selectedProject?.id;
        var searchObject = {
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
            projectId: projectId,
        };

        try {
            let data = await pagingProjectActivity(searchObject);
            this.activityList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.load_fail"));
            this.setLoadingInitial(false);
        }
    };

    setPageActivity = (page) => {
        this.page = page;
        this.update_PageData();
    };

    setRowsPerPageActivity = (event) => {
        this.rowsPerPage = event.target.value;
        this.page = 1;
        this.update_PageData();
    };

    handleChangePageActivity = (event, newPage) => {
        this.setPageActivity(newPage);
    };

    handleOpenPopupProjectActivity = () => {
        this.shouldOpenEditorDialogProjectActivity = true;
    };

    handleCloseProjectActivity = () => {
        this.shouldOpenEditorDialogProjectActivity = false;
        this.update_PageData();
    };
    /*End - Project Activity */

    /*Start- Activity timeSheet */
    updatePageData_TimeSheet = (item) => {
        if (item != null) {
            this.page = 1;
            this.keyword = item.keyword;

            this.searchActivity_TimeSheet();
        } else {
            this.searchActivity_TimeSheet();
        }
    };

    searchActivity_TimeSheet = async () => {
        this.loadingInitial = true;
        let projectId = this.selectedProject?.id;
        var searchObject = {
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
            projectId: projectId,
        };

        try {
            let data = await pagingProjectActivity(searchObject);
            this.selectedActivityList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.load_fail"));
            this.setLoadingInitial(false);
        }
    };

    setPageActivity_TimeSheet = (page) => {
        this.page = page;
        this.updatePageData_TimeSheet();
    };

    setRowsPerPageActivity_TimeSheet = (event) => {
        this.rowsPerPage = event.target.value;
        this.page = 1;
        this.updatePageData_TimeSheet();
    };

    handleChangePageActivity_TimeSheet = (event, newPage) => {
        this.setPageActivity_TimeSheet(newPage);
    };
    /*End- Activity timeSheet */

    handleEditProject = (id) => {
        this.getProject(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleGetLabel = () => {
        this.shouldOpenEditorLabelDialog = true;
        this.getAllLabelsByIdProject();
    };

    handleAddLabel = () => {
        this.shouldOpenAddLabelDialog = true;
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.shouldOpenConfirmationDeleteActivityDialog = false;
        this.shouldOpenActivityCreateEditPopup = false;
        this.shouldOpenTaskListByActivity = false;
        this.isRender = false;
        this.updatePageData();
        // this.search();
    };

    handleRender = (render) => {
        this.isRender = render;
    };

    handleCloseLabel = () => {
        this.shouldOpenEditorLabelDialog = false;
        this.updatePageData();
    };

    handleCloseAddLabel = () => {
        this.shouldOpenAddLabelDialog = false;
        this.getAllLabelsByIdProject();
    };

    handleOpenActivityPopup = () => {
        this.shouldOpenActivityCreateEditPopup = true;
    };

    handleEditActivity = (id) => {
        this.getActivity(id).then(() => {
            this.shouldOpenActivityCreateEditPopup = true;
        });
    };

    handleCloseActivityPopup = () => {
        this.shouldOpenActivityCreateEditPopup = false;
    };

    handleDelete = (id) => {
        this.getProject(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteProject(this.selectedProject.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.delete_fail"));
        }
    };

    handleCloseActivityDetail = (confirm) => {
        this.shouldOpenActivityDetail = confirm;
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedProjectList.length; i++) {
            try {
                await deleteProject(this.selectedProjectList[i].id);
            } catch (error) {
                listAlert.push(this.selectedProjectList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.delete_fail"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getProject = async (id) => {
        if (id != null) {
            try {
                let data = await getProject(id);
                this.handleSelectProject(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.get_fail"));
            }
        } else {
            this.handleSelectProject(null);
        }
    };

    handleSelectProject = (project) => {
        this.selectedProject = project;
    };

    getActivity = async (id) => {
        if (id != null) {
            try {
                let data = await getActivity(id);
                this.handleSelectActivity(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.get_fail"));
            }
        } else {
            this.handleSelectProject(null);
        }
    };

    getTaskByActivity = async (id) => {
        if (id != null) {
            try {
                let data = await getTaskByActivity(id);
                this.handleSelectListTask(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.get_fail"));
            }
        } else {
            this.handleSelectListTask(null);
        }
    };

    handleSelectListTask = (timeSheets) => {
        this.selectedTaskList = timeSheets;
    };

    handleOpenTaskByActivity = (id) => {
        this.getTaskByActivity(id).then(() => {
            this.shouldOpenTaskListByActivity = true;
        });
    };

    getListById = async (id) => {
        if (id != null) {
            try {
                let data = await getListByProjectId({
                    projectId: id,
                    includeAll: true,
                });
                this.handleSelectListActivity(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.get_fail"));
            }
        } else {
            this.handleSelectListActivity(null);
        }
    };

    //handle select project
    handleSelectListActivity = (activities) => {
        this.selectedActivityList = activities;
    };

    getListProjectActivity = async (projectId) => {
        try {
            let data = await getListByProjectId({projectId: projectId});
            this.selectedActivityList = data.data ? data.data : [];
        } catch (error) {
            console.log(error);
            toast.warning("Bạn chưa chọn dự án");
        }
    };

    handleSelectLabel = (label) => {
        this.selectedLabel = label;
    };

    handleSelectListProject = (project) => {
        this.selectedProjectList = project;
    };

    createProject = async (project) => {
        try {
            let response = await checkCode(project?.id, project?.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createProject(project);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.error(i18n.t("toast.add_fail"));
        }
    };

    createActivity = async (activity) => {
        try {
            // let response = await checkCodeActivity(activity.id, activity.code);
            // if (response.data) {
            //   toast.warning(i18n.t("toast.duplicate_code"));
            // } else {
            await createActivity(activity);
            this.handleClose();
            this.handleRender(true);
            toast.success(i18n.t("toast.add_success"));
            // }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.add_fail"));
        }
    };

    getActivityByProject = async (id, parentId) => {
        if (id != null) {
            try {
                let data = await getActivityByProject(id, parentId);
                this.handleSelectActivity(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.get_fail"));
            }
        } else {
            this.handleSelectActivity(null);
        }
    };

    editProject = async (project) => {
        try {
            let response = await checkCode(project.id, project.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editProject(project);
                toast.success(i18n.t("toast.update_success"));
                // this.handleClose();
                this.getProject(project.id);
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.update_fail"));
        }
    };

    editActivity = async (activity) => {
        try {
            // let response = await checkCodeActivity(activity.id, activity.code);
            // if (response.data) {
            //   toast.warning(i18n.t("toast.duplicate_code"));
            // } else {

            await editActivity(activity);
            this.handleRender(true);
            this.handleClose();
            toast.success(i18n.t("toast.update_success"));
            // }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.update_fail"));
        }
    };

    getLabelByIdLabels = async (id) => {
        if (id != null) {
            try {
                let data = await getLabelByIdLabel(id);
                this.handleSelectLabel(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.get_fail"));
            }
        } else {
            this.handleSelectLabel(null);
        }
    };

    handleSelectLabel = (label) => {
        this.selectedLabel = label;
    };

    getAllLabelsByIdProject = async () => {
        try {
            let data = await getAllLabelByIdProject();

            this.labelList = data.data;
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.get_fail"));
        }
    };

    handlePagingStaff = async () => {
        let data = await getAllStaff();
        return data.data;
    };

    setSearchingActivity = (val) => {
        this.searchingActivity = val;
    };

    handleSelectActivity = (activity) => {
        this.selectedActivity = activity;
    };

    // HANDLE FOR SELECTING PROJECT IN TASK POPUP (CREATE/UPDATE)
    availableProjectList = [];

    pagingProject = async (searchWrapper) => {
        try {
            const searchObject = {
                ...searchWrapper,
                pageIndex: 1,
                pageSize: 6666,
            };

            const {data} = await pagingProject(searchObject);

            this.availableProjectList = data?.content;
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu các dự án!");
        }
    };

    // END OF HANDLING FOR SELECTING PROJECT IN TASK POPUP (CREATE/UPDATE)

    // AUTHORITY TO CREATE, UPDATE, EDIT PROJECTS
    canCreateUpdateDeleteProject = () => {
        const roles =
            localStorageService
                .getLoginUser()
                ?.user?.roles?.map((item) => item.authority) || [];

        let hasAuthToCreateDelete = [
            "HR_MANAGER",
            "ROLE_ADMIN",
            "ROLE_SUPER_ADMIN",
        ];

        if (roles.some((role) => hasAuthToCreateDelete.indexOf(role) !== -1))
            return true;

        return false;
    };
}
