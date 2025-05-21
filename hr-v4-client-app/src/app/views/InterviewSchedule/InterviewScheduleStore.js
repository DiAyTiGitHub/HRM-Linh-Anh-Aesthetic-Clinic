import { makeAutoObservable } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import {
    deleteInterviewSchedule ,
    getInterviewSchedule ,
    pagingInterviewSchedules ,
    saveInterviewSchedule ,
    saveInterviewSchedules ,
} from "./InterviewScheduleService"; // Đổi đúng theo file service

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

export default class InterviewScheduleStore {
    intactSearchObject = {
        pageIndex:1 ,
        pageSize:10 ,
        keyword:"" ,
        candidate:null ,
        candidateId:null ,
        recruitmentRoundId:null ,
        recruitmentRound:null ,
        status:null ,
        fromDate:null ,
        toDate:null
    };
    intactInterviewSchedule = {
        candidate:null ,
        status:0 ,
        interviewTime:new Date() ,
        recruitmentRound:null ,
        staffInterviewSchedules:[] ,
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    interviewScheduleList = [];
    selectedInterviewSchedule = null;
    selectedInterviewScheduleList = [];
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

    resetInterviewScheduleStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.interviewScheduleList = [];
        this.selectedInterviewScheduleList = [];
        this.selectedInterviewSchedule = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    };

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {... searchObject};
    };

    search = async () => {
        this.loadingInitial = true;
        try {
            const newSearchObject = {
                ... this.searchObject ,
                candidateId:this.searchObject.candidate?.id ,
                candidate:null ,
                recruitmentRoundId:this.searchObject?.recruitmentRound?.id ,
                recruitmentRound:null ,
            }


            let {data} = await pagingInterviewSchedules(newSearchObject);
            this.interviewScheduleList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
            this.setLoadingInitial(false);
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.search();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.search();
    };

    handleChangePage = async (event , newPage) => {
        await this.setPageIndex(newPage);
    };

    handleEditInterviewSchedule = (id) => {
        this.getInterviewSchedule(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.search();
    };

    handleDelete = (id) => {
        this.getInterviewSchedule(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteInterviewSchedule(this.selectedInterviewSchedule.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (let i = 0; i < this.selectedInterviewScheduleList.length; i++) {
            try {
                await deleteInterviewSchedule(this.selectedInterviewScheduleList[i].id);
            } catch (error) {
                listAlert.push(this.selectedInterviewScheduleList[i].name);
                console.error(error);
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedInterviewScheduleList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getInterviewSchedule = async (id) => {
        if (id != null) {
            try {
                let data = await getInterviewSchedule(id);
                this.handleSelectInterviewSchedule(data.data.data);
            } catch (error) {
                console.error(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectInterviewSchedule(null);
        }
    };

    handleSelectInterviewSchedule = (interviewSchedule) => {
        this.selectedInterviewSchedule = interviewSchedule;
    };

    handleSelectListInterviewSchedule = (interviewSchedules) => {
        this.selectedInterviewScheduleList = interviewSchedules;
    };
    saveInterviewSchedule = async (interviewSchedule) => {
        try {
            const response = await saveInterviewSchedule(interviewSchedule);

            if (response?.data?.status >= 200 && response?.data?.status < 300) {
                toast.success(i18n.t("Lưu thành công"));
                this.handleClose();
            } else {
                toast.warning(response?.data?.message || i18n.t("Đã có lỗi xảy ra khi lưu lịch phỏng vấn"));
            }
        } catch (error) {
            console.error(error);
            const message = error?.response?.data?.message || i18n.t("toast.error");
            toast.warning(message);
        }
    };

    createInterviewSchedules = async (dto) => {
        try {
            const response = await saveInterviewSchedules(dto);
            const status = response?.data?.status;
            const data = response?.data;

            if (status >= 200 && status < 300) {
                toast.success(i18n.t("Tạo lịch phỏng vấn thành công"));
                this.handleClose();
            } else {
                toast.warning(i18n.t("toast.error"));
            }
        } catch (error) {
            const status = error?.response?.status;
            const message = error?.response?.data?.message;

            if (status === 400 && message) {
                toast.warning(message); // Lỗi cụ thể từ backend
            } else {
                toast.error(i18n.t("toast.error")); // Lỗi chung
            }

            console.error(error);
        }
    };
}
