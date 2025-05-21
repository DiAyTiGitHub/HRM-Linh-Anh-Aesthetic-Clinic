import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const UserIndex = EgretLoadable({
  loader: () => import("./UserIndex"),
});

const ViewComponentUser = UserIndex;

const UserProfile = EgretLoadable({
  loader: () => import("./UserProfile"),
});

const ViewComponent = UserProfile;

const ChangePassword = EgretLoadable({
  loader: () => import("./ChangePassword"),
});

const ViewComponentChangePassword = ChangePassword;

const UserRoutes = [
  {
    path: ConstantList.ROOT_PATH + "administration/accounts",
    exact: true,
    component: ViewComponentUser,
  },
  {
    path: ConstantList.ROOT_PATH + "setting/user-profile",
    exact: true,
    component: ViewComponent,
  },
  {
    path: ConstantList.ROOT_PATH + "user-change-password",
    exact: true,
    component: ViewComponentChangePassword,
  },
];

export default UserRoutes;
