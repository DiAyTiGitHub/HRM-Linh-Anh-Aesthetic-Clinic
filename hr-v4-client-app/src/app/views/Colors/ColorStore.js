import { makeAutoObservable } from "mobx";
import {
  pagingColors,
  getColor,
  createColor,
  editColor,
  deleteColor,
} from "./ColorService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class ColorStore {
  colorList = [];
  selectedColor = null;
  selectedColorList = [];
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  keyword = "";
  loadingInitial = false;
  shouldOpenEditorDialog = false;
  shouldOpenConfirmationDialog = false;
  shouldOpenConfirmationDeleteListDialog = false;

  constructor() {
    makeAutoObservable(this);
  }

  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  updatePageData = (item) => {
    if (item != null) {
      this.page = 1;
      this.keyword = item.keyword;
      this.search();
    } else {
      this.search();
    }
  };

  search = async () => {
    this.loadingInitial = true;
    var searchObject = {
      keyword: this.keyword,
      pageIndex: this.page,
      pageSize: this.rowsPerPage,
    };

    try {
      let data = await pagingColors(searchObject);
      this.colorList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning("toast.error");
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

  handleEditColor = (id) => {
    this.getColor(id).then(() => {
      this.shouldOpenEditorDialog = true;
    });
  };

  handleClose = () => {
    this.shouldOpenEditorDialog = false;
    this.shouldOpenConfirmationDialog = false;
    this.shouldOpenConfirmationDeleteListDialog = false;
    this.updatePageData();
  };

  handleDelete = (id) => {
    this.getColor(id).then(() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  handleConfirmDelete = async () => {
    // this.deleteColor(this.selectedColor.id);
    try {
      await deleteColor(this.selectedColor.id);
      toast.success("toast.delete_success");
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning("toast.error");
    }
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedColorList.length; i++) {
      try {
        await deleteColor(this.selectedColorList[i].id);
      } catch (error) {
        listAlert.push(this.selectedColorList[i].name);
        console.log(error);
        console.log(listAlert.toString());
        toast.warning("toast.error");
      }
    }
    this.handleClose();
    toast.success("toast.delete_success");
  };

  getColor = async (id) => {
    if (id != null) {
      try {
        let data = await getColor(id);
        this.handleSelectColor(data.data);
      } catch (error) {
        console.log(error);
        toast.warning("toast.error");
      }
    } else {
      this.handleSelectColor(null);
    }
  };

  handleSelectColor = (color) => {
    this.selectedColor = color;
  };

  handleSelectListColor = (colors) => {
    this.selectedColorList = colors;
    console.log(this.selectedColorList);
  };

  createColor = async (color) => {
    try {
      await createColor(color);
      toast.success("toast.add_success");
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning("toast.error");
    }
  };

  editColor = async (color) => {
    try {
      await editColor(color);
      toast.success("toast.update_success");
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning("toast.error");
    }
  };
}
