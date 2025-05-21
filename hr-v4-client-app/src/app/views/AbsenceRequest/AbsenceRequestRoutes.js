import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const AbsenceRequestIndex = EgretLoadable({
  loader: () => import("./AbsenceRequestIndex"),
});

const ViewComponent = AbsenceRequestIndex;

const AbsenceRequestRoutes = [
  {
    path: ConstantList.ROOT_PATH + "category/absence-request",
    exact: true,
    component: ViewComponent
  },
];

export default AbsenceRequestRoutes;
