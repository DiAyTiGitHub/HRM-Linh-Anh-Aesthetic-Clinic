import {makeAutoObservable} from "mobx";
import {getRecruitmentRequestSummaries} from "../RecruitmentRequestV2/RecruitmentRequestV2Service";
import {HttpStatus} from "../../../LocalConstants";

export default class RecruitmentRequestSummaryStore {
    payload = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
    }
    defaultPayload = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null
    }
    page = {
        content: [],
        totalElements: 0,
        totalPages: 0,
    }
    setPageIndex = (index) => {
        this.payload.pageIndex = index;
    }

    setPageSize = (size) => {
        this.payload.pageSize = size;
    }

    constructor() {
        makeAutoObservable(this)
    }

    getRecruitmentRequestSummaries = async (payload) => {
        const response = await getRecruitmentRequestSummaries(payload);
        if (response.status === HttpStatus.OK) {
            if (response.data.status === HttpStatus.OK) {
                this.page = response.data.data;
            }
        }
    }
    handleSetSearchObject = (payload) => {
        this.payload = {...payload};
        this.getRecruitmentRequestSummaries(this.payload);
    }
}