import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./StaffSocialInsuranceIndex"),
});

const ViewComponent = RenderScreen;

const StaffSocialInsuranceRoutes = [
  {
    path: ConstantList.ROOT_PATH + "insurance/staff-social-insurance",
    exact: true,
    component: ViewComponent
  },
]; 

export default StaffSocialInsuranceRoutes;
