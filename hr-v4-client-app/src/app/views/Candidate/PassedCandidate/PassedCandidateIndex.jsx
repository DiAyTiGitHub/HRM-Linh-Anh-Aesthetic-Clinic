import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useStore} from "app/stores";
import {Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import {useTranslation} from "react-i18next";
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import ThumbDownIcon from '@material-ui/icons/ThumbDown';
import TabsComponent from "app/common/Tab/TabComponent";
import PassedCandidateList from "./PassedCandidateList";
import PassedCandidateIndexToolbar from "./PassedCandidateToolbar";
import CloseIcon from '@material-ui/icons/Close';
import PassedCandidateConfirmReceptPopup from "./PassedCandidatePopup/PassedCandidateConfirmReceptPopup";
import PassedCandidateConfirmRejectPopup from "./PassedCandidatePopup/PassedCandidateConfirmRejectPopup";
import PassedCandidateConfirmResetPopup from "./PassedCandidatePopup/PassedCandidateConfirmResetPopup";
import AddAlarmIcon from '@material-ui/icons/AddAlarm';

function PassedCandidateIndex() {
    const {passedCandidateStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingPassedCandidates,
        searchObject,
        resetStore,
        handleChangePagingStatus,
        setPageIndex,
        handleSelectListChosen,
        openReceptPopup,
        openRejectPopup,
        openResetPopup,
    } = passedCandidateStore;

    useEffect(() => {
        pagingPassedCandidates();

        return resetStore;
    }, []);


    async function handleChangeTabIndex(tabIndex) {
        handleChangePagingStatus(tabIndex);
        handleSelectListChosen([]);
        await setPageIndex(1);
    }

    const tabList = [
        {icon: <GroupWorkIcon fontSize="small"/>, label: "Tất cả"},
        {icon: <CloseIcon fontSize="small"/>, label: "Chưa phân nhận việc"},
        {icon: <AddAlarmIcon fontSize="small"/>, label: "Đã phân nhận việc"},
        {icon: <ThumbDownIcon fontSize="small"/>, label: "Đã từ chối"},
    ];

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb routeSegments={[{name: "Tuyển dụng"}, {name: "Ứng viên trúng tuyển"}]}/>
            </div>

            <Grid container spacing={2}>
                <Grid item xs={12} className="index-card">
                    <PassedCandidateIndexToolbar/>
                </Grid>

                <Grid item xs={12} className="index-card">
                    <TabsComponent
                        value={searchObject?.receptionStatus}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />

                    <PassedCandidateList/>
                </Grid>
            </Grid>

            {openReceptPopup && (
                <PassedCandidateConfirmReceptPopup/>
            )}

            {openRejectPopup && (
                <PassedCandidateConfirmRejectPopup/>
            )}

            {openResetPopup && (
                <PassedCandidateConfirmResetPopup/>
            )}

        </div>
    );
}

export default memo(observer(PassedCandidateIndex));