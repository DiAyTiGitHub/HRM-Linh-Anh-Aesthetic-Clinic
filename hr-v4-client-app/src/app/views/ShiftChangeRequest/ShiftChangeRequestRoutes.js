import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const ShiftChangeRequestIndex = EgretLoadable({
  loader: () => import("./ShiftChangeRequestIndex"),
});

const ViewComponent = ShiftChangeRequestIndex;

const ShiftChangeRequestRoutes = [
  {
    path: ConstantList.ROOT_PATH + "shift-change-request",
    exact: true,
    component: ViewComponent
  },
];

export default ShiftChangeRequestRoutes;
