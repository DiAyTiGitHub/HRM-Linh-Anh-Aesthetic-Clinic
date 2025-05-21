import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./LeaveTypeIndex"),
});

const ViewComponent = RenderScreen;

const LeaveTypeRoutes = [{
    path: ConstantList.ROOT_PATH + "leave-type",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER","HR_COMPENSATION_BENEFIT"],
},];

export default LeaveTypeRoutes;
