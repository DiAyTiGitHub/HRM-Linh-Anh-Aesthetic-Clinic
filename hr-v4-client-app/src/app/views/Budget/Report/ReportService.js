import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH = ConstantList.API_ENPOINT + "/api/budget";

export const reportByMonthBudget = (dto) => {
  const url = `${API_PATH}/summary`;
  return axios.post(url, dto);
};

export const reportByYearBudget = (dto) => {
  const url = `${API_PATH}/summary-year`;
  return axios.post(url, dto);
};

