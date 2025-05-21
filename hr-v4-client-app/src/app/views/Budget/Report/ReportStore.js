import { makeAutoObservable, runInAction } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import { reportByMonthBudget, reportByYearBudget } from "./ReportService";

export default class ReportStore {
  reportByMonth = [];
  reportByYear = [];
  selectedBudget = null; // Ngân sách được chọn
  selectedYear = new Date().getFullYear(); // Năm hiện tại
  selectedMonth = new Date().getMonth() + 1; // Tháng hiện tại (tháng bắt đầu từ 0)
  loading = false;

  constructor() {
    makeAutoObservable(this);
  }

  resetBudgetStore = () => {
    this.reportByMonth = [];
    this.reportByYear = [];
    this.selectedYear = new Date().getFullYear();
    this.selectedMonth = new Date().getMonth() + 1;
    this.selectedBudget = null;
  };

  setSelectedYear = (year) => {
    this.selectedYear = year;
  };

  setSelectedMonth = (month) => {
    this.selectedMonth = month;
  };

  setSelectedBudget = (budget) => {
    this.selectedBudget = budget;
  };

  // Hàm lấy báo cáo theo tháng
  fetchReportByMonth = async (searchObj) => {
    console.log("searchObj: ", searchObj);
    if (!searchObj?.budget) {
      toast.error("Please select a budget before fetching the report");
      return;
    }

    this.loading = true;
    try {
      const response = await reportByMonthBudget(searchObj);
      runInAction(() => {
        this.reportByMonth = response.data;
        this.loading = false;
      });
    } catch (error) {
      runInAction(() => {
        this.loading = false;
      });
      toast.error("Failed to fetch monthly report");
      console.error("Error fetching monthly report:", error);
    }
  };

  // Hàm lấy báo cáo theo năm
  fetchReportByYear = async (searchObj) => {
    if (!searchObj?.budget) {
      toast.error("Please select a budget before fetching the report");
      return;
    }

    this.loading = true;
    try {
      const response = await reportByYearBudget(searchObj);
      runInAction(() => {
        this.reportByYear = response.data;
        this.loading = false;
      });
    } catch (error) {
      runInAction(() => {
        this.loading = false;
      });
      toast.error("Failed to fetch yearly report");
      console.error("Error fetching yearly report:", error);
    }
  };
}
