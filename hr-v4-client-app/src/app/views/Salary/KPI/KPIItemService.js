import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/kpi-item";

export const pagingKPIItem = (dto) => {
    const url = `${API_PATH}/page`;
    return axios.post(url, dto);
};
