import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./StaffAnnualLeaveHistoryIndex"),
});

const ViewComponent = RenderScreen;

const StaffAnnualLeaveHistoryRoutes = [{
    path: ConstantList.ROOT_PATH + "staff-annual-leave-history",
    exact: true,
    component: ViewComponent,
},];

export default StaffAnnualLeaveHistoryRoutes;
