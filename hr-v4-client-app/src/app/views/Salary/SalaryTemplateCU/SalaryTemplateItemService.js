import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/salary-template-item";

export const chooseTemplateItems = (obj) => {
    const url = API_PATH + "/choose-template-items";
    return axios.post(url, obj);
};

export const getListSalaryTemplateItem = (obj) => {
    const url = API_PATH + "/list";
    return axios.post(url, obj);
};

export const saveOrUpdateWithItemConfig = (obj) => {
    const url = API_PATH + "/save-with-item-config";
    return axios.post(url, obj);
};