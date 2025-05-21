import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const PublicHolidayDateIndex = EgretLoadable({
  loader: () => import("./PublicHolidayDateIndex"),
});
const ViewComponent = PublicHolidayDateIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/public-holiday-date",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
