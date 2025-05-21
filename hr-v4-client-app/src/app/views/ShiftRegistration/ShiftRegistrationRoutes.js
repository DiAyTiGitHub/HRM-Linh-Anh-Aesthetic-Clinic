import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./ShiftRegistrationIndex"),
});

const ViewComponent = RenderScreen;

const ShiftRegistrationRoutes = [
  {
    path: ConstantList.ROOT_PATH + "shift-registration",
    exact: true,
    component: ViewComponent,
  },
];

export default ShiftRegistrationRoutes;
