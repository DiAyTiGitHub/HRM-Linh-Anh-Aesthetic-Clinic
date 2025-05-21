import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const OtherLanguageIndex = EgretLoadable({
  loader: () => import("./OtherLanguageIndex"),
});
const ViewComponent = OtherLanguageIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/otherLanguage",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
