import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const TrainingBaseIndex = EgretLoadable({
  loader: () => import("./TrainingBaseIndex"),
});
const ViewComponent = TrainingBaseIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/educationalInstitution",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
