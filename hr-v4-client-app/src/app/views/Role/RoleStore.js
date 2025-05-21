import { makeAutoObservable } from "mobx";
import { pagingRoles } from "./RoleService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class RoleStore {
  roleList = [];
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  text = "";
  loadingInitial = false;

  constructor() {
    makeAutoObservable(this);
  }

  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  updatePageData = (item) => {
    var searchObject = {};
    searchObject.text = this.text;
    searchObject.pageIndex = this.page;
    searchObject.pageSize = this.rowsPerPage;
    if (item != null) {
      this.page = 1;
      this.text = item.text;
      this.search(searchObject);
    } else {
      this.search(searchObject);
    }
  };

  search = async (searchObject) => {
    this.loadingInitial = true;
    try {
      let data = await pagingRoles(
        searchObject.pageIndex,
        searchObject.pageSize
      );
      this.roleList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;

      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      this.setLoadingInitial(false);
    }
  };

  setPage = (page) => {
    this.page = page;
    this.updatePageData();
  };

  setRowsPerPage = (event) => {
    this.rowsPerPage = event.target.value;
    this.page = 1;
    this.updatePageData();
  };

  handleChangePage = (event, newPage) => {
    this.setPage(newPage);
  };
}
