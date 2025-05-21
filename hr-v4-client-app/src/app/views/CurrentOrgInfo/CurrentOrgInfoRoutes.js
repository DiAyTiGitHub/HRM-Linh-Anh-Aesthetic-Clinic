import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const CurrentOrgInfoIndex = EgretLoadable({
  loader: () => import("./CurrentOrgInfoIndex"),
});

const ViewScreen = CurrentOrgInfoIndex;

const CurrentOrgInfoRoutes = [
  {
    path: ConstantList.ROOT_PATH + "organization/company-info",
    exact: true,
    component: ViewScreen,
  },
];

export default CurrentOrgInfoRoutes;
