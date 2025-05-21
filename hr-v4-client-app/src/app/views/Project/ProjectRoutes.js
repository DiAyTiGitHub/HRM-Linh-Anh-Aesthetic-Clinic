import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const ProjectIndex = EgretLoadable({
  loader: () => import("./ProjectIndex"),
});

const ProjectDetail = EgretLoadable({
  loader: () => import("./ProjectDetail"),
});

const ViewComponent = ProjectIndex;
const ViewComponentActivity = ProjectDetail;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "timesheet/project",
    exact: true,
    component: ViewComponent,
  },

  {
    path: ConstantList.ROOT_PATH + "timesheet/project/:id",
    exact: true,
    component: ViewComponentActivity,
  }
];

export default Routes;
