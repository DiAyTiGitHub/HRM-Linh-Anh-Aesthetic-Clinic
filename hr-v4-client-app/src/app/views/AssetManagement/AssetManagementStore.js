import AssetStore from "./Asset/AssetStore";
import ProductStore from "./Product/ProductStore";
import ProductTypeStore from "./ProductType/ProductTypeStore";

export const AssetManagementStore = {
    productTypeStore: new ProductTypeStore(),
    productStore: new ProductStore(),
    assetStore: new AssetStore()
}