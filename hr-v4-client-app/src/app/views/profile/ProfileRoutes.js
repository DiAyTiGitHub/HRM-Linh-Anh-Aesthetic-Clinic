import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
import { withTranslation } from "react-i18next";

const UserProfile = EgretLoadable({
  loader: () => import("./ProfileIndex"),
});

const ViewComponent = withTranslation()(UserProfile);

const UserProfileRoutes = [
  {
    path: ConstantList.ROOT_PATH + "profile",
    exact: true,
    component: ViewComponent,
  },
];

export default UserProfileRoutes;
