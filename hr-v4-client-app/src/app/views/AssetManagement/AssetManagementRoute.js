import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const AssetManagementRoutes = [
    {
        path: ConstantList.ROOT_PATH + "category/asset-management/:assetType",
        exact: true,
        component: EgretLoadable({ loader: () => import("./AssetManagementIndex") }),
    },
    {
        path: ConstantList.ROOT_PATH + "category/asset",
        exact: true,
        component: EgretLoadable({ loader: () => import("./Asset/AssetIndex") }),
    },
];

export default AssetManagementRoutes;
