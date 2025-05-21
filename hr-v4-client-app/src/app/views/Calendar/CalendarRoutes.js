import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const CalendarIndex = EgretLoadable({
  loader: () => import("./CalendarIndex"),
});
const ViewComponent = CalendarIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "calendar",
    component: ViewComponent,
  },
];

export default Routes;
