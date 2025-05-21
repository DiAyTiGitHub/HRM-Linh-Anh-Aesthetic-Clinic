import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const OvertimeRequestIndex = EgretLoadable({
  loader: () => import("./OvertimeRequestIndex"),
});

const ViewComponent = OvertimeRequestIndex;

const OvertimeRequestRoutes = [
  {
    path: ConstantList.ROOT_PATH + "category/overtime-request",
    exact: true,
    component: ViewComponent
  },
];

export default OvertimeRequestRoutes;
