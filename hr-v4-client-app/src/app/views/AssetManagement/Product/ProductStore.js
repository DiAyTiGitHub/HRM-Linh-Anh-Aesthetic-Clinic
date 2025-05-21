import {makeAutoObservable} from "mobx";
import {PageResponse} from "app/common/Model/Shared";
import {SearchObject} from "app/common/Model/SearchObject/SearchObject";
import {autoGenCodeProduct, deleteProduct, getProduct, pagingProduct, saveProduct} from "../AssetManagementService";
import {toast} from "react-toastify";
import i18n from "i18n";
import {Product} from "app/common/Model/Assets";
import {HttpStatus} from "../../../LocalConstants";

export default class ProductStore {
    pageProduct = new PageResponse();
    searchProduct = new SearchObject();

    selectedProduct = null;
    selectedDelete = null;

    constructor() {
        makeAutoObservable(this);
    }

    pagingProduct = async () => {
        try {
            const res = await pagingProduct(this.searchProduct);
            if (res.data) {
                this.pageProduct = res.data;
            }
        } catch (e) {
            console.error(e)
        }
    }

    handleChangeFormSearch = (searchProduct) => {
        // this.searchProduct = SearchObject.checkSearchObject(this.searchProduct, searchProduct);
        this.searchProduct = {...this.searchProduct, ...searchProduct}
        this.pagingProduct();
    }

    openFormProduct = async (productId) => {
        let product = null
        if (productId) {
            product = (await getProduct(productId))?.data
        }

        if (!product) {
            product = new Product();
        }

        this.selectedProduct = product
    }

    onSaveProduct = async (product) => {
        try {
            const res = await saveProduct(product);

            toast.success(i18n.t("toast.update_success"));
            this.onClosePopup();
            this.handleChangeFormSearch(this.searchProduct);
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

    onOpenDeleteProduct = (productId) => this.selectedDelete = productId;

    onConfirmDeleteProduct = async () => {
        try {
            const res = await deleteProduct(this.selectedDelete);
            if (!res?.data) {
                throw new Error()
            }

            toast.success(i18n.t("toast.update_success"));
            this.onClosePopup();
            this.handleChangeFormSearch(this.searchProduct);
        } catch (error) {
            toast.warning(i18n.t("toast.error"));
        }
    }

    onClosePopup = () => {
        this.selectedProduct = null;
        this.selectedDelete = null;
    }

    resetStore = () => {
        this.pageProduct = new PageResponse();
        this.searchProduct = new SearchObject();
        this.onClosePopup();
    }

    autoGenCode = async (configKey) =>{
        const response = await autoGenCodeProduct(configKey)
        if(response.status === HttpStatus.OK){
            return response.data;
        }
    }
}
