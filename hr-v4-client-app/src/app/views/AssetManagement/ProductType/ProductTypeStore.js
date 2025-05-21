import {makeAutoObservable} from "mobx";
import {PageResponse} from "app/common/Model/Shared";
import {SearchObject} from "app/common/Model/SearchObject/SearchObject";
import {deleteProductType, getProductType, pagingProductType, saveProductType,autoGenCode} from "../AssetManagementService";
import {ProductType} from "app/common/Model/Assets";
import {toast} from "react-toastify";
import i18n from "i18n";
import {HttpStatus} from "../../../LocalConstants";

export default class ProductTypeStore {
    pageProductType = new PageResponse();
    searchProductType = new SearchObject();

    selectedProductType = null;
    selectedDelete = null;

    constructor() {
        makeAutoObservable(this);
    }

    pagingProductType = async () => {
        try {
            const res = await pagingProductType(this.searchProductType);
            if (res.data) {
                this.pageProductType = res.data;
            }
        } catch (e) {
            console.error(e)
        }
    }

    handleChangeFormSearch = async (searchProductType) => {
        // this.searchProductType = SearchObject.checkSearchObject(this.searchProductType, searchProductType);
        this.searchProductType = {...this.searchProductType, ...searchProductType}
        await this.pagingProductType();
    }

    openFormProductType = async (productTypeId) => {
        let productType = null
        if (productTypeId) {
            productType = (await getProductType(productTypeId))?.data
        }

        if (!productType) {
            productType = new ProductType();
        }

        this.selectedProductType = productType
    }

    onSaveProductType = async (productType) => {
        try {
            const res = await saveProductType(productType);
            if (!res?.data) {
                throw new Error()
            }

            toast.success(i18n.t("toast.update_success"));
            this.onClosePopup();
            this.handleChangeFormSearch(this.searchProductType);
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error(i18n.t("toast.duplicate_code"), {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    }

    onOpenDeleteProductType = (productTypeId) => this.selectedDelete = productTypeId;

    onConfirmDeleteProductType = async () => {
        try {
            const res = await deleteProductType(this.selectedDelete);
            if (!res?.data) {
                throw new Error()
            }

            toast.success(i18n.t("toast.update_success"));
            this.onClosePopup();
            this.handleChangeFormSearch(this.searchProductType);
        } catch (error) {
            toast.warning(i18n.t("toast.error"));
        }
    }

    onClosePopup = () => {
        this.selectedProductType = null;
        this.selectedDelete = null;
    }

    resetStore = () => {
        this.pageProductType = new PageResponse();
        this.searchProductType = new SearchObject();
        this.onClosePopup();
    }

    autoGenCode = async (configKey) =>{
        const response = await autoGenCode(configKey)
        if(response.status === HttpStatus.OK){
            return response.data;
        }
    }
}