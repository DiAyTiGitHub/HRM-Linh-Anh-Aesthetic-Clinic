import {makeAutoObservable} from "mobx";
import {convertToNotCome, convertToReceivedJob, pagingWaitingJobCandidates} from "./WaitingJobCandidateService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {Candidate} from "app/common/Model/Candidate/Candidate";
import {HttpStatus} from "app/LocalConstants";
import {SearchObjectCandidate} from "app/common/Model/SearchObject/SearchObjectCandidate";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class WaitingJobCandidateStore {
    intactSearchObject = {
        ...new SearchObjectCandidate(),
        onboardDate: null,
        organizationId: null,
        positionTitle: null
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    totalElements = 0;
    totalPages = 0;
    listWaitingCandidates = [];
    selectedCandidate = new Candidate();
    listChosen = [];

    openReceiveJobPopup = false;
    openNotComeToReceivePopup = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {

        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

        this.totalElements = 0;
        this.totalPages = 0;
        this.listWaitingCandidates = [];
        this.openCreateEditPopup = false;
        this.selectedCandidate = new Candidate();
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listChosen = [];
        this.openConfirmUpdateStatus = false;
        this.openReceiveJobPopup = false;
        this.openNotComeToReceivePopup = false;
    }

    handleSetSearchObject = (searchObject) => {
        if (searchObject.department == null) {
            searchObject.departmentId = null;
        } else {
            searchObject.departmentId = searchObject.department.id;
        }
        this.searchObject = {...searchObject};
    }

    pagingWaitingJobCandidates = async () => {
        try {
            const searchData = {...this?.searchObject};

            const data = await pagingWaitingJobCandidates(searchData);

            this.listWaitingCandidates = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingWaitingJobCandidates();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingWaitingJobCandidates();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListChosen = (chosenItems) => {
        this.listChosen = chosenItems;
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openConfirmUpdateStatus = false;
        this.openReceiveJobPopup = false;
        this.openNotComeToReceivePopup = false;
        this.listChosen = [];
    };

    handleRemoveActionItem = (onRemoveId) => {
        this.listChosen = this?.listChosen?.filter(item => item?.id !== onRemoveId);
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listChosen?.forEach(function (candidate) {
            ids.push(candidate?.id);
        });

        return ids;
    }

    handleOpenReceiveJobPopup = () => {
        this.openReceiveJobPopup = true;
    }

    handleConfirmReceiveJob = async (formValues) => {
        try {
            if (this?.listChosen?.length <= 0) {
                toast.error("Không có ứng viên nào được chọn");
                return;
            }

            const response = await convertToReceivedJob(formValues);
            if (response.status === HttpStatus.OK) {
                const {data, message, status} = response.data
                if (status === HttpStatus.OK) {
                    toast.success(message)
                    toast.success("Ứng viên đã được chuyển thành nhân viên! Vui lòng kiểm tra và cập nhật các thông tin cần thiết.", {
                        autoClose: 5000,
                        draggable: true,
                        limit: 5,
                    });
                    this.handleClose();
                    return data;
                } else {
                    toast.warning(message);
                }
            }

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }

    handleOpenNotComeToReceivePopup = () => {
        this.openNotComeToReceivePopup = true;
    }

    handleConfirmNotComeToReceive = async (formValues) => {
        try {
            if (this?.listChosen?.length <= 0) {
                toast.error("Không có ứng viên nào được chọn");
                this.handleClose();
                return;
            }

            const payload = {
                candidateIds: this.getSelectedIds(),
                refusalReason: formValues?.refusalReason
            };

            const {data} = await convertToNotCome(payload);
            if (!data) throw new Error("");

            toast.success("Cập nhật trạng thái tiếp nhận việc thành công!");

            this.handleClose();
            this.pagingWaitingJobCandidates();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }
}
