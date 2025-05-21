import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "insurance/insurance_type",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./InsuranceType/InsuranceTypeIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "insurance/insurance_region",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./InsuranceRegion/InsuranceRegionIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "insurance/insregion_adminunit",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./InsregionAdminunit/InsregionAdminunitIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "insurance/salary_insurance_region",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./SalaryInsuranceRegion/SalaryInsuranceRegionIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "insurance/insurance_rate",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./InsuranceRate/InsuranceRateIndex"),
      }),
  },
];

export default Routes;
