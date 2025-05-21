import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/degree/:degreeType",
    exact: true,
    component: EgretLoadable({ loader: () => import("./PoliticaltheoryLevelIndex"), }),
  },
];

export default Routes;
