import {
    deleteEvaluationForm , exportContractApprovalList ,
    exportEvaluationFormWord ,
    getEvaluationFormsById ,
    pageEvaluationForms ,
    saveEvaluationForms , transferEvaluationForm ,
} from "./evaluationTicketService";
import { toast } from "react-toastify";
import { EVALUATE_PERSON , HttpStatus } from "../../../LocalConstants";
import { makeAutoObservable } from "mobx";
import ConstantList from "../../../appConfig";
import history from "../../../../history";
import { getCurrentStaff } from "../../profile/ProfileService";
import LocalStorageService from "../../../services/localStorageService";
import {getEvaluationTemplate} from "../../System/SystemParam/Evaluation/EvaluationTemplate/EvaluationTemplateService";

export default class EvaluationTicketStore {
    page = {
        pageIndex:1 ,
        pageSize:10 ,
        content:[] ,
        totalPages:0 ,
        totalElements:0 ,
    };

    intactSearchObject = {
        department:null ,
        positionTitle:null ,
        directManager:null ,
        status:null ,
        name:null ,
        staff:null ,
        staffName:null ,
        contractType:null ,
        pageIndex:1 ,
        pageSize:10 ,
        keyword:""
    };
    search = JSON.parse(JSON.stringify(this.intactSearchObject));

    isOpenFilter = false;
    form = null;
    isOpenSaveForm = false;
    selected = [];
    evaluatePerson = null
    template = null;
    listOnDelete = [];
    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.page = {
            pageIndex:1 ,
            pageSize:10 ,
            content:[] ,
            totalPages:0 ,
            totalElements:0 ,
        };
        this.search = {
            pageIndex:1 ,
            pageSize:10 ,
        };
        this.form = null;
        this.isOpenSaveForm = false;
        this.listOnDelete = [];
        this.evaluatePerson = null
        this.template = null;
    };

    handleSelected = (item) => {
        this.selected = item;
    };


    setPageSize = async (event) => {
        this.search = {
            ... this.search ,
            pageIndex:1 ,
            pageSize:event.target.value ,
        };
        await this.pageEvaluationForms(this.search);
    };

    handleChangePage = async (event , newPage) => {
        this.search = {
            ... this.search ,
            pageIndex:newPage ,
        };
        await this.pageEvaluationForms(this.search);
    };
    saveEvaluationForms = async (obj) => {
        const response = await saveEvaluationForms(obj);
        if (response) {
            if (response.status === HttpStatus.OK) {
                toast.success(response.data.message);
                // this.isOpenFilter = true;
                // this.handleSetSearchObject({
                //     ... this.search ,
                //     staff:obj?.staff
                // })
                // history.push({
                //     pathname:ConstantList.ROOT_PATH + "staff-evaluation-ticket" ,
                //     search:`?id=${obj?.staffId}&name=${obj?.staffName}` ,
                // });
                history.push(ConstantList.ROOT_PATH + "staff-evaluation-ticket");
            }
        }
        return true;
    };

    pageEvaluationForms = async (searchObj) => {
        this.search = searchObj
        const response = await pageEvaluationForms(searchObj);
        if (response) {
            if (response.status === HttpStatus.OK) {
                this.page.content = response.data.data.content;
                this.page.totalPages = response.data.data.totalPages
                this.page.totalElements = response.data.data.totalElements
            }
        }
    };
    setStaff = async (id , name) => {
        this.handleSetSearchObject({
            ... this.search ,
            staff:{
                id:id ,
                displayName:name
            } ,
            staffId:id ,
            staffName:name ,
        })
        this.isOpenFilter = true;
        await this.pageEvaluationForms(this.search);
    };
    handleIsOpenSaveForm = (state) => {
        this.isOpenSaveForm = state;
    };
    getEvaluationFormsById = async (id) => {
        const response = await getEvaluationFormsById(id);
        if (response.status === HttpStatus.OK) {
            if (response.data.status === HttpStatus.OK) {
                this.form = response.data.data;
                return response.data.data;
            }
        }
    };
    deleteEvaluationFormById = async (id) => {
        const response = await deleteEvaluationForm(id);
        if (response.status === HttpStatus.OK) {
            if (response.data.status === HttpStatus.OK) {
                toast.success(response.data.message);
                this.resetStore();
                await this.pageEvaluationForms(this.search);
            }
        }
    };

    exportEvaluationFormWord = async (id) => {
        try {
            const response = await exportEvaluationFormWord(id);
            const fileName = "Bieu_Mau_Danh_Gia.docx";
            const urlBlob = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = urlBlob;
            link.setAttribute("download" , fileName);
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            console.error("Lỗi khi export file:" , error);
        }
    };
    handleTogglePopupFilter = () => {
        this.isOpenFilter = !this.isOpenFilter;
    };

    handleEvaluatePerson = () => {
        const {user} = LocalStorageService.getLoginUser()
        if (this.form && this.form?.staffId === user?.person?.id) {
            this.evaluatePerson = EVALUATE_PERSON.STAFF
        }
    }

    transferEvaluationForm = async (id) => {
        const response = await transferEvaluationForm(id)
        if (response.status === HttpStatus.OK) {
            if (response.data.status === HttpStatus.OK) {
                toast.success(response.data?.message)
            } else if (response.data.status === HttpStatus.FORBIDDEN) {
                toast.warning(response.data?.message)
            } else if (response.data.status === HttpStatus.BAD_REQUEST) {
                toast.warning(response.data?.message)
            }
        }
    }
    exportContractApprovalList = async (obj) => {
        try {
            const response = await exportContractApprovalList(obj);
            // Tạo blob từ response
            const blob = new Blob([response.data] , {
                type:'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            });

            // Tạo URL để tải file
            const url = window.URL.createObjectURL(blob);

            // Tạo thẻ <a> để trigger download
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download' , 'contract_approval_list.xlsx');
            document.body.appendChild(link);
            link.click();

            // Cleanup
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Lỗi khi tải file Excel:' , error);
        }
    }
    handleSetSearchObject = (searchObject) => {
        this.search = {... searchObject};
    };

    getTemplate = async (id) => {
        const response = await getEvaluationTemplate(id);
        if (response.status === HttpStatus.OK) {
            if (response.data.status === HttpStatus.OK) {
                this.template = response.data.data;
            }
        }
    }
}
