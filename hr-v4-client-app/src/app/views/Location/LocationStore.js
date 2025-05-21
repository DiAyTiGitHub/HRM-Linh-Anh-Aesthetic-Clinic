import { makeAutoObservable } from "mobx";
import {
  pagingLocations,
  getLocation,
  createLocation,
  editLocation,
  deleteLocation,
  checkCode,
} from "./LocationService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class LocationStore {
  locationList = [];
  selectedLocation = null;
  selectedLocationList = [];
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
      let data = await pagingLocations(searchObject);
      this.locationList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
      this.setLoadingInitial(false);
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
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

  handleEditLocation = (id) => {
    this.getLocation(id).then(() => {
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
    this.getLocation(id).then(() => {
      this.shouldOpenConfirmationDialog = true;
    });
  };

  handleDeleteList = () => {
    this.shouldOpenConfirmationDeleteListDialog = true;
  };

  handleConfirmDelete = async () => {
    try {
      await deleteLocation(this.selectedLocation.id);
      toast.success(i18n.t("toast.delete_success"));
      this.handleClose();
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    let listAlert = [];
    for (var i = 0; i < this.selectedLocationList.length; i++) {
      try {
        await deleteLocation(this.selectedLocationList[i].id);
      } catch (error) {
        listAlert.push(this.selectedLocationList[i].name);
        console.log(error);
        console.log(listAlert.toString());
        toast.warning(i18n.t("toast.error"));
      }
    }
    this.handleClose();
    toast.success(i18n.t("toast.delete_success"));
  };

  getLocation = async (id) => {
    if (id != null) {
      try {
        let data = await getLocation(id);
        this.handleSelectLocation(data.data);
      } catch (error) {
        console.log(error);
        toast.warning(i18n.t("toast.error"));
      }
    } else {
      this.handleSelectLocation(null);
    }
  };

  handleSelectLocation = (location) => {
    this.selectedLocation = location;
  };

  handleSelectListLocation = (locations) => {
    this.selectedLocationList = locations;
  };

  createLocation = async (location) => {
    try {
      let response = await checkCode(location.id, location.code);
      if (response.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await createLocation(location);
        toast.success(i18n.t("toast.add_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  editLocation = async (location) => {
    try {
      let response = await checkCode(location.id, location.code);
      if (response.data) {
        toast.warning(i18n.t("toast.duplicate_code"));
      } else {
        await editLocation(location);
        toast.success(i18n.t("toast.update_success"));
        this.handleClose();
      }
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };
}
