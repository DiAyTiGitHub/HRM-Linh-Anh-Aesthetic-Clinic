import {EgretLoadable} from "../../../../../egret";
import ConstantList from "../../../../appConfig";

const ContentTemplateIndex = EgretLoadable({
  loader: () => import("./ContentTemplateIndex"),
});
const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/content-template",
    exact: true,
    component: ContentTemplateIndex,
  },
];

export default Routes;
