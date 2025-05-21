import { formatDate, getFullYear } from "app/LocalFunction";
import { getMonth } from "date-fns";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import { deleteJournals, getAllJournalCalendar, getJournals, getJournalsByMonth, saveJournals } from "./CalendarService";

const dataDefaultFormJournals = {
    id: null,
    description: null,
    name: null,
    journalDate: null,
    fromDate: null,
    toDate: null,
    location: null,
    type: null,
}

export default class CalendarStore {
    listDataJournals = [];
    dataEditFormJournals = dataDefaultFormJournals;
    currentMonth = new Date();
    dateValue = new Date();
    valueCurrentDate = [];

    openFormJournals = false;
    openConfirmDeleteJournals = false;

    constructor() {
        makeAutoObservable(this);
    }

    getListDataJournals = () => {
        getAllJournalCalendar().then((response) => {
            this.listDataJournals = response.data.content
        })
    }
    loadJournalsByMonth = () => {
        getJournalsByMonth({ monthReport: getMonth(this.currentMonth) + 1, yearReport: getFullYear(this.currentMonth) }).then((response) => {
            this.listDataJournals = response.data
        })
    }

    handleOpenFormJournals = (id, type, journalDate) => {
        if (id) {
            getJournals(id).then((response) => {
                this.dataEditFormJournals = response.data;
            }).catch(() => { return; })
        } else {
            this.dataEditFormJournals = { ...dataDefaultFormJournals, type: type, journalDate: journalDate };
        }
        this.openFormJournals = true;
    }

    handleSubmitFormJournals = (obj) => {
        saveJournals(obj).then(({ data }) => {
            const newValueCurrentDate = this.valueCurrentDate;
            if (getMonth(obj.journalDate) === getMonth(this.currentMonth) && getFullYear(obj.journalDate) === getFullYear(this.currentMonth)) {
                this.loadJournalsByMonth();
            }
            if (obj.id) {
                const indexItem = newValueCurrentDate.find(e => e.id === obj.id);
                newValueCurrentDate[indexItem] = data
            } else {
                newValueCurrentDate.push(data);
            }
            this.handleChangeValueCurrentDate(newValueCurrentDate)
            this.handleClosePopup();
        }).catch(() => {
            toast.error('Đã có lỗi xảy ra!')
        })
    }

    handleClosePopup = () => {
        this.openFormJournals = false;
        this.openConfirmDeleteJournals = false;
    }

    handleChangeCurrentMonth = date =>{
        this.currentMonth = date;
        this.loadJournalsByMonth();
    }
    
    handleChangeValueCurrentDate = value => this.valueCurrentDate = value.concat()

    handleChangeDateValue = (date) => {
        this.dateValue = date;
        if(Array.isArray(this.listDataJournals)) {
            this.handleChangeValueCurrentDate(this.listDataJournals?.filter(e => formatDate('YYYY MM DD', e.journalDate) === formatDate('YYYY MM DD', date)))
        }
    }

    handleOpenConfirmDeleteJournals = (value) => {
        this.dataEditFormJournals = value;
        this.openConfirmDeleteJournals = true
    }

    handleConfirmDeleteJournals = () => {
        deleteJournals(this.dataEditFormJournals.id).then(() => {
            if (getMonth(this.dataEditFormJournals.journalDate) === getMonth(this.currentMonth) && getFullYear(this.dataEditFormJournals.journalDate) === getFullYear(this.currentMonth)) {
                this.loadJournalsByMonth();
            }
            this.handleChangeValueCurrentDate(this.valueCurrentDate.filter(e => e.id !== this.dataEditFormJournals.id))
            this.handleClosePopup();
        }).catch(() => {
            toast.error('Đã có lỗi xảy ra!')
        })
    }
}