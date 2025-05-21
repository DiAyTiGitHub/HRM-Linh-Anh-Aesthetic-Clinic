import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/journal";

export const getAllJournalCalendar = () => {
    return axios.post(API_PATH + "/paging-journals", { pageIndex: 1, pageSize: 1000 })
}

export const getJournals = (id) => {
    return axios.get(API_PATH + "/" + id)
}

export const saveJournals = (obj) => {
    if (obj.id) {
        return axios.put(API_PATH + "/" + obj.id, obj)
    } else {
        return axios.post(API_PATH, obj)
    }
}

export const deleteJournals = (idJournals) => {
    return axios.delete(API_PATH + "/" + idJournals)
}

export const getJournalsByMonth = (obj) => {
    return axios.post(API_PATH + "/find-list-journal", obj)
}