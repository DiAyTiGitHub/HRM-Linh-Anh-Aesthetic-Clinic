import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
import { withTranslation } from 'react-i18next';
const RoleIndex = EgretLoadable({
  loader: () => import("./RoleIndex")
});
const ViewComponent = withTranslation()(RoleIndex);

const RoleRoutes = [
  {
    path: ConstantList.ROOT_PATH + "administration/roles",
    exact: true,
    component: ViewComponent
  }
];

export default RoleRoutes;
