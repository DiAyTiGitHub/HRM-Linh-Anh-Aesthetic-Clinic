import ConstantList from "../../appConfig";
import InterviewPublicLink from "./InterviewPublicLink";
import ThankYouPage from "./ThankYouPage";

const path = ConstantList.ROOT_PATH + "public-router/"
const Routes = [
    {
        path: path + "interview-public-link/:planId",
        exact: true,
        component: () => <InterviewPublicLink/>,
    },
    {
        path: path + "thank-you",
        exact: true,
        component: () => <ThankYouPage/>,
    },
];

export default Routes;
