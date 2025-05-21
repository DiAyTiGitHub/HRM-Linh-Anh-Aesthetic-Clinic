import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const ConfirmOvertimeIndex = EgretLoadable({
  loader: () => import("./ConfirmOvertimeIndex"),
});
const ViewComponent = ConfirmOvertimeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "confirm-overtime",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
