import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useStore} from "app/stores";
import {Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import {useTranslation} from "react-i18next";
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import DoneAllIcon from '@material-ui/icons/DoneAll';
import ThumbDownIcon from '@material-ui/icons/ThumbDown';
import TabsComponent from "app/common/Tab/TabComponent";
import ExamCandidateList from "./ExamCandidateList";
import ExamCandidateIndexToolbar from "./ExamCandidateIndexToolbar";
import CloseIcon from '@material-ui/icons/Close';
import HourglassEmptyIcon from '@material-ui/icons/HourglassEmpty';
import PeopleIcon from '@material-ui/icons/People';
import ExamCandidateConfirmRejectPopup from "./ExamCandidatePopup/ExamCandidateConfirmRejectPopup";
import ExamCandidateConfirmPassPopup from "./ExamCandidatePopup/ExamCandidateConfirmPassPopup";
import ExamCandidateConfirmFailPopup from "./ExamCandidatePopup/ExamCandidateConfirmFailPopup";
import ExamCandidateConfirmResetPopup from "./ExamCandidatePopup/ExamCandidateConfirmResetPopup";

function ExamCandidateIndex() {
    const {examCandidateStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingExamCandidates,
        searchObject,
        resetStore,
        handleChangeExamStatus,
        setPageIndex,
        handleSelectListChosen,
        openRejectPopup,
        openResetPopup,
        openFailPopup,
        openPassPopup
    } = examCandidateStore;

    useEffect(() => {
        pagingExamCandidates();

        return resetStore;
    }, []);


    async function handleChangeTabIndex(tabIndex) {
        handleChangeExamStatus(tabIndex);
        handleSelectListChosen([]);
        await setPageIndex(1);
    }

    const tabList = [
        {icon: <GroupWorkIcon fontSize="small"/>, label: "Tất cả"},
        {icon: <PeopleIcon fontSize="small"/>, label: "Chưa dự tuyển"},
        {icon: <HourglassEmptyIcon fontSize="small"/>, label: "Đang dự tuyển"},
        {icon: <DoneAllIcon fontSize="small"/>, label: "Đạt"},
        {icon: <CloseIcon fontSize="small"/>, label: "Không đạt"},
        {icon: <ThumbDownIcon fontSize="small"/>, label: "Đã từ chối"},
    ];

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb routeSegments={[{name: "Tuyển dụng"}, {name: "Ứng viên thi tuyển"}]}/>
            </div>

            <Grid container spacing={2}>
                <Grid item xs={12} className="index-card">
                    <ExamCandidateIndexToolbar/>
                </Grid>

                <Grid item xs={12} className="index-card">
                    <TabsComponent
                        value={searchObject?.examStatus}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />

                    <ExamCandidateList/>
                </Grid>
            </Grid>

            {openRejectPopup && (
                <ExamCandidateConfirmRejectPopup/>
            )}

            {openResetPopup && (
                <ExamCandidateConfirmResetPopup/>
            )}

            {openFailPopup && (
                <ExamCandidateConfirmFailPopup/>
            )}

            {openPassPopup && (
                <ExamCandidateConfirmPassPopup/>
            )}
        </div>
    );
}

export default memo(observer(ExamCandidateIndex));