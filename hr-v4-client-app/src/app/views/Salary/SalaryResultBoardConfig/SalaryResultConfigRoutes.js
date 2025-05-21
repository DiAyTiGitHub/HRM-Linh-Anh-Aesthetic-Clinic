import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryResultBoardConfigIndex = EgretLoadable({
  loader: () => import("./SalaryResultConfigIndex"),
});

const UpdateMode = SalaryResultBoardConfigIndex;

const SalaryResultBoardConfigROIndex = EgretLoadable({
  loader: () => import("./SalaryResultConfigIndex"),
});

const ReadOnlyMode = SalaryResultBoardConfigROIndex;

const SalaryResultConfigRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary-result-board-config/:id",
    exact: true,
    component: UpdateMode,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
  {
    path: ConstantList.ROOT_PATH + "salary-result-board-config-read-only/:id",
    exact: true,
    component: ReadOnlyMode,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default SalaryResultConfigRoutes;
