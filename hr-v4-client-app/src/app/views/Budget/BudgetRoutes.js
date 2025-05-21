import { EgretLoadable } from "egret";

import ConstantList from "app/appConfig";

const ViewComponent = EgretLoadable({ loader: () => import("./Budget/BudgetIndex") });
const CategoryComponent = EgretLoadable({ loader: () => import("./BudgetCategory/BudgetCategoryIndex") });
const ReportComponent = EgretLoadable({ loader: () => import("./Report/ReportDashBoardIndex") });

const BudgetRoutes = [
  {
    path: ConstantList.ROOT_PATH + "budget/budget",
    exact: true,
    component: ViewComponent,
  },
  {
    path:ConstantList.ROOT_PATH + "budget/budget-category",
    exact: true,
    component: CategoryComponent,
  },
  {
    path:ConstantList.ROOT_PATH + "budget/report",
    exact: true,
    component: ReportComponent,
  }
]

export default BudgetRoutes;
