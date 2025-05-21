import axios from "axios";
import ConstantList from "app/appConfig";

const API_PATH_PRODUCT_TYPE = ConstantList.API_ENPOINT + "/api/product-type";
const API_PATH_PRODUCT = ConstantList.API_ENPOINT + "/api/product";
const API_PATH_ASSET = ConstantList.API_ENPOINT + "/api/asset";

// product type
export const pagingProductType = (obj) => axios.post (API_PATH_PRODUCT_TYPE + "/paging", obj);
export const getProductType = (productTypeId) => axios.get (API_PATH_PRODUCT_TYPE + "/" + productTypeId);
export const saveProductType = (productType) => axios.post (API_PATH_PRODUCT_TYPE + "/save", productType);
export const deleteProductType = (productTypeId) => axios.delete (API_PATH_PRODUCT_TYPE + "/" + productTypeId);

//product
export const pagingProduct = (obj) => axios.post (API_PATH_PRODUCT + "/paging", obj);
export const getProduct = (productId) => axios.get (API_PATH_PRODUCT + "/" + productId);
export const saveProduct = (product) => axios.post (API_PATH_PRODUCT + "/save", product);
export const deleteProduct = (productId) => axios.delete (API_PATH_PRODUCT + "/" + productId);

// asset
export const pagingAsset = (obj) => axios.post (API_PATH_ASSET + "/paging", obj);
export const getAsset = (assetId) => axios.get (API_PATH_ASSET + "/" + assetId);
export const getAssetByStaff = (assetId) => axios.get (API_PATH_ASSET + "/get-by-staff/" + assetId);
export const saveAsset = (asset) => axios.post (API_PATH_ASSET + "/save", asset);
export const transferAsset = (asset) => axios.post (API_PATH_ASSET + "/transfer-asset", asset);

export const returnAsset = (assetId) => axios.get (API_PATH_ASSET + "/return/" + assetId);

export const deleteAsset = (assetId) => axios.delete (API_PATH_ASSET + "/" + assetId);
export const getListAssetByProduct = (productId) => axios.get (API_PATH_ASSET + "/get-by-product/" + productId);

export const importAsset = (file) => {
  let formData = new FormData ();
  formData.append ("uploadfile", file);

  let url = API_PATH_ASSET + "/import-excel-asset";
  return axios ({
    url:url,
    headers:{
      "Content-Type":"multipart/form-data",
      "Accept":"*/*"
    },
    method:"POST",
    data:formData,
  });
}


export const downloadAssetTemplate = () => {
  return axios ({
    method:"post",
    url:`${API_PATH_ASSET}/export-excel-asset-template`,
    responseType:"blob",
  });
}

export const autoGenCode = (configKey) => {
  let url = API_PATH_PRODUCT_TYPE + `/auto-gen-code/${configKey}`;
  return axios.get(url);
};

export const autoGenCodeProduct = (configKey) => {
  let url = API_PATH_PRODUCT + `/auto-gen-code/${configKey}`;
  return axios.get(url);
};