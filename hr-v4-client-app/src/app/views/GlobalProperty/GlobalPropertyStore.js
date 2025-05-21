import { makeAutoObservable } from "mobx";
import { deleteGlobalProperty, getAllGlobalProperty, getGlobalProperty, saveGlobalProperty, } from "./GlobalPropertyService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

const dataDefaultForm = {
  property: null,
  propertyName: null,
  propertyValue: null,
  description: null,
  dataTypeName: null,
}

export default class GlobalPropertyStore {
  globalPropertyList = [];
  openPopupForm = false;
  dataForm = dataDefaultForm;
  openPopupConfirmDelete = false;
  loadingInitial = false;

  constructor() {
    makeAutoObservable(this);
  }

  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  loadAllGlobalProperty = () => {
    getAllGlobalProperty().then((response) => {
      this.globalPropertyList = response.data;
    }).catch(() => {
      toast.error('Thất bại!', "Đã có lỗi xảy ra!")
    })
  }

  handleOpenPopupForm = (property) => {
    if (property) {
      getGlobalProperty(property).then((response) => {
        this.dataForm = response.data
      }).catch(() => {
        toast.error('Thất bại!', 'Đã có lỗi xảy ra!')
      })
    } else {
      this.dataForm = dataDefaultForm;
    }
    this.openPopupForm = true;
  }

  handleSubmitFormOpenPopup = (values, initialValue) => {
    saveGlobalProperty(values, initialValue.property).then(() => {
      toast.success('Thành Công!', 'Thêm Property thành công!')
      this.handleClosePopup();
      this.loadAllGlobalProperty()
    }).catch(() => {
      toast.error('Thất bại!', 'Đã có lỗi xảy ra!')
    })
  }

  handleOpenPopupConfirmDelete = (values) => {
    this.dataForm = values;
    this.openPopupConfirmDelete = true;
  }

  handleConfirmDelete = () => {
    deleteGlobalProperty(this.dataForm.property).then(() => {
      toast.success('Thàng công!', 'Xóa thành công!');
      this.loadAllGlobalProperty()
    }).catch(() => {
      toast.error('Thất bại', 'Đã có lỗi xảy ra!')
    })
    this.handleClosePopup();
  }

  handleClosePopup = () => {
    this.openPopupForm = false;
    this.openPopupConfirmDelete = false;
  }
}
