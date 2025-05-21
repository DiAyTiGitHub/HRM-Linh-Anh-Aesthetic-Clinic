import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const PositionTitleV2Index = EgretLoadable({
  loader: () => import("./PositionTitleV2Index"),
});
//const ViewComponent = PositionTitleV2Index;
const GroupPositionTitleV2Index = EgretLoadable({
  loader: () => import("./GroupPositionTitleV2Index"),
});

const PositionTitleV2Routes = [
  {
    path: ConstantList.ROOT_PATH + "organization/position-title",
    exact: true,
    component: PositionTitleV2Index,
  },
  {
    path: ConstantList.ROOT_PATH + "organization/group-position-title",
    exact: true,
    component: GroupPositionTitleV2Index,
  },
];

export default PositionTitleV2Routes;
