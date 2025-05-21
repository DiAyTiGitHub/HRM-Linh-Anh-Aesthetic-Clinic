import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const TitleConferredIndex = EgretLoadable({
  loader: () => import("./TitleConferredIndex"),
});
const ViewComponent = TitleConferredIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/titleConferred",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
