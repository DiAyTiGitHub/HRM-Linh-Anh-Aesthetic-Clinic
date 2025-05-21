import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const LeaveRequestIndex = EgretLoadable({
  loader: () => import("./LeaveRequestIndex"),
});

const ViewComponent = LeaveRequestIndex;

const LeaveRequestRoutes = [
  {
    path: ConstantList.ROOT_PATH + "category/leave-request",
    exact: true,
    component: ViewComponent
  },
];

export default LeaveRequestRoutes;
