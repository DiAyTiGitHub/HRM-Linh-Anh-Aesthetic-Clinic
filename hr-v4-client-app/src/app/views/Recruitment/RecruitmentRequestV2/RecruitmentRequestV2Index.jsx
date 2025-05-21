import {Grid} from "@material-ui/core";
import {DoneAll, GroupWork, ThumbDown, TransferWithinAStationOutlined} from "@material-ui/icons";
import CheckBoxIcon from "@material-ui/icons/CheckBox";
import CloseIcon from "@material-ui/icons/Close";
import FlightTakeoffIcon from "@material-ui/icons/FlightTakeoff";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import {useStore} from "app/stores";
import {observer} from "mobx-react";
import React, {memo, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import ChoicePersonInChargePopup from "./ChoicePersonInChargePopup";
import RecruitmentRequestListUpdatePopup from "./RecruitmentRequestListUpdatePopup";
import RecruitmentRequestV2Form from "./RecruitmentRequestV2Form";
import RecruitmentRequestV2List from "./RecruitmentRequestV2List";
import RecruitmentRequestV2IndexToolbar from "./RecruitmentRequestV2Toolbar";
import RecruitmentPlanV2Form from "../RecruitmentPlanV2/RecruitmentPlanV2Form";

function RecruitmentRequestV2Index() {
    const {recruitmentRequestStore, hrRoleUtilsStore, recruitmentPlanStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingRecruitmentRequest,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        handleChangePagingStatus,
        openCreateEditPopup,
        setPageIndex,
        handleSelectListDelete,
        openConfirmUpdateStatusPopup,
        openChoicePersonInChargePopup,
        openConfirmDialog,
        handleOpenConfirmDialog,
        openViewPopup,
        handleIsNeedCheck,
        payload,
        saveRecruitmentRequest,
        handleSavePayload
    } = recruitmentRequestStore;

    const [currentTabIndex, setCurrentTabIndex] = useState(0);

    const {openCreateEditPopup: openCreateEditRecruitmentPlanPopup} = recruitmentPlanStore;
    const {checkRoleManageHCNS, hasRoleManageHCNS} = hrRoleUtilsStore;
    useEffect(() => {
        pagingRecruitmentRequest();
        checkRoleManageHCNS();
        return resetStore;
    }, []);

    async function handleChangeTabIndex(tabIndex) {
        setCurrentTabIndex(tabIndex);
        let currentTabObject = tabList[tabIndex];
        let status = currentTabObject.status;
        handleChangePagingStatus(status);
        handleSelectListDelete([]);
        await setPageIndex(1);
    }

    // 👉 Tạo danh sách tab động theo quyền
    const tabList = [
        {icon: <GroupWork fontSize='small'/>, label: "Tất cả", status: null},
        {icon: <CloseIcon fontSize='small'/>, label: "Chưa phê duyệt", status: 0},
        {icon: <TransferWithinAStationOutlined fontSize='small'/>, label: "Đã gửi", status: 1},
        {icon: <DoneAll fontSize='small'/>, label: "Đã phê duyệt", status: 2},
        {icon: <ThumbDown fontSize='small'/>, label: "Đã từ chối", status: 3},
        ...(hasRoleManageHCNS ? [{icon: <CheckBoxIcon fontSize='small'/>, label: "Đã hoàn thành", status: 4}] : []),
        {icon: <FlightTakeoffIcon fontSize='small'/>, label: "Bắt đầu tuyển dụng", status: 5},
    ];
    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        {name: t("navigation.recruitment.title")},
                        {name: t("navigation.recruitment.request")},
                    ]}
                />
            </div>

            <Grid container spacing={2}>
                <Grid item xs={12} className='index-card'>
                    <RecruitmentRequestV2IndexToolbar/>
                </Grid>

                <Grid item xs={12} className='index-card'>
                    <TabsComponent
                        value={currentTabIndex}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />

                    <RecruitmentRequestV2List/>
                </Grid>
            </Grid>

            {openChoicePersonInChargePopup && <ChoicePersonInChargePopup/>}

            {openCreateEditPopup && <RecruitmentRequestV2Form/>}
            {openViewPopup && <RecruitmentRequestV2Form readOnly={true}/>}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}

            {openConfirmDeleteListPopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteListPopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}

            {openConfirmUpdateStatusPopup && <RecruitmentRequestListUpdatePopup/>}

            {openCreateEditRecruitmentPlanPopup && <RecruitmentPlanV2Form/>}
            {openConfirmDialog && (
                <GlobitsConfirmationDialog
                    open={openConfirmDialog}
                    onConfirmDialogClose={() => {
                        handleSavePayload(null)
                        handleOpenConfirmDialog(false)
                    }}
                    onYesClick={async () => {
                        await saveRecruitmentRequest(payload)
                        handleIsNeedCheck(true)
                    }}
                    title={t("Cảnh báo")}
                    text={t("Số lượng vị trí còn trống trong định biên nhỏ hơn số lượng Y/c tuyển dụng")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </div>
    );
}

export default memo(observer(RecruitmentRequestV2Index));
