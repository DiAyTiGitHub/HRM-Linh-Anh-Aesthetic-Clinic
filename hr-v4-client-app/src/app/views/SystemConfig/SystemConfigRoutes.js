import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./SystemConfigIndex"),
});

const ViewComponent = RenderScreen;

const SystemConfigRoutes = [{
    path: ConstantList.ROOT_PATH + "category/system-config",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
},];

export default SystemConfigRoutes;
