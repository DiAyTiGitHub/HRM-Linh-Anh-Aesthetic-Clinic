import { makeAutoObservable } from "mobx";
import { PageResponse } from "app/common/Model/Shared";
import { SearchObject } from "app/common/Model/SearchObject/SearchObject";
import { toast } from "react-toastify";
import i18n from "i18n";
import {
  deleteAsset,
  downloadAssetTemplate,
  getAsset,
  getAssetByStaff as getAssetByStaffService,
  importAsset,
  pagingAsset,
  returnAsset,
  saveAsset,
  transferAsset,
} from "../AssetManagementService";
import { Asset } from "app/common/Model/Assets";
import { saveAs } from "file-saver";

export default class AssetStore {
  pageAsset = new PageResponse ();
  searchAsset = new SearchObject ();

  selectedAsset = null;
  selectedDelete = null;
  listAsset = [];

  selectedTransfer = null;
  selectedReturn = null;

  constructor () {
    makeAutoObservable (this);
  }

  pagingAsset = async () => {
    try {
      const res = await pagingAsset (this.searchAsset);
      if (res.data) {
        this.pageAsset = res.data;
      }
    } catch (e) {
      console.error (e);
    }
  };

  setListAsset = (list) => {
    this.listAsset = list;
  };

  getAssetByStaff = (staffId) => {
    return getAssetByStaffService (staffId)
        .then ((result) => {
          this.listAsset = result?.data;
          return result?.data;
        })
        .catch ((err) => {
          toast.error ("Có lỗi xảy ra");
        });
  };

  handleConfirmDelete = (id) => {
    if (id) {
      return deleteAsset (id)
          .then ((result) => {
            toast.success ("Xóa thành công");
          })
          .catch ((err) => {
            toast.error ("Xóa thất bại");
          });
    }
  };

  handleClose = () => {
    this.selectedReturn = null;
  };

  handleChangeFormSearch = (searchAsset) => {
    this.searchAsset = SearchObject.checkSearchObject (this.searchAsset, searchAsset);
    this.pagingAsset ();
  };

  openFormAsset = async (assetId) => {
    let asset = null;
    if (assetId) {
      asset = (await getAsset (assetId))?.data;
      if (asset.startDate) {
        asset.startDate = new Date (asset.startDate);
      }

      if (asset.endDate) {
        asset.endDate = new Date (asset.endDate);
      }
    }

    if (!asset) {
      asset = new Asset ();
    }

    this.selectedAsset = asset;
  };

  onSaveAsset = async (asset) => {
    try {
      const res = await saveAsset (asset);
      if (!res?.data) {
        throw new Error ();
      }

      toast.success (i18n.t ("toast.update_success"));
      this.onClosePopup ();
      this.handleChangeFormSearch (this.searchAsset);
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  onOpenDeleteAsset = (assetId) => (this.selectedDelete = assetId);

  onConfirmDeleteAsset = async () => {
    try {
      const res = await deleteAsset (this.selectedDelete);
      if (!res?.data) {
        throw new Error ();
      }

      toast.success (i18n.t ("toast.update_success"));
      this.onClosePopup ();
      this.handleChangeFormSearch (this.searchAsset);
    } catch (error) {
      toast.warning (i18n.t ("toast.error"));
    }
  };

  onClosePopup = () => {
    this.selectedAsset = null;
    this.selectedDelete = null;
  };

  onOpenTransferAsset = (rowData) => {
    this.selectedTransfer = {
      asset:rowData,
      staff:null,
      startDate:null,
    };
  };

  onOpenReturnAsset = (id) => {
    this.selectedReturn = id;
  };

  onCloseTransferPopup = () => {
    this.selectedTransfer = null;
  };

  onConfirmReturnAsset = async (id = this.selectedReturn) => {
    try {
      await returnAsset (id);
      this.selectedReturn = null;
      this.pagingAsset ();
    } catch (error) {
      console.error (error);
    }
  };
  onSaveTransfer = async (values) => {
    try {
      const transferData = {
        asset:{
          id:values?.asset?.id,
        },
        staff:{
          id:values?.staff?.id,
        },
        startDate:values?.startDate,
      };

      const res = await transferAsset (transferData);
      if (!res?.data) {
        throw new Error ();
      }

      toast.success (i18n.t ("toast.update_success"));
      this.onCloseTransferPopup ();
      this.handleChangeFormSearch (this.searchAsset);
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };
  resetStore = () => {
    this.pageAsset = new PageResponse ();
    this.searchAsset = new SearchObject ();
    this.onClosePopup ();
  };
  uploadFileExcel = async (event) => {
    const fileInput = event.target; // Lưu lại trước
    const file = fileInput.files[0];
    let message = "Nhập excel thất bại";

    try {
      await importAsset (file);
      toast.success ("Nhập excel thành công");
      this.pagingAsset ();
    } catch (error) {
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'string') {
          message = data;
        } else if (data.message) {
          message = data.message;
        }
      }
      toast.error (message);
    } finally {
      this.handleClose ();
      fileInput.value = null;
    }
  };


  handleDownloadAssetTemplate = async () => {
    try {
      const res = await downloadAssetTemplate ();
      let blob = new Blob ([res.data], {
        type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      saveAs (blob, "Mẫu import công cụ, dụng cụ.xlsx");
      toast.success ("Đã tải mẫu import công cụ, dụng cụ thành công");
    } catch (error) {
      toast.error ("Tải mẫu import công cụ, dụng cụ thất bại");
      console.error (error);
    }
  };
}