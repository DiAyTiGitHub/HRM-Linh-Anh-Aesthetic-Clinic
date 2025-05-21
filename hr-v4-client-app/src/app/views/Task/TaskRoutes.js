import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const TaskIndex = EgretLoadable({
  loader: () => import("./TaskIndex"),
});
const ViewComponent = TaskIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "task/kanban/:id",
    component: ViewComponent,
    exact: true,
  },
  {
    path: ConstantList.ROOT_PATH + "task",
    component: ViewComponent,
    exact: true,
  },
];

export default Routes;
