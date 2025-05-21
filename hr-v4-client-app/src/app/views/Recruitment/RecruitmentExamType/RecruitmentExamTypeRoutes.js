import { EgretLoadable } from "egret";
import ConstantList from "../../../appConfig";

const RecruitmentExamTypeRoutes = [
  {
    path: ConstantList.ROOT_PATH + "category/exam-category",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./RecruitmentExamTypeIndex"),
      }),
  }
];

export default RecruitmentExamTypeRoutes;
